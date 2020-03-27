package me.anfanik.steda.quest.command;

import lombok.val;
import lombok.var;
import me.anfanik.steda.api.command.Command;
import me.anfanik.steda.api.command.CommandHandler;
import me.anfanik.steda.api.command.SubcommandHandler;
import me.anfanik.steda.api.command.executor.Executor;
import me.anfanik.steda.api.command.executor.PlayerExecutor;
import me.anfanik.steda.api.menu.Menu;
import me.anfanik.steda.api.menu.button.MenuButton;
import me.anfanik.steda.api.menu.filling.FillingStrategy;
import me.anfanik.steda.api.utility.ItemBuilder;
import me.anfanik.steda.quest.api.QuestStorage;
import me.anfanik.steda.quest.api.state.exact.QuestCompletedState;
import me.anfanik.steda.quest.api.state.exact.QuestLaunchedState;
import me.anfanik.steda.quest.menu_session.QuestMenuSession;
import org.bukkit.Material;

import java.util.stream.Collectors;

public class QuestCommand extends Command {

    public QuestCommand() {
        super("quest", "quests");
    }

    private Menu<QuestMenuSession> menu = Menu.builder(QuestMenuSession.class)
            .setTitleGenerator(session -> "Активные квесты")
            .setSize(54)
            .setFillingStrategy(session -> {
                val builder = new FillingStrategy.ResultBuilder();

                if (!session.getResult().isEmpty()) {
                    var slot = 0;
                    for (val quest : session.getResult()) {
                        builder.addButton(new MenuButton(ItemBuilder.fromItem(quest.getActiveMenuIcon())
                                .formatLoreLines(line -> "&f" + line)
                                .build(),
                                (player1, click, slot1) -> {
                                    player1.closeInventory();
                                    quest.handleActiveMenuClick();
                                }), slot);
                        slot++;
                    }
                } else {
                    builder.addButton(new MenuButton(ItemBuilder.fromMaterial(Material.BARRIER).setName("Нет активных квестов :(").build()), 22);
                }

                return builder.build();
            })
            .build();

    @CommandHandler
    public void main(PlayerExecutor executor) {
        val player = executor.handle();
        val storage = QuestStorage.get(player.getUniqueId());

        val quests = storage.getQuests().stream()
                .filter(quest -> {
                    val state = quest.getState();
                    return state instanceof QuestLaunchedState
                            || state instanceof QuestCompletedState;
                })
                .collect(Collectors.toSet());
        menu.open(new QuestMenuSession(player, quests, quest -> true));
    }

}