package org.rosstinder.prerevolutionarytindertgbotclient.handler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.rosstinder.prerevolutionarytindertgbotclient.model.BotState;
import org.rosstinder.prerevolutionarytindertgbotclient.model.ButtonText;
import org.rosstinder.prerevolutionarytindertgbotclient.model.ProfileDto;
import org.rosstinder.prerevolutionarytindertgbotclient.service.ReplyKeyboardGetter;
import org.rosstinder.prerevolutionarytindertgbotclient.service.RosstinderClientImpl;
import org.rosstinder.prerevolutionarytindertgbotclient.service.TelegramAnswerSender;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

class StateMenuHandlerTest {
    RosstinderClientImpl rosstinderClientImpl = Mockito.mock(RosstinderClientImpl.class);
    Message message = Mockito.mock(Message.class);
    TelegramAnswerSender telegramAnswerSender = new TelegramAnswerSender();
    ReplyKeyboardGetter replyKeyboardGetter = new ReplyKeyboardGetter();
    StateMenuHandler stateMenuHandler = new StateMenuHandler(telegramAnswerSender, rosstinderClientImpl, replyKeyboardGetter);

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
        Mockito.when(rosstinderClientImpl.getUserStatus(update.getMessage().getChatId())).thenReturn("menu");
        Mockito.when(rosstinderClientImpl.getNextProfile(update.getMessage().getChatId())).thenReturn(new ProfileDto("Это тест, друг", new byte[]{12, 23}));

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