package org.rosstinder.prerevolutionarytindertgbotclient.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.MessageFormat;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    @Value("${bot.name}")
    private String botName;
    @Value("${bot.token}")
    private String botToken;
    private final UpdateController updateController;


    public TelegramBot(UpdateController updateController) {
        this.updateController = updateController;
    }


    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        List<Object> methods = updateController.processUpdate(update);
        for (Object method : methods) {
            sendAnswerMessage(method);
        }
    }

    public void sendAnswerMessage(Object method) {
        try {
            if (method instanceof SendMessage message) {
                execute(message);
                log.debug(MessageFormat.format("Отправлено сообщение пользователю #{0}", message.getChatId()));
            } else if (method instanceof SendPhoto photo) {
                execute(photo);
                log.debug(MessageFormat.format("Отправлено сообщение с картинкой пользователю #{0}", photo.getChatId()));
            }
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
