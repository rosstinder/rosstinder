package org.rosstinder.prerevolutionarytindertgbotclient.controller;

import lombok.extern.slf4j.Slf4j;
import org.rosstinder.prerevolutionarytindertgbotclient.handler.BotStateHandler;
import org.rosstinder.prerevolutionarytindertgbotclient.model.BotState;
import org.rosstinder.prerevolutionarytindertgbotclient.service.RosstinderClientImpl;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UpdateController {
    private final RosstinderClientImpl rosstinderClientImpl;
    private final Map<BotState, BotStateHandler> handlers;

    public UpdateController(RosstinderClientImpl rosstinderClientImpl, List<BotStateHandler> handlers) {
        this.rosstinderClientImpl = rosstinderClientImpl;
        this.handlers = handlers.stream().collect(Collectors.toMap(BotStateHandler::getState, Function.identity()));
    }

    public List<Object> processUpdate(Update update) {
        if (update == null) {
            log.debug("Получен пустой update");
            return Collections.emptyList();
        }
        if (update.getMessage() == null) {
            log.debug("Получено пустое сообщение");
            return Collections.emptyList();
        }
        if (update.getMessage().getText() != null) {
            return processTextMessage(update);
        }
        log.debug("Получен неподдерживаемый тип сообщения");
        return Collections.emptyList();
    }

    private List<Object> processTextMessage(Update update) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        BotState userStatus = BotState.valueOfLabel(rosstinderClientImpl.getUserStatus(chatId));
        log.info(MessageFormat.format("Получено сообщение \"{0}\" от пользователя #{1} со статусом \"{2}\"",
                message.getText(), chatId, userStatus));

        if (handlers.containsKey(userStatus)) {
            return handlers.get(userStatus).processState(update);
        }
        return Collections.emptyList();
    }
}
