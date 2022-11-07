package org.rosstinder.prerevolutionarytinderserver.model;

import lombok.Getter;
import org.rosstinder.prerevolutionarytinderserver.exception.BusinessException;

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

    public static Preference fromString(String text) throws BusinessException {
        for (Preference preference : Preference.values()) {
            if (preference.getPreference().equalsIgnoreCase(text)) {
                return preference;
            }
        }
        throw new BusinessException("Неправильный формат предпочтения. Допустимые значения: Сударъ, Сударыня, Всех.");
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
