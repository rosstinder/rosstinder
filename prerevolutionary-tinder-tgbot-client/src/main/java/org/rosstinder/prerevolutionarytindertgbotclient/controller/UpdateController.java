package org.rosstinder.prerevolutionarytindertgbotclient.controller;

import lombok.extern.slf4j.Slf4j;
import org.rosstinder.prerevolutionarytindertgbotclient.handler.BotStateHandler;
import org.rosstinder.prerevolutionarytindertgbotclient.model.BotState;
import org.rosstinder.prerevolutionarytindertgbotclient.service.RosstinderClient;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UpdateController {
    private final RosstinderClient rosstinderClient;
    private final Map<BotState, BotStateHandler> handlers;

    public UpdateController(RosstinderClient rosstinderClient, List<BotStateHandler> handlers) {
        this.rosstinderClient = rosstinderClient;
        this.handlers = handlers.stream().collect(Collectors.toMap(BotStateHandler::getState, Function.identity()));
    }

    public void processUpdate(Update update) {
        if (update == null) {
            log.debug("Получен пустой update");
            return;
        }
        if (update.getMessage() == null) {
            log.debug("Получено пустое сообщение");
            return;
        }
        if (update.getMessage().getText() != null) {
            processTextMessage(update);
        }
        log.debug("Получен неподдерживаемый тип сообщения");
    }

    private void processTextMessage(Update update) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        BotState userStatus = BotState.valueOfLabel(rosstinderClient.getUserStatus(chatId));
        log.info(MessageFormat.format("Получено сообщение \"{0}\" от пользователя #{1} со статусом \"{2}\"",
                message.getText(), chatId, userStatus));

        if (handlers.containsKey(userStatus)) {
            handlers.get(userStatus).processState(update);
        }
    }
}
