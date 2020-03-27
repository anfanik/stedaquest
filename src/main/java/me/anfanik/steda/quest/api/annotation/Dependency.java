package me.anfanik.steda.quest.api.annotation;

import me.anfanik.steda.quest.api.module.Module;

public @interface Dependency {

    Class<? extends Module> value();

}
