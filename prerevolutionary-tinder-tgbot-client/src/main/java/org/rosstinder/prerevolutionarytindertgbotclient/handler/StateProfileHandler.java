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
import java.util.ArrayList;
import java.util.List;

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
    public List<Object> processState(Update update) {

        String textMessage = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        List<Object> methods = new ArrayList<>();

        if (isThisStartMessage(textMessage)) {
            methods.add(replyToStartMessage(chatId));
        }

        ButtonText buttonText = getButtonText(textMessage);

        switch (buttonText) {
            case CHANGE_NAME -> {
                return changeName(chatId, methods);
            }
            case CHANGE_GENDER -> {
                return changeGender(chatId, methods);
            }
            case CHANGE_DESCRIPTION -> {
                return changeDescription(chatId, methods);
            }
            case CHANGE_PREFERENCE -> {
                return changePreference(chatId, methods);
            }
            case MENU -> {
                return displayMenu(chatId, methods);
            }
            default -> {
                return sendErrorMessage(textMessage, chatId, methods);
            }
        }
    }

    private List<Object> changeName(Long chatId, List<Object> methods) {
        rosstinderClient.setNewStatus(chatId, BotState.UPDATE_NAME);

        methods.add(answerSender.sendMessageWithText(chatId, AnswerText.UPDATE_NAME.getText()));
        return methods;
    }

    private List<Object> changeGender(Long chatId, List<Object> methods) {
        rosstinderClient.setNewStatus(chatId, BotState.UPDATE_GENDER);

        methods.add(answerSender.sendMessageWithKeyboard(chatId, AnswerText.UPDATE_GENDER.getText(), replyKeyboardGetter.getKeyboardForGender()));
        return methods;
    }

    private List<Object> changeDescription(Long chatId, List<Object> methods) {
        rosstinderClient.setNewStatus(chatId, BotState.UPDATE_DESCRIPTION);

        methods.add(answerSender.sendMessageWithText(chatId, AnswerText.UPDATE_DESCRIPTION.getText()));
        return methods;
    }

    private List<Object> changePreference(Long chatId, List<Object> methods) {
        rosstinderClient.setNewStatus(chatId, BotState.UPDATE_PREFERENCE);

        methods.add(answerSender.sendMessageWithKeyboard(chatId, AnswerText.UPDATE_PREFERENCE.getText(), replyKeyboardGetter.getKeyboardForPreference()));
        return methods;
    }

    private List<Object> displayMenu(Long chatId, List<Object> methods) {
        methods.add(answerSender.sendMessageWithKeyboard(chatId,
                AnswerText.MENU.getText(),
                replyKeyboardGetter.getKeyboardForMenu()));

        rosstinderClient.setNewStatus(chatId, BotState.MENU);
        return methods;
    }

    private List<Object> sendErrorMessage(String textMessage, Long chatId, List<Object> methods) {
        log.info(MessageFormat.format("Пользователь #{0} ввел неподходящее сообщение \"{1}\"", chatId, textMessage));

        methods.add(answerSender.sendMessageWithKeyboard(chatId, AnswerText.CHOOSE_AVAILABLE_ACTION.getText(), replyKeyboardGetter.getKeyboardForProfile()));
        return methods;
    }
}
