package org.rosstinder.prerevolutionarytinderserver.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor
public class Response {
    private final Long chatId;
    private final String userStatus;
    private final String status;
    private final Object attachment;
}
