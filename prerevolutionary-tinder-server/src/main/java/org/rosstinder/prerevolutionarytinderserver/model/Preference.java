package org.rosstinder.prerevolutionarytinderserver.model;

import lombok.Getter;
import org.rosstinder.prerevolutionarytinderserver.exception.BusinessException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Preference {
    private final String[] POSSIBLE_VALUES = {"Сударъ", "Сударыня", "Все"};
    private final List<String> VALUES = new ArrayList(Arrays.asList(POSSIBLE_VALUES));
    @Getter
    private String preference;

    public Preference(String preference) throws BusinessException {
        if (VALUES.contains(preference)) {
            this.preference = preference;
        } else {
            throw new BusinessException("Неправильный формат предпочтения. Допустимые значения: Сударъ, Сударыня, Всех.");
        }
    }

    public void setPreference(String preference) throws BusinessException {
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

    public static boolean compareGenderAndPreference(Gender gender, Preference preference) {
        if (preference.getPreference().equals(gender.getGender())) {
            return true;
        } else if (preference.getPreference().equals(preference.VALUES.get(2))) {
            return true;
        }
        return false;
    }
}
