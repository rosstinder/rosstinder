package org.rosstinder.prerevolutionarytindertgbotclient.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.LinkedHashMap;

@Data
public class ResponseDto {
    private Long chatId;
    private String userStatus;
    private String status;
    private Object attachment;
    private Object attachment2;
}
