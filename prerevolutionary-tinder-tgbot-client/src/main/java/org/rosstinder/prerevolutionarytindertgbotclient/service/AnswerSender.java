package org.rosstinder.prerevolutionarytindertgbotclient.service;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.io.ByteArrayInputStream;

@Component
public class AnswerSender {

    private static final String DEFAULT_FILE_NAME = "image.png";

    public SendMessage sendMessageWithText(Long chatId, String answer) {
        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(chatId);
        sendMessage.setText(answer);

        return sendMessage;
    }

    public SendMessage sendMessageWithKeyboard(Long chatId, String answer, ReplyKeyboardMarkup keyboard) {
        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(chatId);
        sendMessage.setText(answer);
        sendMessage.setReplyMarkup(keyboard);

        return sendMessage;
    }

    public SendPhoto sendPhotoWithCaption(Long chatId, String caption, byte[] imageInBytes) {
        SendPhoto sendPhoto = new SendPhoto();

        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(new InputFile(new ByteArrayInputStream(imageInBytes), DEFAULT_FILE_NAME));
        sendPhoto.setCaption(caption);

        return sendPhoto;
    }

    public SendPhoto sendPhotoWithKeyboard(Long chatId, String caption, byte[] imageInBytes, ReplyKeyboardMarkup keyboard) {
        SendPhoto sendPhoto = new SendPhoto();

        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(new InputFile(new ByteArrayInputStream(imageInBytes), DEFAULT_FILE_NAME));
        sendPhoto.setCaption(caption);
        sendPhoto.setReplyMarkup(keyboard);

        return sendPhoto;
    }
}
