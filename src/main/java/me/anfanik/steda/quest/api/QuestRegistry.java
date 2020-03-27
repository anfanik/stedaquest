package me.anfanik.steda.quest.api;

import java.util.Collection;
import java.util.Optional;

public interface QuestRegistry {

    Optional<QuestLine> get(String id);

    Optional<Quest> get(String line, String quest);

    Optional<Quest> getQuest(String id);

    void add(QuestLine quest);

    void remove(QuestLine quest);

    Optional<QuestLine> get(Quest quest);

    Collection<QuestLine> getLines();

}
