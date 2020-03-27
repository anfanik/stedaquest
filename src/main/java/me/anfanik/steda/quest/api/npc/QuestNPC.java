package me.anfanik.steda.quest.api.npc;

import com.comphenix.protocol.wrappers.EnumWrappers;
import lombok.val;
import lombok.var;
import me.anfanik.steda.api.menu.Menu;
import me.anfanik.steda.api.menu.button.MenuButton;
import me.anfanik.steda.api.menu.filling.FillingStrategy;
import me.anfanik.steda.api.utility.Skin;
import me.anfanik.steda.quest.api.Quest;
import me.anfanik.steda.quest.api.state.States;
import me.anfanik.steda.quest.menu_session.QuestMenuSession;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class QuestNPC extends AbstractNPC {

    protected final Map<Quest, Predicate<Quest.Data>> quests = new HashMap<>();

    public QuestNPC(String name, Skin skin, Location location) {
        super(name, skin, location);
        delegate = new QuestDelegate(chatDelegate, quests);
    }

    public void addQuest(Quest quest, Predicate<Quest.Data> predicate) {
        quests.put(quest, predicate);
    }

    public void addQuest(Quest quest) {
        quests.put(quest, data -> data.getState() != States.HIDDEN);
    }

    public Collection<Quest> getQuests() {
        return Collections.unmodifiableCollection(quests.keySet());
    }

    public static class QuestDelegate extends AbstractNPC.DelegateImpl {

        private final Map<Quest, Predicate<Quest.Data>> quests;
        private Menu<QuestMenuSession> menu;
        private ChatDelegate chatDelegate;

        public QuestDelegate(ChatDelegate chatDelegate, Map<Quest, Predicate<Quest.Data>> quests) {
            this.chatDelegate = chatDelegate;
            this.quests = quests;
            menu = Menu.builder(QuestMenuSession.class)
                    .setTitleGenerator(session -> "Квесты")
                    .setFillingStrategy(session -> {
                        val player = session.getPlayer();
                        val builder = new FillingStrategy.ResultBuilder();

                        var slot = 0;
                        for (val quest : session.getResult()) {
                            val data = quest.getDataHolder().get(player);
                            val menuDelegate = quest.getMenuDelegate();
                            builder.addButton(new MenuButton(menuDelegate.getNPCsMenuIcon(player, data.getState()),
                                    (player1, click, slot1) -> {
                                        menuDelegate.handleNPCsMenuClick(player1, data.getState());
                                        session.update();
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