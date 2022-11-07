package org.rosstinder.prerevolutionarytinderserver.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class Like {
    private final Long whoChatId;

    private final Long whomChatId;

    @Setter
    private boolean isLike;

    public Like(Long who, Long whom) {
        this.whoChatId = who;
        this.whomChatId = whom;
    }
}
