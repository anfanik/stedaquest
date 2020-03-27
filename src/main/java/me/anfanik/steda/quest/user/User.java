package me.anfanik.steda.quest.user;

import me.anfanik.steda.quest.api.Quest;

public interface User {

    Quest.Data getData(Quest quest);

    void setData(Quest quest, Quest.Data data);

}
