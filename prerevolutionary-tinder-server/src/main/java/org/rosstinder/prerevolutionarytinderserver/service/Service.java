package org.rosstinder.prerevolutionarytinderserver.service;

import org.rosstinder.prerevolutionarytinderserver.model.entity.User;
import java.util.List;

public interface Service {
    User findById(Long chatId);

    int create(User user);

    void update(User user);

    void deleteById(Long chatId);

    List<User> findAll();
}
