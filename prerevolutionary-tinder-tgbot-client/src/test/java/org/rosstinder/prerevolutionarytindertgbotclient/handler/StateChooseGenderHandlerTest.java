package org.rosstinder.prerevolutionarytindertgbotclient.handler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.rosstinder.prerevolutionarytindertgbotclient.model.AnswerText;
import org.rosstinder.prerevolutionarytindertgbotclient.model.BotState;
import org.rosstinder.prerevolutionarytindertgbotclient.service.ReplyKeyboardGetter;
import org.rosstinder.prerevolutionarytindertgbotclient.service.RosstinderClientImpl;
import org.rosstinder.prerevolutionarytindertgbotclient.service.TelegramAnswerSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

class StateChooseGenderHandlerTest {
    RosstinderClientImpl rosstinderClientImpl = Mockito.mock(RosstinderClientImpl.class);
    Message message = Mockito.mock(Message.class);
    TelegramAnswerSender telegramAnswerSender = new TelegramAnswerSender();
    ReplyKeyboardGetter replyKeyboardGetter = new ReplyKeyboardGetter();
    StateChooseGenderHandler stateChooseGenderHandler = new StateChooseGenderHandler(telegramAnswerSender, rosstinderClientImpl, replyKeyboardGetter);

    @Test
    void getState_shouldReturnStatusChooseGender_whenReferToThisHandler() {
        Assertions.assertEquals(BotState.CHOOSE_GENDER, stateChooseGenderHandler.getState());
    }

    @Test
    void processState_mustReturnMessageSelectGender_whenUserSelectsWrongGender() {
        Mockito.when(message.getChatId()).thenReturn(741852963L);
        Mockito.when(message.getText()).thenReturn("Сударь");
        Update update = new Update();
        update.setMessage(message);
        Mockito.when(rosstinderClientImpl.getUserStatus(update.getMessage().getChatId())).thenReturn("choose gender");

        Assertions.assertEquals(AnswerText.CHOOSE_GENDER.getText(), ((SendMessage) stateChooseGenderHandler.processState(update).get(0)).getText());
    }

    @Test
    void processState_mustReturnMessageEnterName_whenUserChooseCorrectGender() {
        Mockito.when(message.getChatId()).thenReturn(741852963L);
        Mockito.when(message.getText()).thenReturn("Сударъ");
        Update update = new Update();
        update.setMessage(message);
        Mockito.when(rosstinderClientImpl.getUserStatus(update.getMessage().getChatId())).thenReturn("choose gender");

        Assertions.assertEquals(AnswerText.INPUT_NAME.getText(), ((SendMessage) stateChooseGenderHandler.processState(update).get(0)).getText());
    }
}