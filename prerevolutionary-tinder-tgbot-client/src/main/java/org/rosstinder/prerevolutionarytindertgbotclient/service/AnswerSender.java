package org.rosstinder.prerevolutionarytindertgbotclient.service;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.io.ByteArrayInputStream;

@Component
public class AnswerSender {
    public SendMessage sendMessageWithText(Update update, String answer) {
        Message message = update.getMessage();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText(answer);
        return sendMessage;
    }

    public SendMessage sendMessageWithKeyboard(Update update, String answer, ReplyKeyboardMarkup keyboard) {
        Message message = update.getMessage();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText(answer);
        sendMessage.setReplyMarkup(keyboard);
        return sendMessage;
    }

    public SendPhoto sendPhotoWithCaption(Update update, String caption, byte[] imageInBytes) {
        Message message = update.getMessage();
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(message.getChatId());
        sendPhoto.setPhoto(new InputFile(new ByteArrayInputStream(imageInBytes), "image.png"));
        sendPhoto.setCaption(caption);
        return sendPhoto;
    }

    public SendPhoto sendPhotoWithKeyboard(Update update, String caption, byte[] imageInBytes, ReplyKeyboardMarkup keyboard) {
        Message message = update.getMessage();
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(message.getChatId());
        sendPhoto.setPhoto(new InputFile(new ByteArrayInputStream(imageInBytes), "image.png"));
        sendPhoto.setCaption(caption);
        sendPhoto.setReplyMarkup(keyboard);
        return sendPhoto;
    }
}
