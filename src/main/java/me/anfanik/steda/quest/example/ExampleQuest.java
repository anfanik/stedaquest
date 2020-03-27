package me.anfanik.steda.quest.example;

import me.anfanik.steda.quest.api.QuestBase;
import me.anfanik.steda.quest.api.annotation.Dependency;
import me.anfanik.steda.quest.api.module.exact.StedaApi;

public class ExampleQuest extends QuestBase {

    @Dependency(StedaApi.class)
    private StedaApi stedaApi;

    @Dependency(ExampleModule.class)
    private ExampleModule exampleModule;

    public ExampleQuest(String id) {
        super(id);
    }

}
