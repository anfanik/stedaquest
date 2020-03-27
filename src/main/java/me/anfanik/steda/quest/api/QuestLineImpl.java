package me.anfanik.steda.quest.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;

@RequiredArgsConstructor
public class QuestLineImpl implements QuestLine {

    @Getter
    private final String id;

    private final Map<String, Quest> quests = new LinkedHashMap<>();

    @Override
    public boolean has(Quest quest) {
        return quests.containsValue(quest);
    }

    @Override
    public Optional<Quest> get(String id) {
        return Optional.ofNullable(quests.get(id));
    }

    @Override
    public void add(Quest quest) {
        quests.put(quest.getId(), quest);
    }

    @Override
    public Collection<Quest> getQuests() {
        return Collections.unmodifiableCollection(quests.values());
    }

}
