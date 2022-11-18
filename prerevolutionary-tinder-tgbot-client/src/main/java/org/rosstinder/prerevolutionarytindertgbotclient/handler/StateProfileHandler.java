package org.rosstinder.prerevolutionarytindertgbotclient.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rosstinder.prerevolutionarytindertgbotclient.model.AnswerText;
import org.rosstinder.prerevolutionarytindertgbotclient.model.BotState;
import org.rosstinder.prerevolutionarytindertgbotclient.model.ButtonText;
import org.rosstinder.prerevolutionarytindertgbotclient.service.AnswerSender;
import org.rosstinder.prerevolutionarytindertgbotclient.service.ReplyKeyboardGetter;
import org.rosstinder.prerevolutionarytindertgbotclient.service.RosstinderClient;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.text.MessageFormat;

@Slf4j
@Component
@RequiredArgsConstructor
public class StateProfileHandler extends BotStateHandler {
    private final AnswerSender answerSender;
    private final RosstinderClient rosstinderClient;
    private final ReplyKeyboardGetter replyKeyboardGetter;

    @Override
    public BotState getState() {
        return BotState.PROFILE;
    }

    @Override
    public void processState(Update update) {
        isThisStartMessage(update);

        String textMessage = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        ButtonText buttonText = getButtonText(textMessage);

        switch (buttonText) {
            case CHANGE_NAME -> {
                changeName(chatId);
            }
            case CHANGE_GENDER -> {
                changeGender(chatId);
            }
            case CHANGE_DESCRIPTION -> {
                changeDescription(chatId);
            }
            case CHANGE_PREFERENCE -> {
                changePreference(chatId);
            }
            case MENU -> {
                displayMenu(chatId);
            }
            default -> {
                sendErrorMessage(textMessage, chatId);
            }
        }
    }

    private void changeName(Long chatId) {
        rosstinderClient.setNewStatus(chatId, BotState.UPDATE_NAME);

        setView(answerSender.sendMessageWithText(chatId, AnswerText.UPDATE_NAME.getText()));
    }

    private void changeGender(Long chatId) {
        rosstinderClient.setNewStatus(chatId, BotState.UPDATE_GENDER);

        setView(answerSender.sendMessageWithKeyboard(chatId, AnswerText.UPDATE_GENDER.getText(), replyKeyboardGetter.getKeyboardForGender()));
    }

    private void changeDescription(Long chatId) {
        rosstinderClient.setNewStatus(chatId, BotState.UPDATE_DESCRIPTION);

        setView(answerSender.sendMessageWithText(chatId, AnswerText.UPDATE_DESCRIPTION.getText()));
    }

    private void changePreference(Long chatId) {
        rosstinderClient.setNewStatus(chatId, BotState.UPDATE_PREFERENCE);

        setView(answerSender.sendMessageWithKeyboard(chatId, AnswerText.UPDATE_PREFERENCE.getText(), replyKeyboardGetter.getKeyboardForPreference()));
    }

    private void displayMenu(Long chatId) {
        setView(answerSender.sendMessageWithKeyboard(chatId,
                AnswerText.MENU.getText(),
                replyKeyboardGetter.getKeyboardForMenu()));

        rosstinderClient.setNewStatus(chatId, BotState.MENU);
    }

    private void sendErrorMessage(String textMessage, Long chatId) {
        log.info(MessageFormat.format("Пользователь #{0} ввел неподходящее сообщение \"{1}\"", chatId, textMessage));

        setView(answerSender.sendMessageWithKeyboard(chatId, AnswerText.CHOOSE_AVAILABLE_ACTION.getText(), replyKeyboardGetter.getKeyboardForProfile()));
    }
}
