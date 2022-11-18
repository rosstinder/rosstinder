package org.rosstinder.prerevolutionarytindertgbotclient.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rosstinder.prerevolutionarytindertgbotclient.model.AnswerText;
import org.rosstinder.prerevolutionarytindertgbotclient.model.BotState;
import org.rosstinder.prerevolutionarytindertgbotclient.model.ButtonText;
import org.rosstinder.prerevolutionarytindertgbotclient.model.ProfileDto;
import org.rosstinder.prerevolutionarytindertgbotclient.service.AnswerSender;
import org.rosstinder.prerevolutionarytindertgbotclient.service.ReplyKeyboardGetter;
import org.rosstinder.prerevolutionarytindertgbotclient.service.RosstinderClient;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.text.MessageFormat;

@Slf4j
@Component
@RequiredArgsConstructor
public class StateMenuHandler extends BotStateHandler {
    private final AnswerSender answerSender;
    private final RosstinderClient rosstinderClient;
    private final ReplyKeyboardGetter replyKeyboardGetter;

    @Override
    public BotState getState() {
        return BotState.MENU;
    }

    @Override
    public void processState(Update update) {
        isThisStartMessage(update);

        Long chatId = update.getMessage().getChatId();
        String textMessage = update.getMessage().getText();

        ButtonText buttonText = getButtonText(textMessage);

        switch (buttonText) {
            case SEARCH -> {
                search(chatId);
            }
            case FAVORITES -> {
                seeFavorites(chatId);
            }
            case PROFILE -> {
                seeProfile(chatId);
            }
            default -> {
                displayMenuAgain(chatId, textMessage);
            }
        }
    }

    private void search(Long chatId) {
        ProfileDto nextProfile = rosstinderClient.getNextProfile(chatId);

        setView(answerSender.sendPhotoWithKeyboard(chatId,
                nextProfile.getCaption(),
                nextProfile.getImage(),
                replyKeyboardGetter.getKeyboardForSearch()));

        rosstinderClient.setNewStatus(chatId, BotState.SEARCH);
    }

    private void seeFavorites(Long chatId) {
        ProfileDto nextFavorite = rosstinderClient.getNextFavorite(chatId);

        setView(answerSender.sendPhotoWithKeyboard(chatId,
                nextFavorite.getCaption(),
                nextFavorite.getImage(),
                replyKeyboardGetter.getKeyboardForFavorites()));

        rosstinderClient.setNewStatus(chatId, BotState.FAVORITES);
    }

    private void seeProfile(Long chatId) {
        ProfileDto profile = rosstinderClient.getProfile(chatId);

        setView(answerSender.sendPhotoWithKeyboard(chatId,
                profile.getCaption(),
                profile.getImage(),
                replyKeyboardGetter.getKeyboardForProfile()));

        rosstinderClient.setNewStatus(chatId, BotState.PROFILE);
    }

    private void displayMenuAgain(Long chatId, String textMessage) {
        log.info(MessageFormat.format("Пользователь #{0} ввел неподходящее сообщение \"{1}\"", chatId, textMessage));

        setView(answerSender.sendMessageWithKeyboard(chatId,
                AnswerText.CHOOSE_AVAILABLE_ACTION.getText(),
                replyKeyboardGetter.getKeyboardForMenu()));
    }
}
