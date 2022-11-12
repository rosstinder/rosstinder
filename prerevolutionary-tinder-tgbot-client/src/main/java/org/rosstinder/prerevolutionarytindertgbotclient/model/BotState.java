package org.rosstinder.prerevolutionarytindertgbotclient.model;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

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

    private static final Map<String, BotState> BY_STATUS = new HashMap<>();

    static {
        for (BotState a : values()) {
            BY_STATUS.put(a.status, a);
        }
    }

    public static BotState valueOfLabel(String status) {
        return BY_STATUS.get(status);
    }
}
