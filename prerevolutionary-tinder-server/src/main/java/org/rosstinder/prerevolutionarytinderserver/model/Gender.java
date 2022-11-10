package org.rosstinder.prerevolutionarytinderserver.model;

import lombok.Getter;
import org.rosstinder.prerevolutionarytinderserver.exception.BusinessException;

import java.util.ArrayList;
import java.util.Arrays;

public class Gender {
    private final ArrayList VALUES = new ArrayList(Arrays.asList("Сударъ", "Сударыня"));
    @Getter
    private String gender;
    public Gender(String gender) throws BusinessException {
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
