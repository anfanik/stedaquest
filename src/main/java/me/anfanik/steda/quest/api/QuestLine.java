package me.anfanik.steda.quest.api;

import lombok.val;

import java.util.Collection;
import java.util.Optional;

public interface QuestLine {

    String getId();

    boolean has(Quest quest);

    Optional<Quest> get(String id);

    void add(Quest quest);

    Collection<Quest> getQuests();

    default Optional<Quest> next(Quest current) {
        val quests = getQuests();
        if (quests.contains(current)) {
            boolean next = false;
            for (Quest quest : quests) {
                if (next) {
                    return Optional.of(quest);
                } else if (quest.equals(current)) {
                    next = true;
                }
            }
        }
        return Optional.empty();
    }

    default Optional<Quest> previous(Quest current) {
        val quests = getQuests();
        if (quests.contains(current)) {
            Quest previous = null;
            for (Quest quest : quests) {
                if (quest.equals(current)) {
                    return Optional.ofNullable(previous);
                }
                previous = quest;
            }
        }
        return Optional.empty();
    }

}
