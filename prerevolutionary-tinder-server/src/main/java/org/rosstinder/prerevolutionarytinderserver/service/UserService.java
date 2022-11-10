package org.rosstinder.prerevolutionarytinderserver.service;

import org.rosstinder.prerevolutionarytinderserver.exception.BusinessException;
import org.rosstinder.prerevolutionarytinderserver.exception.ServiceException;
import org.rosstinder.prerevolutionarytinderserver.model.Gender;
import org.rosstinder.prerevolutionarytinderserver.model.Preference;
import org.rosstinder.prerevolutionarytinderserver.model.entity.Profile;
import org.rosstinder.prerevolutionarytinderserver.model.entity.User;
import org.rosstinder.prerevolutionarytinderserver.model.repository.ProfileRepository;
import org.rosstinder.prerevolutionarytinderserver.model.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final ImageGenerator imageGenerator = new ImageGenerator();

    public UserService(UserRepository userRepository, ProfileRepository profileRepository) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }

    private List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Метод нахождения пользователя по его chatId.
     * @param chatId идентификатор
     * @return User пользователя
     * @throws BusinessException В случае отсутствия пользователя бросает BusinessException
     */
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

    /**
     * Метод нахождения анкеты пользователя по его chatId.
     * @param chatId индентификатор
     * @return Profile анкета пользователя
     * @throws BusinessException в случае если анкета не была найдена
     */
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

    /**
     * Обновление пола пользователя
     * @param chatId идентификатор
     * @param gender строка с наименованием пола
     * @throws BusinessException если анкета пользователя не была найдена или поле gender неверного формата
     */
    public void updateGender(Long chatId, String gender) throws BusinessException {
        try {
            Profile profile = findProfileByChatId(chatId);
            profile.setGender(new Gender(gender));
            saveProfile(profile);
        } catch (BusinessException e) {
            logger.error(e.getMessage());
            throw new BusinessException(e.getMessage());
        }

    }

    /**
     * Обновление имени пользователя
     * @param chatId идентификтаор
     * @param name имя
     */
    public void updateName(Long chatId, String name) {
        try {
            Profile profile = findProfileByChatId(chatId);
            profile.setName(name);
            saveProfile(profile);
        } catch (BusinessException e) {
            logger.error(e.getMessage());
        }

    }

    /**
     * Обновление описания пользователя
     * @param chatId идентификатор
     * @param description описание
     */
    public void updateDescription(Long chatId, String description) {
        try {
            Profile profile = findProfileByChatId(chatId);
            profile.setDescription(description);
            saveProfile(profile);
        } catch (BusinessException e) {
            logger.error(e.getMessage());
        }
    }

    public void updatePreference(Long chatId, String preference) throws BusinessException {
        try {
            Profile profile = findProfileByChatId(chatId);
            profile.setPreference(new Preference(preference));
            saveProfile(profile);
        } catch (BusinessException e) {
            logger.error(e.getMessage());
            throw new BusinessException(e.getMessage());
        }

    }

    /**
     * Обновление статуса состояния пользователя при использовании бота
     * @param chatId идентификтаор
     * @param status новый статус состояния
     * @throws BusinessException если пользователь не был найден
     */
    public void updateUserStatus(Long chatId, String status) throws BusinessException {
        try {
            User user = findUserByChatId(chatId);
            user.setStatus(status);
            saveUser(user);
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

    public byte[] findProfileUrl(Long chatId) throws BusinessException, ServiceException {
        byte[] result;
        Optional<Profile> optProfile = findAllProfiles().stream()
                .filter(p -> p.getChatId().equals(chatId))
                .findAny();
        if (optProfile.isEmpty()) {
            logger.info("Анкета chatId={} не была найдена.", chatId);
            throw new BusinessException("Анкета chatId="+chatId+" не была найдена.");
        }
        logger.debug("Анкета chatId={} найдена.", chatId);
        try {
            result = imageGenerator.getGeneratedImage(optProfile.get());
        } catch (ServiceException e) {
            throw new ServiceException(e.getMessage());
        }
        return result;
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

    protected void saveUser(User user) {
        userRepository.save(user);
        logger.debug("Данные пользователя chatId="+user.getChatId()+" сохранены.");
    }

    private void saveProfile(Profile profile) {
        profileRepository.save(profile);
        logger.debug("Данные анкеты пользователя chatId="+profile.getChatId()+" сохранены.");
    }

    public boolean incorrectKey(Long chatId) {
        logger.info("Значения для пользователя chatId="+chatId+" были обновлены по причине некорректного значения key в" +
                " полученном запросе. Допустимые значения: gender, name, description, preference.");
        return true;
    }
}
