package org.rosstinder.prerevolutionarytindertgbotclient.handler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.rosstinder.prerevolutionarytindertgbotclient.model.AnswerText;
import org.rosstinder.prerevolutionarytindertgbotclient.model.BotState;
import org.rosstinder.prerevolutionarytindertgbotclient.service.TelegramAnswerSender;
import org.rosstinder.prerevolutionarytindertgbotclient.service.ReplyKeyboardGetter;
import org.rosstinder.prerevolutionarytindertgbotclient.service.RosstinderClientImpl;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

class StateInputDescriptionHandlerTest {
    RosstinderClientImpl rosstinderClientImpl = Mockito.mock(RosstinderClientImpl.class);
    Message message = Mockito.mock(Message.class);
    TelegramAnswerSender telegramAnswerSender = new TelegramAnswerSender();
    ReplyKeyboardGetter replyKeyboardGetter = new ReplyKeyboardGetter();
    StateInputDescriptionHandler stateInputDescriptionHandler = new StateInputDescriptionHandler(telegramAnswerSender, rosstinderClientImpl, replyKeyboardGetter);

    @Test
    void getState_shouldReturnStatusInputDescription_whenReferToThisHandler() {
        Assertions.assertEquals(BotState.INPUT_DESCRIPTION, stateInputDescriptionHandler.getState());
    }

    @Test
    void processUpdate_shouldReturnMessageTooLongDescription_whenUserEnterDescriptionLongerThan512Characters() {
        Mockito.when(message.getChatId()).thenReturn(741852963L);
        Mockito.when(message.getText()).thenReturn(new String(new byte[513]));
        Update update = new Update();
        update.setMessage(message);
        Mockito.when(rosstinderClientImpl.getUserStatus(update.getMessage().getChatId())).thenReturn("input description");

        Assertions.assertEquals(AnswerText.TOO_LONG_DESCRIPTION.getText(), ((SendMessage) stateInputDescriptionHandler.processState(update).get(0)).getText());
    }

    @Test
    void processUpdate_shouldReturnMessageChoosePreference_whenUserEnterSuitableDescription() {
        Mockito.when(message.getChatId()).thenReturn(741852963L);
        Mockito.when(message.getText()).thenReturn("Молодойчеловек");
        Update update = new Update();
        update.setMessage(message);
        Mockito.when(rosstinderClientImpl.getUserStatus(update.getMessage().getChatId())).thenReturn("input description");

        Assertions.assertEquals(AnswerText.CHOOSE_PREFERENCE.getText(), ((SendMessage) stateInputDescriptionHandler.processState(update).get(0)).getText());
    }
}