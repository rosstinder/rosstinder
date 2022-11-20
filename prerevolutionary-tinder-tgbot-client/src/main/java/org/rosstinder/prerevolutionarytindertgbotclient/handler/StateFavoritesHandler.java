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
public class StateFavoritesHandler extends BotStateHandler {
    private final TelegramAnswerSender telegramAnswerSender;
    private final RosstinderClient rosstinderClient;
    private final ReplyKeyboardGetter replyKeyboardGetter;

    @Override
    public BotState getState() {
        return BotState.FAVORITES;
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
            case NEXT -> {
                return displayNextFavorite(chatId, methods);
            }
            case PREVIOUS -> {
                return displayPreviousFavorite(chatId, methods);
            }
            case MENU -> {
                return displayMenu(chatId, methods);
            }
            default -> {
                return sendErrorMessage(textMessage, chatId, methods);
            }
        }
    }

    private List<Object> displayNextFavorite(Long chatId, List<Object> methods) {
        ProfileDto nextFavorite = rosstinderClient.getNextFavorite(chatId);

        methods.add(telegramAnswerSender.sendPhotoWithKeyboard(chatId,
                nextFavorite.getCaption(),
                nextFavorite.getImage(),
                replyKeyboardGetter.getKeyboardForFavorites()));
        return methods;
    }

    private List<Object> displayPreviousFavorite(Long chatId, List<Object> methods) {
        ProfileDto previousFavorite = rosstinderClient.getPreviousFavorite(chatId);

        methods.add(telegramAnswerSender.sendPhotoWithKeyboard(chatId,
                previousFavorite.getCaption(),
                previousFavorite.getImage(),
                replyKeyboardGetter.getKeyboardForFavorites()));
        return methods;
    }

    private List<Object> displayMenu(Long chatId, List<Object> methods) {
        methods.add(telegramAnswerSender.sendMessageWithKeyboard(chatId,
                AnswerText.MENU.getText(),
                replyKeyboardGetter.getKeyboardForMenu()));

        rosstinderClient.setNewStatus(chatId, BotState.MENU);
        return methods;
    }

    private List<Object> sendErrorMessage(String textMessage, Long chatId, List<Object> methods) {
        log.info(MessageFormat.format("Пользователь #{0} ввел неподходящее сообщение \"{1}\"", chatId, textMessage));

        methods.add(telegramAnswerSender.sendMessageWithKeyboard(chatId, AnswerText.CHOOSE_AVAILABLE_ACTION.getText(), replyKeyboardGetter.getKeyboardForFavorites()));
        return methods;
    }
}
