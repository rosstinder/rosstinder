package org.rosstinder.prerevolutionarytinderserver.model.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
//@Entity
//@Table(name = "users")
public class User {
    public final static Long ZERO_VALUE = Long.valueOf(0);

    //private final Long id;
    private final Long chatId;
    @Setter
    private String status;
    @Setter
    private Long lastFavoriteNumber;
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
