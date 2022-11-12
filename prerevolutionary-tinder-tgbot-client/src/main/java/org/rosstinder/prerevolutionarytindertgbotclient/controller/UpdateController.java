package org.rosstinder.prerevolutionarytindertgbotclient.controller;

import org.rosstinder.prerevolutionarytindertgbotclient.model.BotState;
import org.rosstinder.prerevolutionarytindertgbotclient.service.AnswerSender;
import org.rosstinder.prerevolutionarytindertgbotclient.service.RosstinderClient;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Component
public class UpdateController {
    private TelegramBot telegramBot;
    private final RosstinderClient rosstinderClient;
    private final AnswerSender answerSender;

    public UpdateController(RosstinderClient rosstinderClient, AnswerSender answerSender) {
        this.rosstinderClient = rosstinderClient;
        this.answerSender = answerSender;
    }

    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update) {
        if (update == null) {
            // todo: добавить логирование
            return;
        }
        if (update.getMessage() == null) {
            // todo: добавить логирование
            return;
        }
        if (update.getMessage().getText() != null) {
            processTextMessage(update);
        }
    }

    private void processTextMessage(Update update) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        BotState userStatus = BotState.valueOf(rosstinderClient.getUserStatus(chatId));

        switch (userStatus) {
            case NEW -> processNew(update);
            case CHOOSE_GENDER -> processChooseGender(update);
            case INPUT_NAME -> processInputName(update);
            case INPUT_DESCRIPTION -> processInputDescription(update);
            case CHOOSE_PREFERENCE -> processChoosePreference(update);
            case MENU -> processMenu(update);
            case SEARCH -> processSearch(update);
            case PROFILE -> processProfile(update);
            case FAVORITES -> processFavorites(update);
            case UPDATE_GENDER -> processUpdateGender(update);
            case UPDATE_NAME -> processUpdateName(update);
            case UPDATE_DESCRIPTION -> processUpdateDescription(update);
            case UPDATE_PREFERENCE -> processUpdatePreference(update);
        }
    }

    private void isMessageStart(Update update) {
        Long chatId = update.getMessage().getChatId();
        String textMessage = update.getMessage().getText();
        if (textMessage.equals("/start")) {
            InputFile inputFile = new InputFile(new ByteArrayInputStream(rosstinderClient.getImageProfile(chatId)), "");
            setView(answerSender.sendPhotoWithText(update, "Hello world", inputFile));
        }
    }

    private void processNew(Update update) {

    }

    private void processChooseGender(Update update) {

    }

    private void processInputName(Update update) {
    }

    private void processInputDescription(Update update) {
    }

    private void processChoosePreference(Update update) {

    }

    private void processMenu(Update update) {

    }

    private void processFavorites(Update update) {

    }

    private void processProfile(Update update) {

    }

    private void processSearch(Update update) {

    }

    private void processUpdatePreference(Update update) {
    }

    private void processUpdateDescription(Update update) {

    }

    private void processUpdateName(Update update) {

    }

    private void processUpdateGender(Update update) {

    }

    private void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }

    private void setView(SendPhoto sendPhoto) {
        telegramBot.sendAnswerMessage(sendPhoto);
    }
}
