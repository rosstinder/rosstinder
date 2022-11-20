package org.rosstinder.prerevolutionarytindertgbotclient.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rosstinder.prerevolutionarytindertgbotclient.model.AnswerText;
import org.rosstinder.prerevolutionarytindertgbotclient.model.BotState;
import org.rosstinder.prerevolutionarytindertgbotclient.model.ButtonText;
import org.rosstinder.prerevolutionarytindertgbotclient.model.ProfileDto;
import org.rosstinder.prerevolutionarytindertgbotclient.service.ReplyKeyboardGetter;
import org.rosstinder.prerevolutionarytindertgbotclient.service.RosstinderClient;
import org.rosstinder.prerevolutionarytindertgbotclient.service.TelegramAnswerSender;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StateSearchHandler extends BotStateHandler {
    private final TelegramAnswerSender telegramAnswerSender;
    private final RosstinderClient rosstinderClient;
    private final ReplyKeyboardGetter replyKeyboardGetter;

    @Override
    public BotState getState() {
        return BotState.SEARCH;
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
            case LIKE -> {
                return likeThisProfileAndDisplayNextOne(chatId, methods);
            }
            case DONT_LIKE -> {
                return dontLikeThisProfileAndDisplayNextOne(chatId, methods);
            }
            case MENU -> {
                return displayMenu(chatId, methods);
            }
            default -> {
                return sendErrorMessage(chatId, textMessage, methods);
            }
        }
    }

    private List<Object> likeThisProfileAndDisplayNextOne(Long chatId, List<Object> methods) {
        rosstinderClient.setLikeOrDislike(chatId, "true");

        if (!rosstinderClient.getRelationship(chatId).equals("")) {
            methods.add(telegramAnswerSender.sendMessageWithText(chatId, AnswerText.MUTUAL_LOVE.getText()));

            log.info(MessageFormat.format("Обнаружена взаимная симпатия для пользователя #{0}", chatId));
        }

        ProfileDto nextProfile = rosstinderClient.getNextProfile(chatId);

        methods.add(telegramAnswerSender.sendPhotoWithKeyboard(chatId,
                nextProfile.getCaption(),
                nextProfile.getImage(),
                replyKeyboardGetter.getKeyboardForSearch()));

        return methods;
    }

    private List<Object> dontLikeThisProfileAndDisplayNextOne(Long chatId, List<Object> methods) {
        rosstinderClient.setLikeOrDislike(chatId, "false");

        ProfileDto nextProfile = rosstinderClient.getNextProfile(chatId);

        methods.add(telegramAnswerSender.sendPhotoWithKeyboard(chatId,
                nextProfile.getCaption(),
                nextProfile.getImage(),
                replyKeyboardGetter.getKeyboardForSearch()));
        return methods;
    }

    private List<Object> displayMenu(Long chatId, List<Object> methods) {
        methods.add(telegramAnswerSender.sendMessageWithKeyboard(chatId,
                AnswerText.MENU.getText(),
                replyKeyboardGetter.getKeyboardForMenu()));

        rosstinderClient.setNewStatus(chatId, BotState.MENU);
        return methods;
    }

    private List<Object> sendErrorMessage(Long chatId, String textMessage, List<Object> methods) {
        log.info(MessageFormat.format("Пользователь #{0} ввел неподходящее сообщение \"{1}\"", chatId, textMessage));

        methods.add(telegramAnswerSender.sendMessageWithKeyboard(chatId, AnswerText.CHOOSE_AVAILABLE_ACTION.getText(), replyKeyboardGetter.getKeyboardForSearch()));
        return methods;
    }
}
