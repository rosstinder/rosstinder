package org.rosstinder.prerevolutionarytindertgbotclient.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum Gender {
    MALE("Сударь"), FEMALE("Сударыня");

    private final String gender;

    private static final Map<String, Gender> BY_LABEL = new HashMap<>();

    static {
        for (Gender a : values()) {
            BY_LABEL.put(a.gender, a);
        }
    }

    public static Gender valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }
}
