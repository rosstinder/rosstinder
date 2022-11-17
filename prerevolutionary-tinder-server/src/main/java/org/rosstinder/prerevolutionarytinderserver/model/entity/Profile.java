package org.rosstinder.prerevolutionarytinderserver.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.rosstinder.prerevolutionarytinderserver.model.Gender;
import org.rosstinder.prerevolutionarytinderserver.model.Preference;

import javax.persistence.*;

@Entity
@Table(name = "profiles", schema = "rosstinder")
@Getter
@NoArgsConstructor
public class Profile {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(generator = "rosstinder.profiles_sequence")
    private Long id;

    @Column(name = "chat_id", unique = true, nullable = false)
    private Long chatId;
    @Column(name = "name")
    @Setter
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;
    @Column(name = "description")
    @Setter
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(name = "preference")
    private Preference preference;

    public Profile(Long chatId) {
        this.chatId = chatId;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setPreference(Preference preference) {
        this.preference = preference;
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
        } else {
            return obj.toString();
        }
    }
}
