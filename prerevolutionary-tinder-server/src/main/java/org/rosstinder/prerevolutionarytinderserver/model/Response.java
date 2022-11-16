package org.rosstinder.prerevolutionarytinderserver.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.rosstinder.prerevolutionarytinderserver.model.entity.Profile;
import org.rosstinder.prerevolutionarytinderserver.model.entity.User;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor
public class Response {
    private final String userStatus;
    private final String status;
    private final String message;
    private final String image;
}
