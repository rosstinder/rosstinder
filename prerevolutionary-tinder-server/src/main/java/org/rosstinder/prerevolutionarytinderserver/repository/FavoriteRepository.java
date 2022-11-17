package org.rosstinder.prerevolutionarytinderserver.repository;

import org.rosstinder.prerevolutionarytinderserver.model.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    @Query(value = "select * from rosstinder.favorites f where f.who = ? and f.whom = ?", nativeQuery = true)
    Favorite findFavoriteByWhoAndWhom(Long who, Long whom);

    @Query(value = "select * from rosstinder.favorites f where f.who = ? and f.is_like = true", nativeQuery = true)
    List<Favorite> findFavoritesLikeByWho(Long who);

}
