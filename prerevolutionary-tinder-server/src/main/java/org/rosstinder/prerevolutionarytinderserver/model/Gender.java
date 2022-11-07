package org.rosstinder.prerevolutionarytinderserver.model;

import lombok.Getter;
import org.rosstinder.prerevolutionarytinderserver.exception.BusinessException;

public enum Gender {
    MALE ("Сударъ"),
    FEMALE ("Сударыня");

    @Getter
    private String gender;
    Gender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return this.gender;
    }

    public static Gender fromString(String text) throws BusinessException {
        for (Gender gender : Gender.values()) {
            if (gender.getGender().equalsIgnoreCase(text)) {
                return gender;
            }
        }
        throw new BusinessException("Неправильный формат пола. Допустимые значения: Сударъ, Сударыня.");
    }
}
