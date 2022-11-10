package org.rosstinder.prerevolutionarytinderserver.repository;

import org.rosstinder.prerevolutionarytinderserver.model.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

}
