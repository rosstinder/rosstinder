package org.rosstinder.prerevolutionarytinderserver.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "favorites", schema = "rosstinder")
public class Favorite {
    public final static boolean LIKE = true;
    public final static boolean DISLIKE = false;

    @Column(name = "id", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "who", nullable = false)
    private Long whoId;

    @Column(name = "whom", nullable = false)
    private Long whomId;

    @Setter
    @Column(name = "is_like")
    private boolean isLike;

    public Favorite(Long who, Long whom, boolean isLike) {
        this.whoId = who;
        this.whomId = whom;
        this.isLike = isLike;
    }
}
