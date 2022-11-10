package org.rosstinder.prerevolutionarytindertgbotclient.controller;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.net.URI;

@Component
public class UpdateController {
    private TelegramBot telegramBot;
    private RestTemplate restTemplate;

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
            URI url = new URI("localhost:8080/users//status")
            String userStatus = restTemplate.getForObject()
            String messageText = update.getMessage().getText();
            if (messageText.equals("/start")) {

            }
        }
    }
}
