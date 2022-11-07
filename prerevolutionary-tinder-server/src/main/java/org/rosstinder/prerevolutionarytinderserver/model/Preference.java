package org.rosstinder.prerevolutionarytinderserver.model;

import lombok.Getter;

public enum Preference {
    MALE ("Сударъ"),
    FEMALE ("Сударыня"),
    ALL ("Все");

    @Getter
    private String preference;
    Preference(String preference) {
        this.preference = preference;
    }

    @Override
    public String toString() {
        return this.preference;
    }

    public static Preference fromString(String text) {
        for (Preference preference : Preference.values()) {
            if (preference.getPreference().equalsIgnoreCase(text)) {
                return preference;
            }
        }
        return null;
    }

    public static boolean compareGenderAndPreference(Gender gender, Preference preference) {
        if (preference.getPreference().equals(gender.getGender())) {
            return true;
        } else if (preference.getPreference().equals(Preference.ALL)) {
            return true;
        }
        return false;
    }
}
