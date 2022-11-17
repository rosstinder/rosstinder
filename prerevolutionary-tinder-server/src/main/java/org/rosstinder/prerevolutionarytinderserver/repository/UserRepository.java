package org.rosstinder.prerevolutionarytinderserver.repository;

import org.rosstinder.prerevolutionarytinderserver.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "select * from rosstinder.users u where u.chat_id = ?", nativeQuery = true)
    User findUserByChatId(Long chatId);
}
