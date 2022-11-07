package org.rosstinder.prerevolutionarytinderserver.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class Favorite {
    public final static boolean LIKE = true;
    public final static boolean DISLIKE = false;
    private final Long whoChatId;

    private final Long whomChatId;

    @Setter
    private boolean isLike;

    public Favorite(Long who, Long whom) {
        this.whoChatId = who;
        this.whomChatId = whom;
    }
}
