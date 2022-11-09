package org.rosstinder.prerevolutionarytinderserver.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.rosstinder.prerevolutionarytinderserver.model.Gender;
import org.rosstinder.prerevolutionarytinderserver.model.Preference;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "profiles", schema = "rosstinder")
@Getter
public class Profile {
    @Column(name = "chat_id", unique = true, nullable = false)
    private final Long chatId;
    @Column(name = "name")
    @Setter
    private String name;
    @Column(name = "gender")
    @Setter
    private Gender gender;
    @Column(name = "description")
    @Setter
    private String description;
    @Column(name = "preference")
    @Setter
    private Preference preference;

    public Profile(Long chatId) {
        this.chatId = chatId;
    }

    @Override
    public String toString() {
        return String.format("User{chatId=%s,name=%s,gender=%s,desc=%s,preference=%s}",
                chatId,
                getPropertyAsString(name),
                getPropertyAsString(gender),
                getPropertyAsString(description),
                getPropertyAsString(preference));
    }

    private String getPropertyAsString(Object obj) {
        if (obj == null) {
            return "null";
        }
        else if (obj instanceof Gender) {
            return ((Gender) obj).getGender();
        } else if (obj instanceof Preference) {
            return ((Preference) obj).getPreference();
        } else {
            return obj.toString();
        }
    }
}
