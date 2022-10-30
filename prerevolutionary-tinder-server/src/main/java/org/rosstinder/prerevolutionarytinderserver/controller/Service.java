package org.rosstinder.prerevolutionarytinderserver.controller;

import org.rosstinder.prerevolutionarytinderserver.model.User;
import java.util.List;

public interface Service {
    User findById(int chatId);

    int create(User user);

    void update(User user);

    void deleteById(int chatId);

    List<User> findAll();
}
