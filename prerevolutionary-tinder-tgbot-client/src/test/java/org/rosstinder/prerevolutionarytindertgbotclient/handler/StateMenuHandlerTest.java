package org.rosstinder.prerevolutionarytindertgbotclient.handler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.rosstinder.prerevolutionarytindertgbotclient.model.*;
import org.rosstinder.prerevolutionarytindertgbotclient.service.AnswerSender;
import org.rosstinder.prerevolutionarytindertgbotclient.service.ReplyKeyboardGetter;
import org.rosstinder.prerevolutionarytindertgbotclient.service.RosstinderClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

class StateMenuHandlerTest {
    RosstinderClient rosstinderClient = Mockito.mock(RosstinderClient.class);
    Message message = Mockito.mock(Message.class);
    AnswerSender answerSender = new AnswerSender();
    ReplyKeyboardGetter replyKeyboardGetter = new ReplyKeyboardGetter();
    StateMenuHandler stateMenuHandler = new StateMenuHandler(answerSender, rosstinderClient, replyKeyboardGetter);

    @Test
    void getState_shouldReturnStatusMenu_whenReferToThisHandler() {
        Assertions.assertEquals(BotState.MENU, stateMenuHandler.getState());
    }

    @Test
    void processState_shouldReturnNextProfile_whenUserChooseSearch() {
        Mockito.when(message.getChatId()).thenReturn(741852963L);
        Mockito.when(message.getText()).thenReturn(ButtonText.SEARCH.getText());
        Update update = new Update();
        update.setMessage(message);
        Mockito.when(rosstinderClient.getUserStatus(update.getMessage().getChatId())).thenReturn("menu");
        Mockito.when(rosstinderClient.getNextProfile(update.getMessage().getChatId())).thenReturn(new ProfileDto("Это тест, друг", new byte[]{12, 23}));

        Assertions.assertEquals("Это тест, друг", ((SendPhoto) stateMenuHandler.processState(update).get(0)).getCaption());
    }

    @Test
    void isThisStartMessage_shouldReturnTrue_whenUserEnterStartMessage() {
        Assertions.assertTrue(stateMenuHandler.isThisStartMessage("/start"));
    }

    @Test
    void isThisStartMessage_shouldReturnFalse_whenUserEnterNoStartMessage() {
        Assertions.assertFalse(stateMenuHandler.isThisStartMessage("Hello world"));
    }

    @Test
    void isGender_shouldReturnTrue_whenUserEnterMessageMale() {
        Assertions.assertTrue(stateMenuHandler.isGender("Сударъ"));
    }

    @Test
    void isGender_shouldReturnFalse_whenUserEnterMessageIncorrectGender() {
        Assertions.assertFalse(stateMenuHandler.isGender("Сударь"));
    }
}