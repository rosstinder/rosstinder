package org.rosstinder.prerevolutionarytindertgbotclient.handler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.rosstinder.prerevolutionarytindertgbotclient.model.AnswerText;
import org.rosstinder.prerevolutionarytindertgbotclient.model.BotState;
import org.rosstinder.prerevolutionarytindertgbotclient.service.RosstinderClient;
import org.rosstinder.prerevolutionarytindertgbotclient.service.TelegramAnswerSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

class StateInputNameHandlerTest {
    RosstinderClient rosstinderClient = Mockito.mock(RosstinderClient.class);
    Message message = Mockito.mock(Message.class);
    TelegramAnswerSender telegramAnswerSender = new TelegramAnswerSender();
    StateInputNameHandler stateInputNameHandler = new StateInputNameHandler(telegramAnswerSender, rosstinderClient);

    @Test
    void getState_shouldReturnStatusInputName_whenReferToThisHandler() {
        Assertions.assertEquals(BotState.INPUT_NAME, stateInputNameHandler.getState());
    }

    @Test
    void processState_shouldReturnMessageNameTooLong_whenUserEnterNameLongerThan32Characters() {
        Mockito.when(message.getChatId()).thenReturn(741852963L);
        Mockito.when(message.getText()).thenReturn("Александривансергейпетригорьарсен");
        Update update = new Update();
        update.setMessage(message);
        Mockito.when(rosstinderClient.getUserStatus(update.getMessage().getChatId())).thenReturn("input name");

        Assertions.assertEquals(AnswerText.TOO_LONG_NAME.getText(), ((SendMessage) stateInputNameHandler.processState(update).get(0)).getText());
    }

    @Test
    void processState_shouldReturnMessageInputDescription_whenUserEnterSuitableName() {
        Mockito.when(message.getChatId()).thenReturn(741852963L);
        Mockito.when(message.getText()).thenReturn("Александр");
        Update update = new Update();
        update.setMessage(message);
        Mockito.when(rosstinderClient.getUserStatus(update.getMessage().getChatId())).thenReturn("input name");

        Assertions.assertEquals(AnswerText.INPUT_DESCRIPTION.getText(), ((SendMessage) stateInputNameHandler.processState(update).get(0)).getText());
    }

}