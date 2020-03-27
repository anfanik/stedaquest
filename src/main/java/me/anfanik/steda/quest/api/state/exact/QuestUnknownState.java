package me.anfanik.steda.quest.api.state.exact;

import lombok.NoArgsConstructor;
import me.anfanik.steda.quest.api.Quest;
import me.anfanik.steda.quest.api.state.QuestStateBase;

@NoArgsConstructor
public class QuestUnknownState extends QuestStateBase {

    public QuestUnknownState(Quest quest) {
        super(quest);
    }

}
