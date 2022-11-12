package org.rosstinder.prerevolutionarytindertgbotclient.controller;

import org.rosstinder.prerevolutionarytindertgbotclient.service.RosstinderClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class UpdateController {
    private TelegramBot telegramBot;
    private final RosstinderClient rosstinderClient;

    public UpdateController(RosstinderClient rosstinderClient) {
        this.rosstinderClient = rosstinderClient;
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
            String messageText = update.getMessage().getText();
            System.out.println(rosstinderClient.getImageProfile(message.getChatId()));
            if (messageText.equals("/start")) {
                return;
            }
        }
    }

    private void processTextMessage(Update update) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        String userStatus = rosstinderClient.getUserStatus(chatId);
        switch (userStatus) {
            case "new" -> processStatusNew(update);
            case ""
        }
    }
}
