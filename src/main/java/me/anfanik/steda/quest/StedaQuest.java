package me.anfanik.steda.quest;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.val;
import me.anfanik.database.api.Database;
import me.anfanik.steda.api.command.Command;
import me.anfanik.steda.quest.api.QuestStorage;
import me.anfanik.steda.quest.command.QuestCommand;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public class StedaQuest implements Listener {

    private static StedaQuest instance; {
        instance = this;
    }

    public static StedaQuest get() {
        return instance;
    }

    @Setter
    protected static Plugin plugin;

    @Getter
    private final Database database;

    public Plugin getPlugin() {
        return plugin;
    }


    @EventHandler
    public void handleQuit(PlayerQuitEvent event) {
        val player = event.getPlayer();
        QuestStorage.invalidate(player.getUniqueId());
    }

    public static void initialize(Database database) {
        val instance = new StedaQuest(database);
        Bukkit.getPluginManager().registerEvents(instance, plugin);
        if (!Command.get("quest").isPresent()) {
            new QuestCommand();
        }
    }

}
