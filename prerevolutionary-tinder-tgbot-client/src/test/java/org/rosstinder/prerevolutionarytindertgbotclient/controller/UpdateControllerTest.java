package org.rosstinder.prerevolutionarytindertgbotclient.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.rosstinder.prerevolutionarytindertgbotclient.handler.BotStateHandler;
import org.rosstinder.prerevolutionarytindertgbotclient.handler.StateChooseGenderHandler;
import org.rosstinder.prerevolutionarytindertgbotclient.handler.StateInputDescriptionHandler;
import org.rosstinder.prerevolutionarytindertgbotclient.handler.StateInputNameHandler;
import org.rosstinder.prerevolutionarytindertgbotclient.model.AnswerText;
import org.rosstinder.prerevolutionarytindertgbotclient.service.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;

class UpdateControllerTest {
    RosstinderClient rosstinderClient = Mockito.mock(RosstinderClient.class);
    Message message = Mockito.mock(Message.class);
    TelegramAnswerSender telegramAnswerSender = new TelegramAnswerSender();
    ReplyKeyboardGetter replyKeyboardGetter = new ReplyKeyboardGetter();
    List<BotStateHandler> botStateHandlers = List.of(new StateChooseGenderHandler(telegramAnswerSender, rosstinderClient, replyKeyboardGetter),
            new StateInputNameHandler(telegramAnswerSender, rosstinderClient),
            new StateInputDescriptionHandler(telegramAnswerSender, rosstinderClient, replyKeyboardGetter));
    UpdateController updateController = new UpdateController(rosstinderClient, botStateHandlers);

    @Test
    void processUpdate_mustReturnEmptyList_whenUpdateDoesntContainMessages() {
        Update update = new Update();
        Assertions.assertEquals(Collections.EMPTY_LIST, updateController.processUpdate(update));
    }

    @Test
    void processUpdate_mustReturnEmptyList_whenUserSubmitMessageWithoutText() {
        Update update = new Update();
        update.setMessage(message);

        Assertions.assertEquals(Collections.EMPTY_LIST, updateController.processUpdate(update));
    }

    @Test
    void processUpdate_mustReturnMessageEnterName_whenUserChooseCorrectGender() {
        Mockito.when(message.getChatId()).thenReturn(741852963L);
        Mockito.when(message.getText()).thenReturn("Сударъ");
        Update update = new Update();
        update.setMessage(message);
        Mockito.when(rosstinderClient.getUserStatus(update.getMessage().getChatId())).thenReturn("choose gender");

        Assertions.assertEquals(AnswerText.INPUT_NAME.getText(), ((SendMessage) updateController.processUpdate(update).get(0)).getText());
    }
}