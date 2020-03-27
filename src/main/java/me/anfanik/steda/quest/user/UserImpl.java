package me.anfanik.steda.quest.user;

import lombok.val;
import me.anfanik.steda.quest.api.Quest;

import java.util.HashMap;
import java.util.Map;

public class UserImpl implements User {

    private Map<Quest, Quest.Data> cache = new HashMap<>();

    @Override
    public Quest.Data getData(Quest quest) {
        return cache.computeIfAbsent(quest, quest1 -> null); //Todo: Загрузка из базы данных
    }

    @Override
    public void setData(Quest quest, Quest.Data data) {
        cache.put(quest, data);
    }

    private void pushData() {
        val map = new HashMap<Quest, String>();
        cache.forEach((quest, data) -> map.put(quest, quest.getDataHolder().getModel().serialize(data)));
        //Todo: Загрузка в базу данных
    }

}
