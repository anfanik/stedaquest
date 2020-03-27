package me.anfanik.steda.quest.api;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import me.anfanik.steda.quest.StedaQuest;
import me.anfanik.steda.quest.api.state.QuestState;
import me.anfanik.steda.quest.api.state.exact.QuestLaunchedState;
import me.anfanik.steda.quest.api.state.exact.QuestUnknownState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
public abstract class Quest {

    private String id;

    private Player player;

    private QuestState state = new QuestUnknownState(this);

    private QuestLaunchedState launchedState;

    public Quest(String id, Player player, QuestLaunchedState launchedState) {
        this.id = id;
        this.player = player;
        this.launchedState = launchedState;
        launchedState.setQuest(this);
    }

    public Quest() {
        id = null;
        player = null;
        launchedState = null;
    }

    public void setState(QuestState state) {
        if (this.state instanceof BukkitRunnable) {
            ((BukkitRunnable) this.state).cancel();
        }
        if (this.state instanceof Listener) {
            HandlerList.unregisterAll((Listener) this.state);
        }
        this.state.invalidate();

        state.setQuest(this);
        this.state = state;
        if (state instanceof Listener) {
            Bukkit.getPluginManager().registerEvents((Listener) state, StedaQuest.get().getPlugin());
        }
        state.initialize();
        save();
    }

    public boolean launch() {
        return state.launch();
    }

    public boolean complete() {
        return state.complete();
    }

    public boolean giveReward() {
        return state.giveReward();
    }

    public void handleNPCsMenuClick() {
        state.handleNPCsMenuClick();
    }

    public ItemStack getNPCsMenuIcon() {
        return state.getNPCsMenuIcon();
    }

    public void handleActiveMenuClick() {
        state.handleActiveMenuClick();
    }

    public ItemStack getActiveMenuIcon() {
        return state.getActiveMenuIcon();
    }

    public abstract String getName();

    public abstract Collection<String> getGoal();

    public Collection<String> getReward() {
        return Collections.singleton("&7Отсутствуетц");
    }

    public void save() {
        val storage = QuestStorage.getIfPresent(getPlayer().getUniqueId());
        if (storage != null) {
            storage.save(this);
        }
    }

}
