package org.rosstinder.prerevolutionarytinderserver.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.rosstinder.prerevolutionarytinderserver.model.Gender;
import org.rosstinder.prerevolutionarytinderserver.model.Preference;

@Getter
public class Profile {
    private final Long chatId;
    @Setter
    private String name;
    @Setter
    private Gender gender;
    @Setter
    private String title;
    @Setter
    private String description;
    @Setter
    private Preference preference;

    public Profile(Long chatId) {
        this.chatId = chatId;
    }

    @Override
    public String toString() {
        return String.format("User{chatId=%s,name=%s,gender=%s,title=%s,desc=%s,preference=%s}",
                chatId,
                getPropertyAsString(name),
                getPropertyAsString(gender),
                getPropertyAsString(title),
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
