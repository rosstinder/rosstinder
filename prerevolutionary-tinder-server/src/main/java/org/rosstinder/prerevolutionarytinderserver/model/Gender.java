package org.rosstinder.prerevolutionarytinderserver.model;

import lombok.Getter;

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

    public static Gender fromString(String text) {
        for (Gender gender : Gender.values()) {
            if (gender.getGender().equalsIgnoreCase(text)) {
                return gender;
            }
        }
        return null;
    }
}
