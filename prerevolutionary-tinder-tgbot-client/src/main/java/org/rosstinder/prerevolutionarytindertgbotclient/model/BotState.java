package org.rosstinder.prerevolutionarytindertgbotclient.model;

import lombok.Getter;

public enum BotState {
    NEW("new"),
    CHOOSE_GENDER("choose gender"),
    INPUT_NAME("input name"),
    INPUT_DESCRIPTION("input description"),
    CHOOSE_PREFERENCE("choose preference"),
    MENU("menu"),
    SEARCH("search"),
    PROFILE("profile"),
    FAVORITES("favorites"),
    UPDATE_GENDER("update gender"),
    UPDATE_NAME("update name"),
    UPDATE_DESCRIPTION("update description"),
    UPDATE_PREFERENCE("update preference");

    @Getter
    private final String status;

    BotState(String status) {
        this.status = status;
    }
}
