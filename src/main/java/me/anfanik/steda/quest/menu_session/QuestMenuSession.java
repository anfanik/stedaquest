package me.anfanik.steda.quest.menu_session;

import lombok.val;
import me.anfanik.steda.api.menu.MenuSession;
import me.anfanik.steda.quest.api.Quest;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Predicate;

public class QuestMenuSession extends MenuSession {

    private final Map<Quest, Predicate<Quest.Data>> quests;
    private List<Quest> result;

    public QuestMenuSession(Player player, Set<Quest> quests, Predicate<Quest.Data> predicate) {
        super(player);
        this.quests = new HashMap<>();
        quests.forEach(quest -> this.quests.put(quest, predicate));
        refresh();
    }

    public QuestMenuSession(Player player, Map<Quest, Predicate<Quest.Data>> quests) {
        super(player);
        this.quests = quests;
        refresh();
    }

    @Override
    public void update() {
        super.update();
        refresh();
    }

    public Collection<Quest> refresh() {
        val result = new ArrayList<Quest>();
        quests.forEach((quest, predicate) -> {
            val data = quest.getDataHolder().get(getPlayer());
            if (predicate.test(data)) {
                result.add(quest);
            }
        });
        this.result = result;
        return result;
    }

    public List<Quest> getResult() {
        return Collections.unmodifiableList(result);
    }

}