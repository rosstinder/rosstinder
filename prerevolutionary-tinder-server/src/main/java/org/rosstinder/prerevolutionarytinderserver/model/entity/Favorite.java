package org.rosstinder.prerevolutionarytinderserver.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "favorites", schema = "rosstinder")
public class Favorite {
    public final static boolean LIKE = true;
    public final static boolean DISLIKE = false;

    @Column(name = "id", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "who_chat_id", nullable = false)
    private Long whoChatId;

    @Column(name = "whom_chat_id", nullable = false)
    private Long whomChatId;

    @Setter
    @Column(name = "is_like")
    private boolean isLike;

    public Favorite(Long who, Long whom, boolean isLike) {
        this.whoChatId = who;
        this.whomChatId = whom;
        this.isLike = isLike;
    }
}
