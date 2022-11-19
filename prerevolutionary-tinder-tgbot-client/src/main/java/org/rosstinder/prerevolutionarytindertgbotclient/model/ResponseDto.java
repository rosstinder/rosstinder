package org.rosstinder.prerevolutionarytindertgbotclient.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
