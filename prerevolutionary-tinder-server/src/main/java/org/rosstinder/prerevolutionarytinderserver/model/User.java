package org.rosstinder.prerevolutionarytinderserver.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class User {
    private final UUID id;
    private final Long chatId;
    private String username;
    @Setter
    private Gender gender;
    @Setter
    private String name;
    @Setter
    private String title;
    @Setter
    private String description;
    @Setter
    private Preference preference;
    private List<User> likedUsers = new ArrayList<>();
    private boolean searchFlag = false;
    private boolean editFlag = false;
    private boolean likesFlag = false;

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

    public void addLikesUser(User user) {
        this.likedUsers.add(user);
    }

    public void editUser(User editedUser) {

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
