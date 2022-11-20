package org.rosstinder.prerevolutionarytindertgbotclient.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rosstinder.prerevolutionarytindertgbotclient.model.AnswerText;
import org.rosstinder.prerevolutionarytindertgbotclient.model.BotState;
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
public class StateInputNameHandler extends BotStateHandler {
    private final TelegramAnswerSender telegramAnswerSender;
    private final RosstinderClientImpl rosstinderClientImpl;

    @Override
    public BotState getState() {
        return BotState.INPUT_NAME;
    }

    @Override
    public List<Object> processState(Update update) {
        List<Object> methods = new ArrayList<>();

        Long chatId = update.getMessage().getChatId();
        String textMessage = update.getMessage().getText();

        if (isTextLengthNoMoreThanNCharacters(textMessage, AnswerText.TOO_LONG_NAME.getLength())) {
            rosstinderClientImpl.setName(chatId, textMessage);
            log.info(MessageFormat.format("Для пользователя #{0} установлено имя {1}", chatId, textMessage));

            methods.add(telegramAnswerSender.sendMessageWithText(chatId, AnswerText.INPUT_DESCRIPTION.getText()));
            rosstinderClientImpl.setNewStatus(chatId, BotState.INPUT_DESCRIPTION);
        } else {
            log.info(MessageFormat.format("Пользователь #{0} ввел слишком длинное имя: \"{1}\"", chatId, textMessage));
            methods.add(telegramAnswerSender.sendMessageWithText(chatId, AnswerText.TOO_LONG_NAME.getText()));
        }
        return methods;
    }
}
