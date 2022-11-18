package org.rosstinder.prerevolutionarytindertgbotclient.model;

import lombok.Getter;

@Getter
public enum AnswerText {
    CHOOSE_GENDER("Выберите Вашъ полъ"),
    INPUT_NAME("Ввѣдите Ваше имя"),
    INPUT_DESCRIPTION("Ввѣдите несколько строкъ о себе"),
    CHOOSE_PREFERENCE("Выберите Ваши предпочтенiя"),
    MENU("Выберите необходимый пунктъ меню"),
    PROFILE("Выберите пунктъ анкеты, который хотите изменить"),
    UPDATE_GENDER("Выберите Вашъ новый полъ"),
    UPDATE_NAME("Ввѣдите Ваше новое имя"),
    UPDATE_DESCRIPTION("Ввѣдите новое описанiе себя"),
    UPDATE_PREFERENCE("Выберите Ваши новые предпочтенiя"),
    CHOOSE_AVAILABLE_ACTION("Пожалуйста, выберите доступное дѣйствiе..."),
    MUTUAL_LOVE("Вы любимы!"),
    TOO_LONG_NAME("Слишкомъ длинное имя, попробуйте снова!", 32),
    TOO_LONG_DESCRIPTION("Слишкомъ длинное описанiе, попробуйте снова!", 512);

    private final String text;
    private int length;

    AnswerText(String text) {
        this.text = text;
    }

    AnswerText(String text, int length) {
        this.text = text;
        this.length = length;
    }
}
