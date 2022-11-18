package org.rosstinder.prerevolutionarytindertgbotclient.handler;

import lombok.extern.slf4j.Slf4j;
import org.rosstinder.prerevolutionarytindertgbotclient.controller.TelegramBot;
import org.rosstinder.prerevolutionarytindertgbotclient.model.*;
import org.rosstinder.prerevolutionarytindertgbotclient.service.AnswerSender;
import org.rosstinder.prerevolutionarytindertgbotclient.service.RosstinderClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.text.MessageFormat;

@Slf4j
@Component
public abstract class BotStateHandler {
    private TelegramBot telegramBot;
    @Autowired
    private AnswerSender answerSender;
    @Autowired
    private RosstinderClient rosstinderClient;

    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    protected void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }

    protected void setView(SendPhoto sendPhoto) {
        telegramBot.sendAnswerMessage(sendPhoto);
    }

    public abstract BotState getState();

    public abstract void processState(Update update);

    protected void isThisStartMessage(Update update) {
        Long chatId = update.getMessage().getChatId();
        String textMessage = update.getMessage().getText();

        if (textMessage.equals("/start")) {
            log.info(MessageFormat.format("От пользователя #{0} получена команда /start", chatId));
            ProfileDto profile = rosstinderClient.getNextFavorite(chatId);

            setView(answerSender.sendPhotoWithCaption(chatId,
                    profile.getCaption(),
                    profile.getImage()));
        }
    }

    protected ButtonText getButtonText(String message) {
        try {
            return ButtonText.valueOfLabel(message);
        } catch (IllegalArgumentException exception) {
            return ButtonText.NO_BUTTON;
        }
    }

    protected boolean isGender(String message) {
        try {
            Gender.valueOfLabel(message);
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    protected boolean isPreference(String message) {
        try {
            Preference.valueOfLabel(message);
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    protected boolean isTextLengthNoMoreThanNCharacters(String text, int N) {
        return text.length() <= N;
    }
}
