package me.anfanik.steda.quest.api.npc;

import com.comphenix.protocol.wrappers.EnumWrappers;
import lombok.val;
import lombok.var;
import me.anfanik.steda.api.menu.Menu;
import me.anfanik.steda.api.menu.button.MenuButton;
import me.anfanik.steda.api.menu.filling.FillingStrategy;
import me.anfanik.steda.api.utility.ItemBuilder;
import me.anfanik.steda.api.utility.Skin;
import me.anfanik.steda.quest.api.Quest;
import me.anfanik.steda.quest.api.state.exact.QuestHiddenState;
import me.anfanik.steda.quest.api.state.exact.QuestRewardGivenState;
import me.anfanik.steda.quest.api.state.exact.QuestUnknownState;
import me.anfanik.steda.quest.menu_session.QuestMenuSession;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class QuestNPC extends AbstractNPC {

    protected final Map<String, Predicate<Quest>> quests = new HashMap<>();

    public QuestNPC(String name, Skin skin, Location location) {
        super(name, skin, location);
        delegate = new QuestDelegate(chatDelegate, quests);
    }

    public void addQuest(String id, Predicate<Quest> predicate) {
        quests.put(id, predicate);
    }

    public void addQuest(String id) {
        quests.put(id, quest -> {
            val state = quest.getState();
            return !(state instanceof QuestHiddenState || state instanceof QuestRewardGivenState || state instanceof QuestUnknownState);
        });
    }

    public static class QuestDelegate extends AbstractNPC.DelegateImpl {

        private final Map<String, Predicate<Quest>> quests;
        private Menu<QuestMenuSession> menu;
        private ChatDelegate chatDelegate;

        public QuestDelegate(ChatDelegate chatDelegate, Map<String, Predicate<Quest>> quests) {
            this.chatDelegate = chatDelegate;
            this.quests = quests;
            menu = Menu.builder(QuestMenuSession.class)
                    .setSize(9)
                    .setTitleGenerator(session -> "Квесты")
                    .setFillingStrategy(session -> {
                        val builder = new FillingStrategy.ResultBuilder();

                        var slot = 0;
                        for (val quest : session.getResult()) {
                            builder.addButton(new MenuButton(ItemBuilder.fromItem(quest.getNPCsMenuIcon())
                                    .formatLoreLines(line -> "&f" + line)
                                    .build(),
                                    (player1, click, slot1) -> {
                                        player1.closeInventory();
                                        quest.handleNPCsMenuClick();
                                    }), slot);
                            slot++;
                        }

                        return builder.build();
                    })
                    .build();
        }

        @Override
        public void handleInteract(Player player, EnumWrappers.Hand hand) {
            val session = new QuestMenuSession(player, quests);
            if (!session.getResult().isEmpty()) {
                menu.open(session);
            } else {
                chatDelegate.sendMessage(player, "Извини, у меня нет заданий для тебя!");
            }
        }

    }

}