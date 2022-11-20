package org.rosstinder.prerevolutionarytindertgbotclient.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rosstinder.prerevolutionarytindertgbotclient.model.AnswerText;
import org.rosstinder.prerevolutionarytindertgbotclient.model.BotState;
import org.rosstinder.prerevolutionarytindertgbotclient.service.ReplyKeyboardGetter;
import org.rosstinder.prerevolutionarytindertgbotclient.service.RosstinderClientImpl;
import org.rosstinder.prerevolutionarytindertgbotclient.service.TelegramAnswerSender;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StateNewHandler extends BotStateHandler {

    private final TelegramAnswerSender telegramAnswerSender;
    private final RosstinderClientImpl rosstinderClientImpl;
    private final ReplyKeyboardGetter replyKeyboardGetter;

    @Override
    public BotState getState() {
        return BotState.NEW;
    }

    @Override
    public List<Object> processState(Update update) {
        List<Object> methods = new ArrayList<>();

        Long chatId = update.getMessage().getChatId();

        ReplyKeyboardMarkup keyboard = replyKeyboardGetter.getKeyboardForGender();
        rosstinderClientImpl.setNewStatus(chatId, BotState.CHOOSE_GENDER);
        methods.add(telegramAnswerSender.sendMessageWithKeyboard(chatId, AnswerText.CHOOSE_GENDER.getText(), keyboard));

        return methods;
    }
}
