package org.rosstinder.prerevolutionarytindertgbotclient.handler;

import org.rosstinder.prerevolutionarytindertgbotclient.model.AnswerText;
import org.rosstinder.prerevolutionarytindertgbotclient.model.BotState;
import org.rosstinder.prerevolutionarytindertgbotclient.service.AnswerSender;
import org.rosstinder.prerevolutionarytindertgbotclient.service.ReplyKeyboardGetter;
import org.rosstinder.prerevolutionarytindertgbotclient.service.RosstinderClient;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

@Component
public class StateNewHandler extends BotStateHandler {

    private final AnswerSender answerSender;
    private final RosstinderClient rosstinderClient;
    private final ReplyKeyboardGetter replyKeyboardGetter;

    public StateNewHandler(AnswerSender answerSender, RosstinderClient rosstinderClient, ReplyKeyboardGetter replyKeyboardGetter) {
        this.answerSender = answerSender;
        this.rosstinderClient = rosstinderClient;
        this.replyKeyboardGetter = replyKeyboardGetter;
    }

    @Override
    public BotState getState() {
        return BotState.NEW;
    }

    @Override
    public void processState(Update update) {
        Long chatId = update.getMessage().getChatId();

        ReplyKeyboardMarkup keyboard = replyKeyboardGetter.getKeyboardForGender();
        rosstinderClient.setNewStatus(chatId, BotState.CHOOSE_GENDER);
        setView(answerSender.sendMessageWithKeyboard(chatId, AnswerText.CHOOSE_GENDER.getText(), keyboard));
    }
}
