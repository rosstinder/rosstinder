package org.rosstinder.prerevolutionarytinderserver.model;

import lombok.Getter;

public enum Preference {
    MALE("Сударъ"),
    FEMALE("Сударыня"),
    ALL("Всех");

    @Getter
    private final String preference;

    Preference(String preference) {
        this.preference = preference;
    }

    @Override
    public String toString() {
        return this.preference;
    }

    public static boolean compareGenderAndPreference(Gender gender, Preference preference) {
        if (preference.getPreference().equals(gender.getGender())) {
            return true;
        } else return preference.equals(Preference.ALL);
    }
}
