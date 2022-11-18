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
public class StateSearchHandler extends BotStateHandler {
    private final AnswerSender answerSender;
    private final RosstinderClient rosstinderClient;
    private final ReplyKeyboardGetter replyKeyboardGetter;

    @Override
    public BotState getState() {
        return BotState.SEARCH;
    }

    @Override
    public void processState(Update update) {
        isThisStartMessage(update);

        String textMessage = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        ButtonText buttonText = getButtonText(textMessage);

        switch (buttonText) {
            case LIKE -> {
                likeThisProfileAndSeeNextOne(chatId);
            }
            case DONT_LIKE -> {
                dontLikeThisProfileAndSeeNextOne(chatId);
            }
            case MENU -> {
                displayMenu(chatId);
            }
            default -> {
                sendErrorMessage(chatId, textMessage);
            }
        }
    }

    private void likeThisProfileAndSeeNextOne(Long chatId) {
        rosstinderClient.setLikeOrDislike(chatId, "true");

        if (!rosstinderClient.getRelationship(chatId).equals("")) {
            setView(answerSender.sendMessageWithText(chatId, AnswerText.MUTUAL_LOVE.getText()));

            log.info(MessageFormat.format("Обнаружена взаимная симпатия для пользователя #{0}", chatId));
        }

        ProfileDto nextProfile = rosstinderClient.getNextProfile(chatId);

        setView(answerSender.sendPhotoWithKeyboard(chatId,
                nextProfile.getCaption(),
                nextProfile.getImage(),
                replyKeyboardGetter.getKeyboardForSearch()));
    }

    private void dontLikeThisProfileAndSeeNextOne(Long chatId) {
        rosstinderClient.setLikeOrDislike(chatId, "false");

        ProfileDto nextProfile = rosstinderClient.getNextProfile(chatId);

        setView(answerSender.sendPhotoWithKeyboard(chatId,
                nextProfile.getCaption(),
                nextProfile.getImage(),
                replyKeyboardGetter.getKeyboardForSearch()));
    }

    private void displayMenu(Long chatId) {
        setView(answerSender.sendMessageWithKeyboard(chatId,
                AnswerText.MENU.getText(),
                replyKeyboardGetter.getKeyboardForMenu()));

        rosstinderClient.setNewStatus(chatId, BotState.MENU);
    }

    private void sendErrorMessage(Long chatId, String textMessage) {
        log.info(MessageFormat.format("Пользователь #{0} ввел неподходящее сообщение \"{1}\"", chatId, textMessage));

        setView(answerSender.sendMessageWithKeyboard(chatId, AnswerText.CHOOSE_AVAILABLE_ACTION.getText(), replyKeyboardGetter.getKeyboardForSearch()));
    }
}
