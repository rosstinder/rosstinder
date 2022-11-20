package org.rosstinder.prerevolutionarytindertgbotclient.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum Gender {
    MALE("Сударъ"), FEMALE("Сударыня");

    private final String gender;

    private static final Map<String, Gender> BY_LABEL = new HashMap<>();

    static {
        for (Gender a : values()) {
            BY_LABEL.put(a.gender, a);
        }
    }

    public static Optional<Gender> valueOfLabel(String label) {
        return Optional.ofNullable(BY_LABEL.get(label));
    }
}
