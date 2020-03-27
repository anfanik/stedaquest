package me.anfanik.steda.quest.api.state.exact;

import lombok.NoArgsConstructor;
import me.anfanik.steda.api.utility.ItemBuilder;
import me.anfanik.steda.quest.api.Quest;
import me.anfanik.steda.quest.api.state.QuestStateBase;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@NoArgsConstructor
public class QuestCompletedState extends QuestStateBase {

    public QuestCompletedState(Quest quest) {
        super(quest);
    }

    @Override
    public boolean giveReward() {
        getQuest().setState(new QuestRewardGivenState(getQuest()));
        return true;
    }

    @Override
    public void handleNPCsMenuClick() {
        getQuest().giveReward();
    }

    @Override
    public void handleActiveMenuClick() {
        handleNPCsMenuClick();
    }

    @Override
    public ItemStack getNPCsMenuIcon() {
        return ItemBuilder.fromMaterial(Material.ENCHANTED_BOOK)
                .setName("&a" + getQuest().getName())
                .appendLore("", "&eНаграда:")
                .appendLore(getQuest().getReward().toArray(new String[0]))
                .appendLore("", "&eКлик&f: Взять награду")
                .build();
    }

    @Override
    public ItemStack getActiveMenuIcon() {
        return getNPCsMenuIcon();
    }

}
