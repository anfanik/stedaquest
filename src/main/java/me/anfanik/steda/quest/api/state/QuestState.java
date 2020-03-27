package me.anfanik.steda.quest.api.state;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import me.anfanik.steda.quest.api.Quest;
import org.bukkit.inventory.ItemStack;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, property="@class")

public interface QuestState {

    Quest getQuest();

    void setQuest(Quest quest);

    boolean launch();

    boolean complete();

    boolean giveReward();

    void handleNPCsMenuClick();

    ItemStack getNPCsMenuIcon();

    void handleActiveMenuClick();

    ItemStack getActiveMenuIcon();

    default void initialize() {};

    default void invalidate() {};

}
