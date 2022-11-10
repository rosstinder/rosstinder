package org.rosstinder.prerevolutionarytinderserver.model;

import lombok.Getter;
import org.rosstinder.prerevolutionarytinderserver.exception.BusinessException;

import java.util.ArrayList;
import java.util.Arrays;

public class Preference {
    private final ArrayList VALUES = new ArrayList(Arrays.asList("Сударъ", "Сударыня", "Все"));
    @Getter
    private String preference;

    public Preference(String preference) throws BusinessException {
        if (VALUES.contains(preference)) {
            this.preference = preference;
        } else {
            throw new BusinessException("Неправильный формат предпочтения. Допустимые значения: Сударъ, Сударыня, Всех.");
        }
    }

    @Override
    public String toString() {
        return this.preference;
    }

    public static boolean compareGenderAndPreference(String gender, String preference) {
        if (preference.equals(gender)) {
            return true;
        } else if (preference.equals("Все")) {
            return true;
        }
        return false;
    }
}
