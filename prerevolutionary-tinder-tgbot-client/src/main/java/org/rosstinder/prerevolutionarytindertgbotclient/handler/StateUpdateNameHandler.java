package org.rosstinder.prerevolutionarytindertgbotclient.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rosstinder.prerevolutionarytindertgbotclient.model.AnswerText;
import org.rosstinder.prerevolutionarytindertgbotclient.model.BotState;
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
public class StateUpdateNameHandler extends BotStateHandler {
    private final TelegramAnswerSender telegramAnswerSender;
    private final RosstinderClient rosstinderClient;
    private final ReplyKeyboardGetter replyKeyboardGetter;

    @Override
    public BotState getState() {
        return BotState.UPDATE_NAME;
    }

    @Override
    public List<Object> processState(Update update) {
        List<Object> methods = new ArrayList<>();

        String textMessage = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        if (isTextLengthNoMoreThanNCharacters(textMessage, AnswerText.TOO_LONG_NAME.getLength())) {
            rosstinderClient.setName(chatId, textMessage);
            log.info(MessageFormat.format("Для пользователя #{0} установлено имя {1}", chatId, textMessage));

            methods.add(telegramAnswerSender.sendMessageWithKeyboard(chatId, AnswerText.PROFILE.getText(), replyKeyboardGetter.getKeyboardForProfile()));
            rosstinderClient.setNewStatus(chatId, BotState.PROFILE);
        } else {
            log.info(MessageFormat.format("Пользователь #{0} ввел слишком длинное имя: \"{1}\"", chatId, textMessage));
            methods.add(telegramAnswerSender.sendMessageWithText(chatId, AnswerText.TOO_LONG_NAME.getText()));
        }
        return methods;
    }
}
