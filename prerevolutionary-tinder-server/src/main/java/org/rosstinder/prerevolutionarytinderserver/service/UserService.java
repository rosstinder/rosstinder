package org.rosstinder.prerevolutionarytinderserver.service;

import org.rosstinder.prerevolutionarytinderserver.exception.BusinessException;
import org.rosstinder.prerevolutionarytinderserver.model.Gender;
import org.rosstinder.prerevolutionarytinderserver.model.Preference;
import org.rosstinder.prerevolutionarytinderserver.model.entity.Profile;
import org.rosstinder.prerevolutionarytinderserver.model.entity.User;
import org.rosstinder.prerevolutionarytinderserver.model.repository.ProfileRepository;
import org.rosstinder.prerevolutionarytinderserver.model.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository userRepository, ProfileRepository profileRepository) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }

    private List<User> findAll() {
        return userRepository.findAll();
    }

    public User findUserByChatId(Long chatId) throws BusinessException {
        Optional<User> optUser = findAll().stream()
                .filter(u -> u.getChatId().equals(chatId))
                .findAny();
        if (optUser.isEmpty()) {
            logger.info("Пользователь chatId={} не был найден.", chatId);
            throw new BusinessException("Пользователь chatId="+chatId+" не был найден.");
        }
        logger.debug("Пользователь chatId={} найден.", chatId);
        return optUser.get();
    }

    public Profile findProfileByChatId(Long chatId) throws BusinessException {
        Optional<Profile> optProfile = findAllProfiles().stream()
                .filter(p -> p.getChatId().equals(chatId))
                .findAny();
        if (optProfile.isEmpty()) {
            logger.info("Анкета chatId={} не была найдена.", chatId);
            throw new BusinessException("Анкета chatId="+chatId+" не была найдена.");
        }
        logger.debug("Анкета chatId={} найдена.", chatId);
        return optProfile.get();
    }

    public void updateGender(Long chatId, String gender) throws BusinessException {
        try {
            Profile profile = findProfileByChatId(chatId);
            profile.setGender(new Gender(gender));
        } catch (BusinessException e) {
            logger.error(e.getMessage());
            throw new BusinessException(e.getMessage());
        }

    }

    public void updateName(Long chatId, String name) {
        try {
            Profile profile = findProfileByChatId(chatId);
            profile.setName(name);
        } catch (BusinessException e) {
            logger.error(e.getMessage());
        }

    }

    public void updateDescription(Long chatId, String description) {
        try {
            Profile profile = findProfileByChatId(chatId);
            profile.setDescription(description);
        } catch (BusinessException e) {
            logger.error(e.getMessage());
        }
    }

    public void updatePreference(Long chatId, String preference) throws BusinessException {
        try {
            Profile profile = findProfileByChatId(chatId);
            profile.setPreference(new Preference(preference));
        } catch (BusinessException e) {
            logger.error(e.getMessage());
            throw new BusinessException(e.getMessage());
        }

    }

    public void updateUserStatus(Long chatId, String status) throws BusinessException {
        try {
            User user = findUserByChatId(chatId);
            user.setStatus(status);
        } catch (BusinessException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    public void createUser(Long chatId, String status) throws BusinessException {
        if (isUserDoesNotExist(chatId)) {
            saveUser(new User(chatId, status));
            logger.debug("Новый пользователь chatId={} был добавлен.", chatId);
        }
        else {
            throw new BusinessException("Пользователь с chatId={" + chatId + "} уже существует. ChatId должен быть уникальным.");
        }
    }

    public void createProfile(Long chatId) throws BusinessException {
        if (isProfileDoesNotExist(chatId)) {
            profileRepository.save(new Profile(chatId));
            logger.debug("Новая анкета пользователя chatId={} была добавлена.", chatId);
        } else {
            throw new BusinessException("Анкета с chatId={" + chatId + "} уже существует. ChatId должен быть уникальным.");
        }
    }

    public boolean isUserDoesNotExist(Long chatId) {
        Optional<User> optUser = findAll().stream()
                .filter(u -> u.getChatId().equals(chatId))
                .findAny();
        return optUser.isEmpty();
    }

    private boolean isProfileDoesNotExist(Long chatId) {
        Optional<Profile> optProfile = findAllProfiles().stream()
                .filter(p -> p.getChatId().equals(chatId))
                .findAny();
        return optProfile.isEmpty();
    }

    public List<Profile> findAllProfiles() {
        return profileRepository.findAll();
    }

    public Profile findProfileUrl(Long chatId) throws BusinessException {
        Optional<Profile> optProfile = findAllProfiles().stream()
                .filter(p -> p.getChatId().equals(chatId))
                .findAny();
        if (optProfile.isEmpty()) {
            logger.info("Анкета chatId={} не была найдена.", chatId);
            throw new BusinessException("Анкета chatId="+chatId+" не была найдена.");
        }
        logger.debug("Анкета chatId={} найдена.", chatId);
        return optProfile.get();
    }

    public Long findNextProfileChatId(Long chatId) throws BusinessException {
        Long result;
        try {
            User user = findUserByChatId(chatId);
            Profile profile = findProfileByChatId(chatId);
            Optional<Long> nextProfile = findAllProfiles().stream()
                    .filter(p -> !p.getChatId().equals(chatId))
                    .filter(p -> Preference.compareGenderAndPreference(profile.getGender(), p.getPreference())
                            && Preference.compareGenderAndPreference(p.getGender(), profile.getPreference()))
                    .map(Profile::getChatId)
                    .sorted(Long::compareTo)
                    .filter(id -> id.compareTo(user.getLastProfileNumber()) > 0)
                    .findFirst();
            if(nextProfile.isEmpty()) {
                logger.info("Поиск анкеты, удовлетворяющей критериям пользователя chatId={}, начат с начала", chatId);
                user.setLastProfileNumber(User.ZERO_VALUE);
                nextProfile = findAllProfiles().stream()
                        .filter(p -> !p.getChatId().equals(chatId))
                        .filter(p -> Preference.compareGenderAndPreference(profile.getGender(), p.getPreference())
                                && Preference.compareGenderAndPreference(p.getGender(), profile.getPreference()))
                        .map(Profile::getChatId)
                        .sorted(Long::compareTo)
                        .filter(id -> id.compareTo(user.getLastProfileNumber()) > 0)
                        .findFirst();
                if(nextProfile.isEmpty()) {
                    logger.info("Отсутствуют анкеты, удовлетворяющие критериям пользователя chatId="+chatId+".");
                    throw new BusinessException("Отсутствуют анкеты, удовлетворяющие критериям пользователя chatId="+chatId+".");
                }
            }
            updateUserProfileNumber(chatId, nextProfile.get());
            result = nextProfile.get();
        } catch (BusinessException e) {
            throw new BusinessException(e.getMessage());
        }
        return result;
    }

    public void updateUserProfileNumber(Long chatId, Long profileNumber) {
        try {
            User user = findUserByChatId(chatId);
            user.setLastProfileNumber(profileNumber);
            saveUser(user);
        } catch (BusinessException e) {
            logger.error(e.getMessage());
        }
    }

    private void saveUser(User user) {
        userRepository.save(user);
        logger.debug("Данные пользователя chatId="+user.getChatId()+" сохранены.");
    }

    public boolean incorrectKey(Long chatId) {
        logger.info("Значения для пользователя chatId="+chatId+" были обновлены по причине некорректного значения key в" +
                " полученном запросе. Допустимые значения: gender, name, description, preference.");
        return true;
    }
}
