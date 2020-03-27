package me.anfanik.steda.quest.api;

import java.util.*;

public class QuestRegistryImpl implements QuestRegistry {

    private final Map<String, QuestLine> lines = new HashMap<>();

    @Override
    public Optional<QuestLine> get(String id) {
        return Optional.ofNullable(lines.get(id));
    }

    @Override
    public Optional<Quest> get(String line, String quest) {
        QuestLine questLine = lines.get(line);
        if (questLine == null) {
            return Optional.empty();
        }
        return questLine.get(quest);
    }

    @Override
    public Optional<Quest> getQuest(String id) {
        String[] parts = id.split(":");
        if (parts.length < 2) {
            throw new IllegalArgumentException("id must be in line:quest format");
        }
        return get(parts[0], parts[1]);
    }

    @Override
    public void add(QuestLine line) {
        lines.put(line.getId(), line);
    }

    @Override
    public void remove(QuestLine line) {
        lines.remove(line.getId());
    }

    @Override
    public Optional<QuestLine> get(Quest quest) {
        for (QuestLine line : lines.values()) {
            if (line.has(quest)) {
                return Optional.of(line);
            }
        }
        return Optional.empty();
    }

    @Override
    public Collection<QuestLine> getLines() {
        return Collections.unmodifiableCollection(lines.values());
    }

}
