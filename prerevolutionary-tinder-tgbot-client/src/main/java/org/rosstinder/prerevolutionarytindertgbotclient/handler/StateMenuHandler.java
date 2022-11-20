package org.rosstinder.prerevolutionarytindertgbotclient.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rosstinder.prerevolutionarytindertgbotclient.model.AnswerText;
import org.rosstinder.prerevolutionarytindertgbotclient.model.BotState;
import org.rosstinder.prerevolutionarytindertgbotclient.model.ButtonText;
import org.rosstinder.prerevolutionarytindertgbotclient.model.ProfileDto;
import org.rosstinder.prerevolutionarytindertgbotclient.service.ReplyKeyboardGetter;
import org.rosstinder.prerevolutionarytindertgbotclient.service.RosstinderClientImpl;
import org.rosstinder.prerevolutionarytindertgbotclient.service.TelegramAnswerSender;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StateMenuHandler extends BotStateHandler {
    private final TelegramAnswerSender telegramAnswerSender;
    private final RosstinderClientImpl rosstinderClientImpl;
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
        ProfileDto nextProfile = rosstinderClientImpl.getNextProfile(chatId);

        methods.add(telegramAnswerSender.sendPhotoWithKeyboard(chatId,
                nextProfile.getCaption(),
                nextProfile.getImage(),
                replyKeyboardGetter.getKeyboardForSearch()));

        rosstinderClientImpl.setNewStatus(chatId, BotState.SEARCH);
        return methods;
    }

    private List<Object> displayFavorites(Long chatId, List<Object> methods) {
        ProfileDto nextFavorite = rosstinderClientImpl.getNextFavorite(chatId);

        methods.add(telegramAnswerSender.sendPhotoWithKeyboard(chatId,
                nextFavorite.getCaption(),
                nextFavorite.getImage(),
                replyKeyboardGetter.getKeyboardForFavorites()));

        rosstinderClientImpl.setNewStatus(chatId, BotState.FAVORITES);
        return methods;
    }

    private List<Object> displayProfile(Long chatId, List<Object> methods) {
        ProfileDto profile = rosstinderClientImpl.getProfile(chatId);

        methods.add(telegramAnswerSender.sendPhotoWithKeyboard(chatId,
                profile.getCaption(),
                profile.getImage(),
                replyKeyboardGetter.getKeyboardForProfile()));

        rosstinderClientImpl.setNewStatus(chatId, BotState.PROFILE);
        return methods;
    }

    private List<Object> displayMenuAgain(Long chatId, String textMessage, List<Object> methods) {
        log.info(MessageFormat.format("Пользователь #{0} ввел неподходящее сообщение \"{1}\"", chatId, textMessage));

        methods.add(telegramAnswerSender.sendMessageWithKeyboard(chatId,
                AnswerText.CHOOSE_AVAILABLE_ACTION.getText(),
                replyKeyboardGetter.getKeyboardForMenu()));
        return methods;
    }
}
