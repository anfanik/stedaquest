package me.anfanik.steda.quest.menu_session;

import lombok.val;
import me.anfanik.steda.api.menu.MenuSession;
import me.anfanik.steda.quest.api.Quest;
import me.anfanik.steda.quest.api.QuestStorage;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Predicate;

public class QuestMenuSession extends MenuSession {

    private final Map<String, Predicate<Quest>> quests;
    private List<Quest> result;

    public QuestMenuSession(Player player, Set<Quest> quests, Predicate<Quest> predicate) {
        super(player);
        this.quests = new HashMap<>();
        quests.forEach(quest -> this.quests.put(quest.getId(), predicate));
        refresh();
    }

    public QuestMenuSession(Player player, Map<String, Predicate<Quest>> quests) {
        super(player);
        this.quests = quests;
        refresh();
    }

    @Override
    public void update() {
        super.update();
        refresh();
    }

    public Collection<Quest> refresh() {
        val result = new ArrayList<Quest>();
        val storage = QuestStorage.get(getPlayer().getUniqueId());
        storage.getQuests().stream()
                .filter(quest -> quests.containsKey(quest.getId()))
                .filter(quest -> {
                    val predicate = quests.get(quest.getId());
                    return predicate.test(quest);
                })
                .forEach(result::add);

        this.result = result;
        return result;
    }

    public List<Quest> getResult() {
        return Collections.unmodifiableList(result);
    }

}