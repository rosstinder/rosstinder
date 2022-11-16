package org.rosstinder.prerevolutionarytinderserver.service;

import org.rosstinder.prerevolutionarytinderserver.model.entity.Profile;
import org.rosstinder.prerevolutionarytinderserver.model.entity.User;

public interface UserService {
    User findUserByChatId(Long chatId);
    Profile findProfileByChatId(Long chatId);
    Profile findProfileById(Long id);
    void updateUserStatus(Long chatId, String status);
    void createUser(Long chatId, String status);
    void createProfile(Long chatId);
    String findProfileByteString(Long id);
    Long findNextProfileByChatId(Long chatId);
    String findNameAndGender(Long id);
    void updateUserKeyValue(Long chatId, String key, String value);

}
