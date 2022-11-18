package org.rosstinder.prerevolutionarytindertgbotclient.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum Preference {
    MALE("Сударъ"),
    FEMALE("Сударыня"),
    ALL("Всехъ");

    private final String preference;

    private static final Map<String, Preference> BY_LABEL = new HashMap<>();

    static {
        for (Preference a : values()) {
            BY_LABEL.put(a.preference, a);
        }
    }

    public static Preference valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }
}
