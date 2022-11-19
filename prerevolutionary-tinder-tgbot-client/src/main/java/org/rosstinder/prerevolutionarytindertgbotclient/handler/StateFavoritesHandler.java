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
public class StateFavoritesHandler extends BotStateHandler {
    private final AnswerSender answerSender;
    private final RosstinderClient rosstinderClient;
    private final ReplyKeyboardGetter replyKeyboardGetter;

    @Override
    public BotState getState() {
        return BotState.FAVORITES;
    }

    @Override
    public void processState(Update update) {
        isThisStartMessage(update);

        String textMessage = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        ButtonText buttonText = getButtonText(textMessage);

        switch (buttonText) {
            case NEXT -> {
                seeNextFavorite(chatId);
            }
            case PREVIOUS -> {
                seePreviousFavorite(chatId);
            }
            case MENU -> {
                displayMenu(chatId);
            }
            default -> {
                sendErrorMessage(textMessage, chatId);
            }
        }
    }

    private void seeNextFavorite(Long chatId) {
        ProfileDto nextFavorite = rosstinderClient.getNextFavorite(chatId);

        setView(answerSender.sendPhotoWithKeyboard(chatId,
                nextFavorite.getCaption(),
                nextFavorite.getImage(),
                replyKeyboardGetter.getKeyboardForFavorites()));
    }

    private void seePreviousFavorite(Long chatId) {
        ProfileDto previousFavorite = rosstinderClient.getPreviousFavorite(chatId);

        setView(answerSender.sendPhotoWithKeyboard(chatId,
                previousFavorite.getCaption(),
                previousFavorite.getImage(),
                replyKeyboardGetter.getKeyboardForFavorites()));
    }

    private void displayMenu(Long chatId) {
        setView(answerSender.sendMessageWithKeyboard(chatId,
                AnswerText.MENU.getText(),
                replyKeyboardGetter.getKeyboardForMenu()));

        rosstinderClient.setNewStatus(chatId, BotState.MENU);
    }

    private void sendErrorMessage(String textMessage, Long chatId) {
        log.info(MessageFormat.format("Пользователь #{0} ввел неподходящее сообщение \"{1}\"", chatId, textMessage));

        setView(answerSender.sendMessageWithKeyboard(chatId, AnswerText.CHOOSE_AVAILABLE_ACTION.getText(), replyKeyboardGetter.getKeyboardForFavorites()));
    }
}
