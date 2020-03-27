package me.anfanik.steda.quest.api.state.exact;

import lombok.NoArgsConstructor;
import me.anfanik.steda.quest.api.Quest;
import me.anfanik.steda.quest.api.state.QuestStateBase;

@NoArgsConstructor
public class QuestHiddenState extends QuestStateBase {

    public QuestHiddenState(Quest quest) {
        super(quest);
    }

}
