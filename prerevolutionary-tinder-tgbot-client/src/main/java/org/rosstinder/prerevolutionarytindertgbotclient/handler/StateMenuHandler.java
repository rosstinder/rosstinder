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
import java.util.ArrayList;
import java.util.List;

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
    public List<Object> processState(Update update) {

        Long chatId = update.getMessage().getChatId();
        String textMessage = update.getMessage().getText();

        List<Object> methods = new ArrayList<>();

        if (isThisStartMessage(textMessage)) {
            methods.add(replyToStartMessage(chatId));
        }

        ButtonText buttonText = getButtonText(textMessage);

        switch (buttonText) {
            case SEARCH -> {
                return search(chatId, methods);
            }
            case FAVORITES -> {
                return displayFavorites(chatId, methods);
            }
            case PROFILE -> {
                return displayProfile(chatId, methods);
            }
            default -> {
                return displayMenuAgain(chatId, textMessage, methods);
            }
        }
    }

    private List<Object> search(Long chatId, List<Object> methods) {
        ProfileDto nextProfile = rosstinderClient.getNextProfile(chatId);

        methods.add(answerSender.sendPhotoWithKeyboard(chatId,
                nextProfile.getCaption(),
                nextProfile.getImage(),
                replyKeyboardGetter.getKeyboardForSearch()));

        rosstinderClient.setNewStatus(chatId, BotState.SEARCH);
        return methods;
    }

    private List<Object> displayFavorites(Long chatId, List<Object> methods) {
        ProfileDto nextFavorite = rosstinderClient.getNextFavorite(chatId);

        methods.add(answerSender.sendPhotoWithKeyboard(chatId,
                nextFavorite.getCaption(),
                nextFavorite.getImage(),
                replyKeyboardGetter.getKeyboardForFavorites()));

        rosstinderClient.setNewStatus(chatId, BotState.FAVORITES);
        return methods;
    }

    private List<Object> displayProfile(Long chatId, List<Object> methods) {
        ProfileDto profile = rosstinderClient.getProfile(chatId);

        methods.add(answerSender.sendPhotoWithKeyboard(chatId,
                profile.getCaption(),
                profile.getImage(),
                replyKeyboardGetter.getKeyboardForProfile()));

        rosstinderClient.setNewStatus(chatId, BotState.PROFILE);
        return methods;
    }

    private List<Object> displayMenuAgain(Long chatId, String textMessage, List<Object> methods) {
        log.info(MessageFormat.format("Пользователь #{0} ввел неподходящее сообщение \"{1}\"", chatId, textMessage));

        methods.add(answerSender.sendMessageWithKeyboard(chatId,
                AnswerText.CHOOSE_AVAILABLE_ACTION.getText(),
                replyKeyboardGetter.getKeyboardForMenu()));
        return methods;
    }
}
