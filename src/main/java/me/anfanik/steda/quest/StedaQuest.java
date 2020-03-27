package me.anfanik.steda.quest;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.anfanik.steda.quest.api.QuestRegistry;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public class StedaQuest {

    private static StedaQuest instance; {
        instance = this;
    }

    public static StedaQuest get() {
        return instance;
    }

    @Setter
    protected static Plugin plugin;
    @Getter
    private final QuestRegistry registry;

    public Plugin getPlugin() {
        return plugin;
    }

    public static void initialize(QuestRegistry registry) {
        new StedaQuest(registry);
    }

}
