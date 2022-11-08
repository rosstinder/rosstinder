package org.rosstinder.prerevolutionarytinderserver.model.repository;

import org.rosstinder.prerevolutionarytinderserver.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
