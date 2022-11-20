package org.rosstinder.prerevolutionarytindertgbotclient.handler;

import lombok.extern.slf4j.Slf4j;
import org.rosstinder.prerevolutionarytindertgbotclient.model.*;
import org.rosstinder.prerevolutionarytindertgbotclient.service.AnswerSender;
import org.rosstinder.prerevolutionarytindertgbotclient.service.RosstinderClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.text.MessageFormat;
import java.util.List;

@Slf4j
@Component
public abstract class BotStateHandler {
    private AnswerSender answerSender;
    private RosstinderClient rosstinderClient;

    @Autowired
    public void setAnswerSender(AnswerSender answerSender) {
        this.answerSender = answerSender;
    }

    @Autowired
    public void setRosstinderClient(RosstinderClient rosstinderClient) {
        this.rosstinderClient = rosstinderClient;
    }

    public abstract BotState getState();

    public abstract List<Object> processState(Update update);

    protected SendPhoto replyToStartMessage(Long chatId) {
        log.info(MessageFormat.format("От пользователя #{0} получена команда /start", chatId));
        ProfileDto profile = rosstinderClient.getNextFavorite(chatId);

        return answerSender.sendPhotoWithCaption(chatId,
                profile.getCaption(),
                profile.getImage());
    }

    protected boolean isThisStartMessage(String textMessage) {
        return textMessage.equals("/start");
    }

    protected ButtonText getButtonText(String message) {
        return ButtonText.valueOfLabel(message).orElse(ButtonText.NO_BUTTON);
    }

    protected boolean isGender(String message) {
        return Gender.valueOfLabel(message).isPresent();
    }

    protected boolean isPreference(String message) {
        return Preference.valueOfLabel(message).isPresent();

    }

    protected boolean isTextLengthNoMoreThanNCharacters(String text, int N) {
        return text.length() <= N;
    }
}
