package org.rosstinder.prerevolutionarytinderserver.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class Like {
    private final UUID id;

    private final UUID who;

    private final UUID whom;

    @Setter
    private boolean isLike;

    public Like(UUID who, UUID whom) {
        this.id = UUID.randomUUID();
        this.who = who;
        this.whom = whom;
    }
}
