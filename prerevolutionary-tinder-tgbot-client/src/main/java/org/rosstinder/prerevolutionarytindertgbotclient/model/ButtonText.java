package org.rosstinder.prerevolutionarytindertgbotclient.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum ButtonText {
    MALE("Сударъ"),
    FEMALE("Сударыня"),
    ALL("Всехъ"),
    LIKE("❤"),
    DONT_LIKE("\uD83D\uDC94"),
    NEXT("➡"),
    PREVIOUS("⬅"),
    MENU("Меню \uD83D\uDCF1"),
    SEARCH("Поискъ \uD83D\uDD0D"),
    FAVORITES("Любимцы \uD83D\uDC98"),
    PROFILE("Анкета \uD83D\uDCDD"),
    CHANGE_GENDER("Изменить полъ"),
    CHANGE_NAME("Изменить имя"),
    CHANGE_DESCRIPTION("Изменить описанiе"),
    CHANGE_PREFERENCE("Изменить предпочтенiя"),
    NO_BUTTON("");

    private final String text;

    private static final Map<String, ButtonText> BY_LABEL = new HashMap<>();

    static {
        for (ButtonText a : values()) {
            BY_LABEL.put(a.text, a);
        }
    }

    public static Optional<ButtonText> valueOfLabel(String label) {
        return Optional.ofNullable(BY_LABEL.get(label));
    }
}
