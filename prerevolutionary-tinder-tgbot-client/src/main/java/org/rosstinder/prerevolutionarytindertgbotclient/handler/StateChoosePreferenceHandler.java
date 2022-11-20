package org.rosstinder.prerevolutionarytindertgbotclient.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rosstinder.prerevolutionarytindertgbotclient.model.AnswerText;
import org.rosstinder.prerevolutionarytindertgbotclient.model.BotState;
import org.rosstinder.prerevolutionarytindertgbotclient.model.ProfileDto;
import org.rosstinder.prerevolutionarytindertgbotclient.service.AnswerSender;
import org.rosstinder.prerevolutionarytindertgbotclient.service.ReplyKeyboardGetter;
import org.rosstinder.prerevolutionarytindertgbotclient.service.RosstinderClient;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StateChoosePreferenceHandler extends BotStateHandler {
    private final AnswerSender answerSender;
    private final RosstinderClient rosstinderClient;
    private final ReplyKeyboardGetter replyKeyboardGetter;

    @Override
    public BotState getState() {
        return BotState.CHOOSE_PREFERENCE;
    }

    @Override
    public List<Object> processState(Update update) {
        List<Object> methods = new ArrayList<>();

        Long chatId = update.getMessage().getChatId();
        String textMessage = update.getMessage().getText();

        if (isPreference(textMessage)) {
            rosstinderClient.setPreference(chatId, textMessage);
            log.info(MessageFormat.format("Для пользователя #{0} установлены предпочтения {1}", chatId, textMessage));

            ProfileDto profile = rosstinderClient.getProfile(chatId);

            methods.add(answerSender.sendPhotoWithCaption(chatId, profile.getCaption(), profile.getImage()));

            methods.add(answerSender.sendMessageWithKeyboard(chatId, AnswerText.MENU.getText(), replyKeyboardGetter.getKeyboardForMenu()));
            rosstinderClient.setNewStatus(chatId, BotState.MENU);
        } else {
            log.info(MessageFormat.format("Пользователь #{0} ввел неподходящее сообщение \"{1}\"", chatId, textMessage));
            methods.add(answerSender.sendMessageWithKeyboard(chatId, AnswerText.CHOOSE_PREFERENCE.getText(), replyKeyboardGetter.getKeyboardForPreference()));
        }
        return methods;
    }
}
