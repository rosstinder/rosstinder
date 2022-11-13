package org.rosstinder.prerevolutionarytindertgbotclient.model;

import lombok.Data;

@Data
public class ResponseDto {
    private Long chatId;
    private String userStatus;
    private String status;
    private Object attachment;
    private Object attachment2;
}
