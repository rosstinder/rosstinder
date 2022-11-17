package org.rosstinder.prerevolutionarytinderserver.model;

import lombok.Getter;

public enum Gender {
    MALE("Сударъ"),
    FEMALE("Сударыня");

    @Getter
    private final String gender;

    Gender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return this.gender;
    }
}
