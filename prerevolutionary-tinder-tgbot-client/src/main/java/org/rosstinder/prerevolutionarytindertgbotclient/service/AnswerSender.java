package org.rosstinder.prerevolutionarytindertgbotclient.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

public class AnswerSender {
    public SendMessage sendMessageWithText(Update update, String answer) {
        Message message = update.getMessage();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText(answer);
        return sendMessage;
    }

    public SendMessage sendMessageWithKeyboard(Update update, String answer, ReplyKeyboardMarkup keyboard) {
        return null;
    }
}
