package me.anfanik.steda.quest.api.state;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.anfanik.steda.api.utility.ItemBuilder;
import me.anfanik.steda.quest.api.Quest;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@NoArgsConstructor
public class QuestStateBase implements QuestState {

    @Getter
    @JsonIgnore
    private transient Quest quest;

    @Override
    @JsonIgnore
    public void setQuest(Quest quest) {
        this.quest = quest;
    }

    @Override
    public boolean launch() {
        return false;
    }

    @Override
    public boolean complete() {
        return false;
    }

    @Override
    public boolean giveReward() {
        return false;
    }

    @Override
    public void handleNPCsMenuClick() {
    }

    @Override
    public ItemStack getNPCsMenuIcon() {
        return ItemBuilder.fromMaterial(Material.BARRIER)
                .setName("&4" + quest.getName())
                .build();
    }

    @Override
    public void handleActiveMenuClick() {
    }

    @Override
    public ItemStack getActiveMenuIcon() {
        return ItemBuilder.fromMaterial(Material.BARRIER)
                .setName("&4" + quest.getName())
                .build();
    }

}
