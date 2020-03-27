package me.anfanik.steda.quest.api.state;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum States implements State {

    UNKNOWN(""),
    HIDDEN("Квест скрыт"),
    PENDING("Квест открыт"),
    LAUNCHED("Квест скрыт"),
    COMPLETED("Квест выполнен"),
    REWARD_GIVEN("Награда за квест получена");

    @Getter
    private final String description;

    @Override
    public String getId() {
        return name();
    }
}