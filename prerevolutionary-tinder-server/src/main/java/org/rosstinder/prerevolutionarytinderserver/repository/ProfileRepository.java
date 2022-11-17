package org.rosstinder.prerevolutionarytinderserver.repository;

import org.rosstinder.prerevolutionarytinderserver.model.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    @Query(value = "select * from rosstinder.profiles p where p.id = ?", nativeQuery = true)
    Profile findProfileById(Long id);

    @Query(value = "select * from rosstinder.profiles p where p.chat_id = ?", nativeQuery = true)
    Profile findProfileByChatId(Long id);
}
