package org.rosstinder.prerevolutionarytindertgbotclient.model;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto {
    private String userStatus;
    private String status;
    private String message;
    private String image;
}
