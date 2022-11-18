package org.rosstinder.prerevolutionarytindertgbotclient.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rosstinder.prerevolutionarytindertgbotclient.model.AnswerText;
import org.rosstinder.prerevolutionarytindertgbotclient.model.BotState;
import org.rosstinder.prerevolutionarytindertgbotclient.service.AnswerSender;
import org.rosstinder.prerevolutionarytindertgbotclient.service.RosstinderClient;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.text.MessageFormat;

@Slf4j
@Component
@RequiredArgsConstructor
public class StateInputNameHandler extends BotStateHandler {
    private final RosstinderClient rosstinderClient;
    private final AnswerSender answerSender;

    @Override
    public BotState getState() {
        return BotState.INPUT_NAME;
    }

    @Override
    public void processState(Update update) {
        Long chatId = update.getMessage().getChatId();
        String textMessage = update.getMessage().getText();

        if (isTextLengthNoMoreThanNCharacters(textMessage, AnswerText.TOO_LONG_NAME.getLength())) {
            rosstinderClient.setName(chatId, textMessage);
            log.info(MessageFormat.format("Для пользователя #{0} установлено имя {1}", chatId, textMessage));

            setView(answerSender.sendMessageWithText(chatId, AnswerText.INPUT_DESCRIPTION.getText()));
            rosstinderClient.setNewStatus(chatId, BotState.INPUT_DESCRIPTION);
        } else {
            log.info(MessageFormat.format("Пользователь #{0} ввел слишком длинное имя: \"{1}\"", chatId, textMessage));
            setView(answerSender.sendMessageWithText(chatId, AnswerText.TOO_LONG_NAME.getText()));
        }
    }
}
