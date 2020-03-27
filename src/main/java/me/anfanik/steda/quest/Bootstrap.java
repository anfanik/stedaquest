package me.anfanik.steda.quest;

import org.bukkit.plugin.java.JavaPlugin;

public class Bootstrap extends JavaPlugin {

    @Override
    public void onEnable() {
        StedaQuest.setPlugin(this);
    }

}