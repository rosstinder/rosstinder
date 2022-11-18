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
public class StateChooseGenderHandler extends BotStateHandler {
    private final AnswerSender answerSender;
    private final RosstinderClient rosstinderClient;
    private final ReplyKeyboardGetter replyKeyboardGetter;


    @Override
    public BotState getState() {
        return BotState.CHOOSE_GENDER;
    }

    @Override
    public void processState(Update update) {
        Long chatId = update.getMessage().getChatId();
        String textMessage = update.getMessage().getText();

        if (isGender(textMessage)) {
            rosstinderClient.setGender(chatId, textMessage);
            log.info(MessageFormat.format("Для пользователя #{0} установлен пол {1}", chatId, textMessage));
            rosstinderClient.setNewStatus(chatId, BotState.INPUT_NAME);
            setView(answerSender.sendMessageWithText(chatId, AnswerText.INPUT_NAME.getText()));
        } else {
            log.info(MessageFormat.format("Пользователь #{0} ввел неподходящее сообщение \"{1}\"", chatId, textMessage));
            setView(answerSender.sendMessageWithKeyboard(chatId, AnswerText.CHOOSE_GENDER.getText(), replyKeyboardGetter.getKeyboardForGender()));
        }
    }
}