package me.anfanik.steda.quest.api;

import lombok.Builder;
import me.anfanik.steda.quest.api.state.State;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public interface Quest {

    String getId();

    boolean launch(Player player);

    boolean complete(Player player);

    boolean giveReward(Player player);

    MenuDelegate getMenuDelegate();

    void setMenuDelegate(MenuDelegate delegate);

    DataHolder getDataHolder();

    interface Display {

        String getName();

        List<String> getLore();

        List<String> getGoal();

        List<String> getReward();

        static Builder builder() {
            return new Builder();
        }

        class Builder {

            private String name;
            private List<String> lore;
            private List<String> goal;
            private List<String> reward;

            public Builder name(String name) {
                this.name = name;
                return this;
            }

            public Builder lore(Collection<String> lore) {
                this.lore = new ArrayList<>(lore);
                return this;
            }

            public Builder lore(String... lore) {
                this.lore = Arrays.asList(lore);
                return this;
            }

            public Builder appendLore(String line) {
                if (lore == null) {
                    lore = new ArrayList<>();
                }
                lore.add(line);
                return this;
            }

            public Builder goal(Collection<String> goal) {
                this.goal = new ArrayList<>(goal);
                return this;
            }

            public Builder goal(String... goal) {
                this.goal = Arrays.asList(goal);
                return this;
            }

            public Builder appendGoal(String line) {
                if (goal == null) {
                    goal = new ArrayList<>();
                }
                goal.add(line);
                return this;
            }

            public Builder reward(Collection<String> reward) {
                this.reward = new ArrayList<>(reward);
                return this;
            }

            public Builder reward(String... reward) {
                this.reward = Arrays.asList(reward);
                return this;
            }

            public Builder appendReward(String line) {
                if (reward == null) {
                    reward = new ArrayList<>();
                }
                reward.add(line);
                return this;
            }

            public Display build() {
                return new QuestBase.DisplayImpl(name, lore, goal, reward);
            }

        }

    }

    interface MenuDelegate {

        void handleNPCsMenuClick(Player player, State state);

        ItemStack getNPCsMenuIcon(Player player, State state);

        void handlePlayersMenuClick(Player player, State state);

        ItemStack getPlayersMenuIcon(Player player, State state);

    }

    interface Data {

        State getState();

        void setState(State state);

    }

    interface DataModel<D extends Data> {

        String serialize(D data);

        D deserialize(String serialized);

        D createInstance(UUID uuid);

    }

    interface DataHolder<D extends Data, M extends DataModel<D>> {

        M getModel();

        D get(Player player);

        void set(Player player, D data);

        void invalidate(Player player);

    }

}
