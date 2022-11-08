package org.rosstinder.prerevolutionarytinderserver.model;

import lombok.Getter;
import org.rosstinder.prerevolutionarytinderserver.exception.BusinessException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Gender {
    private final String[] POSSIBLE_VALUES = {"Сударъ", "Сударыня"};

    private final List<String> VALUES = new ArrayList(Arrays.asList(POSSIBLE_VALUES));
    @Getter
    private String gender;
    public Gender(String gender) throws BusinessException {
        if (VALUES.contains(gender)) {
            this.gender = gender;
        } else {
            throw new BusinessException("Неправильный формат пола. Допустимые значения: Сударъ, Сударыня.");
        }
    }

    public void setGender(String gender) throws BusinessException {
        if (VALUES.contains(gender)) {
            this.gender = gender;
        } else {
            throw new BusinessException("Неправильный формат пола. Допустимые значения: Сударъ, Сударыня.");
        }
    }

    @Override
    public String toString() {
        return this.gender;
    }

}
