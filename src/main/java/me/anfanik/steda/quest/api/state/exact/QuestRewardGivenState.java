package me.anfanik.steda.quest.api.state.exact;

import lombok.NoArgsConstructor;
import me.anfanik.steda.quest.api.Quest;
import me.anfanik.steda.quest.api.state.QuestStateBase;

@NoArgsConstructor
public class QuestRewardGivenState extends QuestStateBase {

    public QuestRewardGivenState(Quest quest) {
        super(quest);
    }

}
