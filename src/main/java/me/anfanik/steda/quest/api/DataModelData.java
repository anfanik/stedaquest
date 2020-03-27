package me.anfanik.steda.quest.api;

import lombok.Data;

@Data
public class DataModelData {

    private final Quest.DataModel model;

    private final Quest.Data data;

    public String serialize() {
        return model.serialize(data);
    }

    public Quest.Data deserialize(String n) {
        return null;
    }

}