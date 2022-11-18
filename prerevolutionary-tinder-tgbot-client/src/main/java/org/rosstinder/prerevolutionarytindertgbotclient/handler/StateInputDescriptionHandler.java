package org.rosstinder.prerevolutionarytindertgbotclient.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rosstinder.prerevolutionarytindertgbotclient.model.AnswerText;
import org.rosstinder.prerevolutionarytindertgbotclient.model.BotState;
import org.rosstinder.prerevolutionarytindertgbotclient.service.AnswerSender;
import org.rosstinder.prerevolutionarytindertgbotclient.service.ReplyKeyboardGetter;
import org.rosstinder.prerevolutionarytindertgbotclient.service.RosstinderClient;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.text.MessageFormat;

@Slf4j
@Component
@RequiredArgsConstructor
public class StateInputDescriptionHandler extends BotStateHandler {
    private final AnswerSender answerSender;
    private final RosstinderClient rosstinderClient;
    private final ReplyKeyboardGetter replyKeyboardGetter;

    @Override
    public BotState getState() {
        return BotState.INPUT_DESCRIPTION;
    }

    @Override
    public void processState(Update update) {
        Long chatId = update.getMessage().getChatId();
        String textMessage = update.getMessage().getText();

        if (isTextLengthNoMoreThanNCharacters(textMessage, AnswerText.TOO_LONG_DESCRIPTION.getLength())) {
            rosstinderClient.setDescription(chatId, textMessage);
            log.info(MessageFormat.format("Для пользователя #{0} установлено описание {1}", chatId, textMessage));

            setView(answerSender.sendMessageWithKeyboard(chatId, AnswerText.CHOOSE_PREFERENCE.getText(), replyKeyboardGetter.getKeyboardForPreference()));
            rosstinderClient.setNewStatus(chatId, BotState.CHOOSE_PREFERENCE);
        } else {
            log.info(MessageFormat.format("Пользователь #{0} ввел слишком длинное описание: \"{1}\"", chatId, textMessage));
            setView(answerSender.sendMessageWithText(chatId, AnswerText.TOO_LONG_DESCRIPTION.getText()));
        }
    }
}
