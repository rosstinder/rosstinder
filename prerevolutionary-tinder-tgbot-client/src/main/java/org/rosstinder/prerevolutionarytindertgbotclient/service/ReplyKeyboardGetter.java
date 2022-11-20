package org.rosstinder.prerevolutionarytindertgbotclient.service;

import org.rosstinder.prerevolutionarytindertgbotclient.model.ButtonText;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class ReplyKeyboardGetter {

    public ReplyKeyboardMarkup getKeyboardForGender() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(createKeyboardRow(ButtonText.MALE, ButtonText.FEMALE));
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup getKeyboardForPreference() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(createKeyboardRow(ButtonText.MALE, ButtonText.FEMALE));
        keyboard.add(createKeyboardRow(ButtonText.ALL));
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup getKeyboardForMenu() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(createKeyboardRow(ButtonText.SEARCH, ButtonText.FAVORITES));
        keyboard.add(createKeyboardRow(ButtonText.PROFILE));
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup getKeyboardForSearch() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(createKeyboardRow(ButtonText.DONT_LIKE, ButtonText.LIKE));
        keyboard.add(createKeyboardRow(ButtonText.MENU));
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup getKeyboardForFavorites() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(createKeyboardRow(ButtonText.PREVIOUS, ButtonText.NEXT));
        keyboard.add(createKeyboardRow(ButtonText.MENU));
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup getKeyboardForProfile() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(createKeyboardRow(ButtonText.CHANGE_NAME, ButtonText.CHANGE_GENDER));
        keyboard.add(createKeyboardRow(ButtonText.CHANGE_DESCRIPTION, ButtonText.CHANGE_PREFERENCE));
        keyboard.add(createKeyboardRow(ButtonText.MENU));
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    private KeyboardRow createKeyboardRow(ButtonText... buttons) {
        KeyboardRow row = new KeyboardRow();
        Arrays.stream(buttons).map(ButtonText::getText).forEach(row::add);
        return row;
    }
}
