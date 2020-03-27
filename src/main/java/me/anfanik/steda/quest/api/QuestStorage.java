package me.anfanik.steda.quest.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.*;
import me.anfanik.database.api.result.Row;
import me.anfanik.steda.quest.StedaQuest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Consumer;

/**
 * Table: create table StedaQuest (uuid varchar(36) not null primary key, quest varchar(32), data text, unique key uuid_quest (uuid, quest)) character set utf8 collate utf8_general_ci;
 */
@RequiredArgsConstructor
public class QuestStorage {

    private static final ObjectMapper mapper = new ObjectMapper(); static {
        var visibilityChecker = mapper.getSerializationConfig().getDefaultVisibilityChecker();

        visibilityChecker = visibilityChecker.withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY);

        mapper.setVisibility(visibilityChecker);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        val module = new SimpleModule();

        module.addSerializer(new StdSerializer<Player>(Player.class) {
            @Override
            @SneakyThrows
            public void serialize(Player player, JsonGenerator generator, SerializerProvider provider) {
                generator.writeStartObject();
                generator.writeStringField("uuid", player.getUniqueId().toString());
                generator.writeEndObject();
            }
        });

        module.addDeserializer(Player.class, new StdDeserializer<Player>(Player.class) {
            @Override
            @SneakyThrows
            public Player deserialize(JsonParser parser, DeserializationContext context) {
                val uuid = UUID.fromString(((JsonNode) parser.getCodec().readTree(parser)).get("uuid").asText());
                return Bukkit.getPlayer(uuid);
            }
        });

        mapper.registerModule(module);

    }

    @Getter
    private final UUID uuid;
    private final Map<String, Quest> quests = new HashMap<>();

    public void add(Quest quest) {
        quests.put(quest.getId(), quest);
    }

    public Quest get(String id) {
        return quests.get(id);
    }

    public void remove(String id) {
        quests.remove(id);
    }

    @SneakyThrows
    public void save(Quest quest) {
        val database = StedaQuest.get().getDatabase();
        val json = mapper.writeValueAsString(quest);
        val update = database.sync().prepareUpdate("insert into StedaQuest (uuid, quest, class, `data`) values (?, ?, ?, ?) on duplicate key update `data` = ?");
        update.execute(uuid.toString(), quest.getId(), quest.getClass().getCanonicalName(), json, json);
    }

    public Collection<Quest> getQuests() {
        return Collections.unmodifiableCollection(quests.values());
    }

    private static final Map<UUID, QuestStorage> cache = new HashMap<>();

    public static QuestStorage get(UUID uuid) {
        return cache.computeIfAbsent(uuid, QuestStorage::load);
    }

    private static QuestStorage load(UUID uuid) {
        val database = StedaQuest.get().getDatabase();
        val select = database.sync().prepareSelect("select * from StedaQuest where uuid = ?");
        val result = select.execute(uuid.toString());

        val storage = new QuestStorage(uuid);
        result.getRows().forEach(new Consumer<Row>() {
            @Override
            @SneakyThrows
            public void accept(Row row) {
                val clazz = Class.forName(row.getString("class"));
                val json = row.getString("data");

                val quest = (Quest) mapper.readValue(json, clazz);
                storage.add(quest);
            }
        });
        return storage;
    }

    public static QuestStorage getIfPresent(UUID uuid) {
        return cache.get(uuid);
    }

    public static void invalidate(UUID uuid) {
        cache.remove(uuid);
    }

    public static Collection<QuestStorage> getCache() {
        return Collections.unmodifiableCollection(cache.values());
    }

}
