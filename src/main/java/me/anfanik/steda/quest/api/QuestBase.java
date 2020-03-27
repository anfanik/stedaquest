package me.anfanik.steda.quest.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.val;
import me.anfanik.steda.api.utility.ItemBuilder;
import me.anfanik.steda.api.utility.serialization.MapSerializer;
import me.anfanik.steda.quest.StedaQuest;
import me.anfanik.steda.quest.api.state.State;
import me.anfanik.steda.quest.api.state.States;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@RequiredArgsConstructor
public class QuestBase implements Quest {

    @Getter
    private final String id;

    @Getter
    @Setter
    private Display display;

    public QuestBase(String id, Display display) {
        this.id = id;
        this.display = display;
    }

    @Override
    public boolean launch(Player player) {
        val data = getDataHolder().get(player);
        if (data.getState() == States.PENDING) {
            data.setState(States.LAUNCHED);
            return true;
        }
        return false;
    }

    @Override
    public boolean complete(Player player) {
        val data = getDataHolder().get(player);
        if (data.getState() == States.LAUNCHED) {
            data.setState(States.COMPLETED);
            val registry = StedaQuest.get().getRegistry();
            registry.get(this).flatMap(line -> line.next(QuestBase.this))
                    .ifPresent(quest -> {
                        Quest.Data data1 = quest.getDataHolder().get(player);
                        data1.setState(States.PENDING);
                    });
            return true;
        }
        return false;
    }

    @Override
    public boolean giveReward(Player player) {
        val data = getDataHolder().get(player);
        if (data.getState() == States.COMPLETED) {
            invalidate(player);
            return true;
        }
        return false;
    }

    public void invalidate(Player player) {
        getDataHolder().invalidate(player);
    }

    @Getter
    @Setter
    private MenuDelegate menuDelegate = new MenuDelegateImpl();

    @Getter
    @Setter
    private DataHolder<?, ?> dataHolder = new DataHolderImpl(new DataModelImpl());

    @RequiredArgsConstructor
    @Getter
    public static class DisplayImpl implements Display {

        private final String name;
        private final List<String> lore;
        private final List<String> goal;
        private final List<String> reward;

        public DisplayImpl(String name, List<String> lore, String goal, String reward) {
            this.name = name;
            this.lore = lore;
            this.goal = Collections.singletonList(goal);
            this.reward = Collections.singletonList(reward);
        }

    }

    public class MenuDelegateImpl implements MenuDelegate {

        @Override
        public void handleNPCsMenuClick(Player player, State state) {
            if (state == States.PENDING) {
                launch(player);
                player.closeInventory();
            } else if (state == States.COMPLETED) {
                giveReward(player);
                player.closeInventory();
            }
        }

        @Override
        public ItemStack getNPCsMenuIcon(Player player, State state) {
            ItemBuilder<?> builder;
            if (state == States.PENDING) {
                builder = ItemBuilder.fromMaterial(Material.BOOK)
                        .setName("&e" + display.getName())
                        //.appendLore(getDisplay().getLore().toArray(new String[0]))
                        .appendLore(getDisplay().getGoal().toArray(new String[0]))
                        .appendLore("", "&eКлик&f: Взять квест");
            } else if (state == States.LAUNCHED) {
                builder = ItemBuilder.fromMaterial(Material.BOOK)
                        .setName("&6" + display.getName())
                        //.appendLore(getDisplay().getLore().toArray(new String[0]))
                        .appendLore("", "&eЦель:")
                        .appendLore(display.getGoal().toArray(new String[0]))
                        .appendLore("", "&eНаграда:")
                        .appendLore(display.getReward().toArray(new String[0]))
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .addItemFlag(ItemFlag.HIDE_ENCHANTS);
            } else if (state == States.COMPLETED) {
                builder = ItemBuilder.fromMaterial(Material.ENCHANTED_BOOK)
                        .setName("&a" + display.getName())
                        .appendLore("", "&eНаграда:")
                        .appendLore(display.getReward().toArray(new String[0]))
                        .appendLore("", "&eКлик&f: Взять награду");
            } else {
                builder = ItemBuilder.fromMaterial(Material.BARRIER)
                        .setName("&4" + display.getName());
            }
            builder.formatLoreLines(line -> "&f" + line);
            return builder.build();
        }

        @Override
        public void handlePlayersMenuClick(Player player, State state) {

        }

        @Override
        public ItemStack getPlayersMenuIcon(Player player, State state) {
            ItemBuilder<?> builder;
            if (state == States.LAUNCHED) {
                builder = ItemBuilder.fromMaterial(Material.BOOK)
                        .setName("&6" + display.getName())
                        .appendLore(getDisplay().getLore().toArray(new String[0]))
                        .appendLore("", "&eЦель:")
                        .appendLore(display.getGoal().toArray(new String[0]))
                        .appendLore("", "&eНаграда:")
                        .appendLore(display.getReward().toArray(new String[0]))
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .addItemFlag(ItemFlag.HIDE_ENCHANTS);
            } else if (state == States.COMPLETED) {
                builder = ItemBuilder.fromMaterial(Material.ENCHANTED_BOOK)
                        .setName("&a" + display.getName())
                        .appendLore("", "&eНаграда:")
                        .appendLore(display.getReward().toArray(new String[0]))
                        .appendLore("", "&eКлик&f: Взять награду");
            } else {
                builder = ItemBuilder.fromMaterial(Material.BARRIER)
                        .setName("&4" + display.getName());
            }
            builder.formatLoreLines(line -> "&f" + line);
            return builder.build();
        }
    }

    public static class DataImpl implements Data {

        @Getter
        @Setter
        private State state;

    }

    public static class DataModelImpl implements DataModel<Data> {

        private final Map<String, State> states = new HashMap<>();

        public void registerState(State state) {
            states.put(state.getId(), state);
        }

        @Override
        public String serialize(Data data) {
            return "[\"state\"=\"" + data.getState().getId() + "\"]";
        }

        private static final MapSerializer<String, String> dataSerializer = MapSerializer.getSerializer(
                Objects::toString,
                Objects::toString,
                Objects::toString,
                Objects::toString
        );

        @Override
        public Data deserialize(String serialized) {
            return deserialize(MapSerializer.deserialize(serialized, Object::toString, Object::toString)); //TODO DataModelImpl::deserialize with map deserializer from StedaApi\
        }

        protected Data deserialize(Map<String, String> map) {
            Data data = new DataImpl();

            State state;
            val stateRaw = map.getOrDefault("state", States.HIDDEN.getId());
            try {
                state = States.valueOf(stateRaw.toUpperCase());
            } catch (IllegalArgumentException exception) {
                state = states.get(stateRaw);
            }
            if (state == null) {
                state = States.UNKNOWN;
                new IllegalStateException("unknown state").printStackTrace();
            }
            data.setState(state);
            return data;
        }

        @Override
        public Data createInstance(UUID uuid) {
            Data data = new DataImpl();
            data.setState(States.HIDDEN);
            return data;
        }
    }

    public static class DataHolderImpl implements DataHolder<Data, DataModel<Data>> {

        @Getter
        @Setter
        private DataModel<Data> model;

        public DataHolderImpl(DataModel<Data> model) {
            this.model = model;
        }

        private final Map<UUID, Data> cache = new HashMap<>();

        @Override
        public Data get(Player player) {
            return cache.computeIfAbsent(player.getUniqueId(), model::createInstance);
        }

        @Override
        public void set(Player player, Data data) {
            cache.put(player.getUniqueId(), data);
            //TODO: Data saving
        }

        public void invalidate(Player player) {
            val data = cache.remove(player.getUniqueId());
            if (data != null) {
                data.setState(States.HIDDEN);
            }
        }

    }

}
