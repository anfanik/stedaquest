package me.anfanik.steda.quest.api.state.exact;

import me.anfanik.steda.api.utility.ItemBuilder;
import me.anfanik.steda.quest.api.state.QuestStateBase;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class QuestLaunchedState extends QuestStateBase {

    public QuestLaunchedState() {
        super(null);
    }

    @Override
    public boolean complete() {
        //noinspection deprecation
        getQuest().getPlayer().sendTitle("§aКвест выполнен:", "§e" + getQuest().getName());
        getQuest().setState(new QuestCompletedState(getQuest()));
        return true;
    }

    @Override
    public ItemStack getNPCsMenuIcon() {
        return ItemBuilder.fromMaterial(Material.BOOK_AND_QUILL)
                .setName("&6" + getQuest().getName())
                //.appendLore(getDisplay().getLore().toArray(new String[0]))
                .appendLore("", "&eЦель:")
                .appendLore(getQuest().getGoal().toArray(new String[0]))
                .appendLore("", "&eНаграда:")
                .appendLore(getQuest().getReward().toArray(new String[0]))
                .addEnchantment(Enchantment.DURABILITY, 1)
                .addItemFlag(ItemFlag.HIDE_ENCHANTS)
                .build();
    }

    @Override
    public ItemStack getActiveMenuIcon() {
        return getNPCsMenuIcon();
    }

}
