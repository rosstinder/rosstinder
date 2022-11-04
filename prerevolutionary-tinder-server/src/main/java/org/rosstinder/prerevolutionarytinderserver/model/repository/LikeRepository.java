package org.rosstinder.prerevolutionarytinderserver.model.repository;

import org.rosstinder.prerevolutionarytinderserver.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LikeRepository extends JpaRepository<Like, UUID> {
    //запрос, проверяющий, что данный юзер не лайкал ранее другого юзера
    boolean isUserLikedAnotherUser(UUID who, UUID whom);

    //запрос, проверяющий, что лайк данного юзера оказался взаимным
    boolean isUserMatchesWithAnotherUser(UUID who, UUID whom);

    //запрос, возвращающий уиды юзеров, к-ых лайкнул конкретный юзер
    List<Like> getAllUserLikes(UUID who);

    List<Like> getAllLikes();

}
