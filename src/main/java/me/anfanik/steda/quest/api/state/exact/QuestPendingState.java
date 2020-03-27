package me.anfanik.steda.quest.api.state.exact;

import lombok.NoArgsConstructor;
import me.anfanik.steda.api.utility.ItemBuilder;
import me.anfanik.steda.quest.api.Quest;
import me.anfanik.steda.quest.api.state.QuestStateBase;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@NoArgsConstructor
public class QuestPendingState extends QuestStateBase {

    public QuestPendingState(Quest quest) {
        super(quest);
    }

    @Override
    public boolean launch() {
        getQuest().setState(getQuest().getLaunchedState());
        return true;
    }

    @Override
    public void handleNPCsMenuClick() {
        getQuest().launch();
    }

    @Override
    public ItemStack getNPCsMenuIcon() {
        return ItemBuilder.fromMaterial(Material.BOOK_AND_QUILL)
                .setName("&e" + getQuest().getName())
                .appendLore(getQuest().getGoal().toArray(new String[0]))
                .appendLore("", "&eКлик&f: Взять квест")
                .build();
    }

}
