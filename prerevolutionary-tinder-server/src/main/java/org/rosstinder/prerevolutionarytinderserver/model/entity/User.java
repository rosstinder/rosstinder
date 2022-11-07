package org.rosstinder.prerevolutionarytinderserver.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.rosstinder.prerevolutionarytinderserver.model.Gender;
import org.rosstinder.prerevolutionarytinderserver.model.Preference;

import javax.persistence.*;
import java.util.UUID;

@Getter
//@Entity
//@Table(name = "users")
public class User {
    public final static Long ZERO = Long.valueOf(0);

    //private final Long id;
    private final Long chatId;
    @Setter
    private String status;
    @Setter
    private Long lastLoverNumber;
    @Setter
    private Long lastProfileNumber;

    public User(Long chatId, String status) {
        //this.id = id;
        this.chatId = chatId;
        this.status = status;
        this.lastLoverNumber = ZERO;
        this.lastProfileNumber = ZERO;
    }

    @Override
    public String toString() {
        return String.format("User{chatId=%s,status=%s,lastLoverNumber=%s,lastProfileNumber=%s}",
                chatId,
                status,
                lastLoverNumber,
                lastProfileNumber);
    }
}
