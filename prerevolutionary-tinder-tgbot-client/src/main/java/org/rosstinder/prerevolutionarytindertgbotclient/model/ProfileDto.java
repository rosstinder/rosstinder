package org.rosstinder.prerevolutionarytindertgbotclient.model;

import lombok.Data;

@Data
public class ProfileDto {
    private Long id;
    private Long chatId;
    private String name;
    private String gender;
    private String description;
    private String preference;
}
