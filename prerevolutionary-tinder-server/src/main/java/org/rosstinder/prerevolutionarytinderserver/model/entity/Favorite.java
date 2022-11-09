package org.rosstinder.prerevolutionarytinderserver.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@AllArgsConstructor
@Entity
@Table(name = "favorites", schema = "rosstinder")
public class Favorite {
    public final static boolean LIKE = true;
    public final static boolean DISLIKE = false;

    @Column(name = "who_chat_id", nullable = false)
    private final Long whoChatId;

    @Column(name = "whom_chat_id", nullable = false)
    private final Long whomChatId;

    @Setter
    @Column(name = "is_like")
    private boolean isLike;

    public Favorite(Long who, Long whom) {
        this.whoChatId = who;
        this.whomChatId = whom;
    }
}
