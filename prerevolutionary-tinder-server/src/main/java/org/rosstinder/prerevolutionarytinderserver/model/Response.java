package org.rosstinder.prerevolutionarytinderserver.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Response {
    private final String userStatus;
    private final String status;
    private final String message;
    private final String image;
}
