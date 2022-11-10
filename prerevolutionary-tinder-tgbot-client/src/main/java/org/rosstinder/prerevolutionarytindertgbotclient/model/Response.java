package org.rosstinder.prerevolutionarytindertgbotclient.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Response {
    private final Long chatId;
    private final String userStatus;
    private final String status;
    private final Object attachment;
}
