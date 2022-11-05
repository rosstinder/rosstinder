package org.rosstinder.prerevolutionarytinderserver.model.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.rosstinder.prerevolutionarytinderserver.model.Gender;
import org.rosstinder.prerevolutionarytinderserver.model.Preference;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private final UUID id;
    @Column(name = "chat_id", unique = true, nullable = false)
    private final Long chatId;
    @Column(name = "username", unique = true, nullable = false)
    private String username;
    @Column(name = "gender")
    @Setter
    private Gender gender;
    @Column(name = "name")
    @Setter
    private String name;
    @Column(name = "title")
    @Setter
    private String title;
    @Column(name = "description")
    @Setter
    private String description;
    @Column(name = "preference")
    @Setter
    private Preference preference;

    public User(Long chatId, String username) {
        this.id = UUID.randomUUID();
        this.chatId = chatId;
        this.username = username;
    }
    public User(Long chatId, String username, Gender gender, String name, String title,
                String description, Preference preference) {
        this.id = UUID.randomUUID();
        this.chatId = chatId;
        this.username = username;
        this.gender = gender;
        this.name = name;
        this.title = title;
        this.description = description;
        this.preference = preference;
    }

    @Override
    public String toString() {
        return String.format("User{id=%s,chatId=%s,gender=%s,name=%s,title=%s,description=%s,preference1=%s,prederence2=%s}",
                id,
                chatId,
                gender,
                name,
                title,
                description,
                preference);
    }
}
