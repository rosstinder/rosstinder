package org.rosstinder.prerevolutionarytinderserver.model.repository;

import org.rosstinder.prerevolutionarytinderserver.model.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LikeRepository extends JpaRepository<Like, UUID> {
    //запрос, проверяющий, что данный юзер не лайкал ранее другого юзера
    @Query("select isLike from rosstinder.likes where who = :who and whom = :whom")
    boolean isUserLikedAnotherUser(@Param("who") UUID who, @Param("whom") UUID whom);

    //запрос, проверяющий, что лайк данного юзера оказался взаимным
    @Query("select isLike from rosstinder.likes where who = :whom and whom = :who")
    boolean isUserMatchesWithAnotherUser(@Param("who") UUID who, @Param("whom") UUID whom);

    //запрос, возвращающий уиды юзеров, к-ых лайкнул конкретный юзер
    @Query("select * from rosstinder.likes where id = :who")
    List<Like> getAllUserLikes(@Param("id") UUID who);

    @Query("select * from rosstinder.likes")
    List<Like> getAllLikes();

}
