package org.rosstinder.prerevolutionarytindertgbotclient.handler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.rosstinder.prerevolutionarytindertgbotclient.model.AnswerText;
import org.rosstinder.prerevolutionarytindertgbotclient.model.BotState;
import org.rosstinder.prerevolutionarytindertgbotclient.service.AnswerSender;
import org.rosstinder.prerevolutionarytindertgbotclient.service.ReplyKeyboardGetter;
import org.rosstinder.prerevolutionarytindertgbotclient.service.RosstinderClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.junit.jupiter.api.Assertions.*;

class StateInputDescriptionHandlerTest {
    RosstinderClient rosstinderClient = Mockito.mock(RosstinderClient.class);
    Message message = Mockito.mock(Message.class);
    AnswerSender answerSender = new AnswerSender();
    ReplyKeyboardGetter replyKeyboardGetter = new ReplyKeyboardGetter();
    StateInputDescriptionHandler stateInputDescriptionHandler = new StateInputDescriptionHandler(answerSender, rosstinderClient, replyKeyboardGetter);

    @Test
    void getState_shouldReturnStatusInputDescription_whenReferredToThisHandler() {
        Assertions.assertEquals(BotState.INPUT_DESCRIPTION, stateInputDescriptionHandler.getState());
    }

    @Test
    void processUpdate_shouldReturnMessageTooLongDescription_whenUserEnteredDescriptionLongerThan512Characters() {
        Mockito.when(message.getChatId()).thenReturn(741852963L);
        Mockito.when(message.getText()).thenReturn("МолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловекМолодойчеловек");
        Update update = new Update();
        update.setMessage(message);
        Mockito.when(rosstinderClient.getUserStatus(update.getMessage().getChatId())).thenReturn("input description");

        Assertions.assertEquals(AnswerText.TOO_LONG_DESCRIPTION.getText(), ((SendMessage) stateInputDescriptionHandler.processState(update).get(0)).getText());
    }

    @Test
    void processUpdate_shouldReturnMessageChoosePreference_whenUserEnteredSuitableDescription() {
        Mockito.when(message.getChatId()).thenReturn(741852963L);
        Mockito.when(message.getText()).thenReturn("Молодойчеловек");
        Update update = new Update();
        update.setMessage(message);
        Mockito.when(rosstinderClient.getUserStatus(update.getMessage().getChatId())).thenReturn("input description");

        Assertions.assertEquals(AnswerText.CHOOSE_PREFERENCE.getText(), ((SendMessage) stateInputDescriptionHandler.processState(update).get(0)).getText());
    }
}