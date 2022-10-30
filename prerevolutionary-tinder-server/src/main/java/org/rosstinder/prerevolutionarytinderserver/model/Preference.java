package org.rosstinder.prerevolutionarytinderserver.model;

import lombok.Getter;

public enum Preference {
    MALE ("Сударъ"),
    FEMALE ("Сударыня"),
    ALL ("Все");

    @Getter
    private String gender;
    Preference(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return this.gender;
    }
}
