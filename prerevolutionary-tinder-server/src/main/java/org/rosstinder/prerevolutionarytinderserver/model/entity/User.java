package org.rosstinder.prerevolutionarytinderserver.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "users", schema = "rosstinder")
public class User {
    public final static Long ZERO_VALUE = Long.valueOf(0);

//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
//    @Column(name = "id")
//    private Long id;
    @Column(name = "chat_id", unique = true, nullable = false)
    private Long chatId;
    @Column(name = "status")
    @Setter
    private String status;

    @Column(name = "last_favorite_num")
    @Setter
    private Long lastFavoriteNumber;
    @Column(name = "last_profile_num")
    @Setter
    private Long lastProfileNumber;

    public User(Long chatId, String status) {
        //this.id = id;
        this.chatId = chatId;
        this.status = status;
        this.lastFavoriteNumber = ZERO_VALUE;
        this.lastProfileNumber = ZERO_VALUE;
    }

    @Override
    public String toString() {
        return String.format("User{chatId=%s,status=%s,lastLoverNumber=%s,lastProfileNumber=%s}",
                chatId,
                status,
                lastFavoriteNumber,
                lastProfileNumber);
    }
}
