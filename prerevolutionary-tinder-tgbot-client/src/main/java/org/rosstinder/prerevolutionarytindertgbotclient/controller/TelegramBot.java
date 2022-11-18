package org.rosstinder.prerevolutionarytindertgbotclient.controller;

import lombok.extern.slf4j.Slf4j;
import org.rosstinder.prerevolutionarytindertgbotclient.handler.BotStateHandler;
import org.rosstinder.prerevolutionarytindertgbotclient.model.BotState;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    @Value("${bot.name}")
    private String botName;
    @Value("${bot.token}")
    private String botToken;
    private UpdateController updateController;
    private final List<BotStateHandler> handlers;


    public TelegramBot(UpdateController updateController, List<BotStateHandler> handlers) {
        this.updateController = updateController;
        this.handlers = handlers;
    }

    @PostConstruct
    public void init() {
        updateController.registerBot(this);
        for (BotStateHandler handler : handlers) {
            handler.registerBot(this);
        }
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
        updateController.processUpdate(update);
    }

    public void sendAnswerMessage(SendMessage message) {
        try {
            execute(message);
            log.debug(MessageFormat.format("Отправлено сообщение пользователю #{0}", message.getChatId()));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendAnswerMessage(SendPhoto message) {
        try {
            execute(message);
            log.debug(MessageFormat.format("Отправлено сообщение с картинкой пользователю #{0}", message.getChatId()));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
