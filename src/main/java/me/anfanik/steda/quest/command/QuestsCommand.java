package me.anfanik.steda.quest.command;

import lombok.val;
import lombok.var;
import me.anfanik.steda.api.command.Command;
import me.anfanik.steda.api.command.CommandHandler;
import me.anfanik.steda.api.command.executor.PlayerExecutor;
import me.anfanik.steda.api.menu.Menu;
import me.anfanik.steda.api.menu.button.MenuButton;
import me.anfanik.steda.api.menu.filling.FillingStrategy;
import me.anfanik.steda.quest.api.QuestRegistry;
import me.anfanik.steda.quest.api.state.States;
import me.anfanik.steda.quest.menu_session.QuestMenuSession;

import java.util.stream.Collectors;

public class QuestsCommand extends Command {

    private final QuestRegistry registry;

    public QuestsCommand(QuestRegistry registry) {
        super("activequests");
        this.registry = registry;
    }

    private Menu<QuestMenuSession> menu = Menu.builder(QuestMenuSession.class)
            .setTitleGenerator(session -> "Активные квесты")
            .setSize(54)
            .setFillingStrategy(session -> {
                val player = session.getPlayer();
                val builder = new FillingStrategy.ResultBuilder();

                var slot = 0;
                for (val quest : session.getResult()) {
                    val data = quest.getDataHolder().get(player);
                    val menuDelegate = quest.getMenuDelegate();
                    builder.addButton(new MenuButton(menuDelegate.getPlayersMenuIcon(player, data.getState()),
                            (player1, click, slot1) -> {
                                menuDelegate.handlePlayersMenuClick(player1, data.getState());
                                session.update();
                            }), slot);
                    slot++;
                }

                return builder.build();
            })
            .build();

    @CommandHandler
    public void main(PlayerExecutor executor) {
        val player = executor.handle();
        val quests = registry.getLines().stream().flatMap(line -> line.getQuests().stream()).collect(Collectors.toSet());
        menu.open(new QuestMenuSession(player, quests, data -> data.getState() == States.LAUNCHED || data.getState() == States.COMPLETED));
    }

}
