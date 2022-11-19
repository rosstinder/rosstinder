package org.rosstinder.prerevolutionarytinderserver.service;

import org.rosstinder.prerevolutionarytinderserver.exception.BusinessException;
import org.rosstinder.prerevolutionarytinderserver.exception.ServiceException;
import org.rosstinder.prerevolutionarytinderserver.model.Gender;
import org.rosstinder.prerevolutionarytinderserver.model.Preference;
import org.rosstinder.prerevolutionarytinderserver.model.entity.Profile;
import org.rosstinder.prerevolutionarytinderserver.model.entity.User;
import org.rosstinder.prerevolutionarytinderserver.repository.ProfileRepository;
import org.rosstinder.prerevolutionarytinderserver.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final ImageGeneratorService imageGenerator;
    private final TranslatorClientImpl translatorClient;

    public UserServiceImpl(UserRepository userRepository, ProfileRepository profileRepository, ImageGeneratorService imageGenerator, TranslatorClientImpl translatorClient) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.imageGenerator = imageGenerator;
        this.translatorClient = translatorClient;
    }

    /**
     * Метод нахождения пользователя по его chatId
     *
     * @param chatId идентификатор
     * @return User пользователя
     * @throws BusinessException В случае отсутствия пользователя бросает BusinessException
     */
    @Override
    public User findUserByChatId(Long chatId) throws BusinessException {
        User user = userRepository.findUserByChatId(chatId);
        if (user == null) {
            logger.info("Пользователь chatId={} не был найден.", chatId);
            throw new BusinessException("Пользователь chatId=" + chatId + " не был найден.");
        }
        logger.debug("Пользователь chatId={} найден.", chatId);
        return user;
    }

    /**
     * Метод нахождения анкеты пользователя по его chatId
     *
     * @param chatId индентификатор пользователя
     * @return анкета пользователя
     * @throws BusinessException в случае если анкета не была найдена
     */
    @Override
    public Profile findProfileByChatId(Long chatId) throws BusinessException {
        Profile profile = profileRepository.findProfileByChatId(chatId);
        if (profile == null) {
            logger.info("Анкета chatId={} не была найдена.", chatId);
            throw new BusinessException("Анкета chatId=" + chatId + " не была найдена.");
        }
        logger.debug("Анкета chatId={} найдена.", chatId);
        return profile;
    }

    /**
     * Метод нахождения анкеты по ее идентификатору
     *
     * @param id идентификатор анкеты
     * @return анкета
     * @throws BusinessException в случае если анкета не была найдена
     */
    @Override
    public Profile findProfileById(Long id) throws BusinessException {
        Profile profile = profileRepository.findProfileById(id);
        if (profile == null) {
            logger.info("Анкета id={} не была найдена.", id);
            throw new BusinessException("Анкета chatId=" + id + " не была найдена.");
        }
        logger.debug("Анкета chatId={} найдена.", id);
        return profile;
    }

    /**
     * Сохранение пользователя и его изменений
     *
     * @param user данные пользователя
     */
    protected void saveUser(User user) {
        userRepository.save(user);
        logger.debug("Данные пользователя chatId=" + user.getChatId() + " сохранены.");
    }

    private void saveProfile(Profile profile) {
        profileRepository.save(profile);
        logger.debug("Данные анкеты пользователя chatId=" + profile.getChatId() + " сохранены.");
    }

    private void updateGender(Long chatId, String gender) throws BusinessException {
        Profile profile;
        try {
            profile = findProfileByChatId(chatId);
        } catch (BusinessException e) {
            logger.error(e.getMessage());
            throw new BusinessException(e.getMessage());
        }
        switch (gender) {
            case ("Сударъ") -> profile.setGender(Gender.MALE);
            case ("Сударыня") -> profile.setGender(Gender.FEMALE);
            default -> {
                logger.error("Неправильный формат пола. Допустимые значения: Сударъ, Сударыня.");
                throw new BusinessException("Неправильный формат пола. Допустимые значения: Сударъ, Сударыня.");
            }
        }
        saveProfile(profile);
        logger.info("Пол пользователя chatId=" + chatId + " был обновлен.");
    }

    private void updateName(Long chatId, String name) {
        try {
            Profile profile = findProfileByChatId(chatId);
            profile.setName(name);
            saveProfile(profile);
            logger.info("Имя пользователя chatId=" + chatId + " было обновлено.");
        } catch (BusinessException e) {
            logger.error(e.getMessage());
        }

    }

    private void updateDescription(Long chatId, String description) {
        try {
            Profile profile = findProfileByChatId(chatId);
            profile.setDescription(description);
            saveProfile(profile);
            logger.info("Описание пользователя chatId=" + chatId + " было обновлено.");
        } catch (BusinessException e) {
            logger.error(e.getMessage());
        }
    }

    private void updatePreference(Long chatId, String preference) throws BusinessException {
        Profile profile;
        try {
            profile = findProfileByChatId(chatId);
        } catch (BusinessException e) {
            logger.error(e.getMessage());
            throw new BusinessException(e.getMessage());
        }
        switch (preference) {
            case ("Сударъ") -> profile.setPreference(Preference.MALE);
            case ("Сударыня") -> profile.setPreference(Preference.FEMALE);
            case ("Всехъ") -> profile.setPreference(Preference.ALL);
            default -> {
                logger.error("Неправильный формат предпочтения. Допустимые значения: Сударъ, Сударыня, Всехъ.");
                throw new BusinessException("Неправильный формат предпочтения. Допустимые значения: Сударъ, Сударыня, Всехъ.");
            }
        }
        saveProfile(profile);
        logger.info("Предпочтения пользователя chatId=" + chatId + " были обновлены.");
    }

    /**
     * Обновление статуса состояния пользователя при использовании бота
     *
     * @param chatId идентификтаор
     * @param status новый статус состояния
     * @throws BusinessException если пользователь не был найден
     */
    @Override
    public void updateUserStatus(Long chatId, String status) throws BusinessException {
        try {
            User user = findUserByChatId(chatId);
            user.setStatus(status);
            saveUser(user);
            logger.debug("Новый статус пользователя chatId=" + chatId + " сохранен.");
        } catch (BusinessException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * Создание нового пользователя
     *
     * @param chatId идентификатор пользователя
     * @param status статус пользователя после создания
     * @throws BusinessException если пользователь с данным chatId уже существует
     */
    @Override
    public void createUser(Long chatId, String status) throws BusinessException {
        if (isUserDoesNotExist(chatId)) {
            saveUser(new User(chatId, status));
            logger.info("Новый пользователь chatId={} был добавлен.", chatId);
        } else {
            logger.info("Пользователь chatId={} не был добавлен.", chatId);
            throw new BusinessException("Пользователь с chatId={" + chatId + "} уже существует. " +
                    "ChatId должен быть уникальным.");
        }
    }

    /**
     * Создание анкеты пользователя
     *
     * @param chatId идентификатор пользователя
     * @throws BusinessException если анкета с полем chatId не уникальна
     */
    @Override
    public void createProfile(Long chatId) throws BusinessException {
        if (isProfileDoesNotExistByChatId(chatId)) {
            profileRepository.save(new Profile(chatId));
            logger.debug("Новая анкета пользователя chatId={} была добавлена.", chatId);
        } else {
            throw new BusinessException("Анкета с chatId={" + chatId + "} уже существует. " +
                    "ChatId должен быть уникальным.");
        }
    }

    /**
     * Метод проверяет, что пользователь не существует
     *
     * @param chatId идентификтаор пользователя
     * @return true - не существует; false - существует
     */
    public boolean isUserDoesNotExist(Long chatId) {
        User user = userRepository.findUserByChatId(chatId);
        return user == null;
    }

    /**
     * Метод проверяет, что анкета не существует
     *
     * @param id идентификатор анкеты
     * @return true - анкета не существует; false - анкета существует
     */
    public boolean isProfileDoesNotExistById(Long id) {
        Profile profile = profileRepository.findProfileById(id);
        return profile == null;
    }

    /**
     * Метод проверяет, что анкета не существует для пользователя с chatId
     *
     * @param chatId идентификатор пользователя
     * @return true - анкета существует; false - анкета не существует
     */
    private boolean isProfileDoesNotExistByChatId(Long chatId) {
        Profile profile = profileRepository.findProfileByChatId(chatId);
        if (profile == null) {
            logger.info("Анкета для пользователя chatId=" + chatId + " не найдена.");
            return true;
        } else {
            logger.info("Анкета для пользователя chatId=" + chatId + " найдена.");
            return false;
        }
    }

    private List<Profile> findAllProfiles() {
        return profileRepository.findAll();
    }

    /**
     * Метод возвращает сгенерированную картинку в виде byte[] на основе профиля
     *
     * @param id идентификатор анкеты
     * @return картинка анкеты в виде byte[]
     * @throws BusinessException если анкета не существует
     * @throws ServiceException  если не удалось сгенерировать картинку
     */
    @Override
    public String findProfileByteString(Long id) throws BusinessException, ServiceException {
        String result;
        try {
            Profile profile = findProfileById(id);
            result = imageGenerator.getGeneratedImage(profile);
        } catch (ServiceException e) {
            throw new ServiceException(e.getMessage());
        } catch (BusinessException e) {
            throw new BusinessException(e.getMessage());
        }
        logger.debug("Изображение с описанием анкеты id=" + id + " сгенерировано.");
        return result;
    }

    /**
     * Метод находит следующую анкету для отображения в разделе Поиск с учетом предпочтений пользователей
     *
     * @param chatId идентификатор пользователя, который желает просмотреть анкету
     * @return идентификатор анкеты (id)
     * @throws BusinessException если пользователь не найден или отсутствуют анкеты,
     *                           удовлетворяющие предпочтениям пользователя
     */
    @Override
    public Long findNextProfileByChatId(Long chatId) throws BusinessException {
        Long nextProfileId;
        try {
            User user = findUserByChatId(chatId);
            Profile profile = findProfileByChatId(chatId);
            nextProfileId = findAllProfiles().stream()
                    .filter(p -> !p.getChatId().equals(chatId))
                    .filter(p -> p.getGender() != null && p.getPreference() != null && p.getName() != null)
                    .filter(p -> Preference.compareGenderAndPreference(profile.getGender(), p.getPreference()) &&
                            Preference.compareGenderAndPreference(p.getGender(), profile.getPreference()))
                    .map(Profile::getId)
                    .sorted(Long::compareTo)
                    .filter(id -> id.compareTo(user.getLastProfileNumber()) > 0)
                    .findFirst()
                    .orElse(null);
            if (nextProfileId == null) {
                logger.info("Поиск анкеты, удовлетворяющей критериям пользователя chatId={}, начат с начала", chatId);
                user.setLastProfileNumber(User.ZERO_VALUE);
                nextProfileId = findAllProfiles().stream()
                        .filter(p -> !p.getChatId().equals(chatId))
                        .filter(p -> p.getGender() != null && p.getPreference() != null && p.getName() != null)
                        .filter(p -> Preference.compareGenderAndPreference(profile.getGender(), p.getPreference()) &&
                                Preference.compareGenderAndPreference(p.getGender(), profile.getPreference()))
                        .map(Profile::getId)
                        .sorted(Long::compareTo)
                        .filter(id -> id.compareTo(user.getLastProfileNumber()) > 0)
                        .findFirst()
                        .orElse(null);
                if (nextProfileId == null) {
                    logger.info("Отсутствуют анкеты, удовлетворяющие критериям пользователя chatId=" + chatId + ".");
                    throw new BusinessException("Отсутствуют анкеты, удовлетворяющие критериям пользователя chatId=" + chatId + ".");
                }
            }
            updateUserProfileNumber(chatId, nextProfileId);
        } catch (BusinessException e) {
            throw new BusinessException(e.getMessage());
        }
        return nextProfileId;
    }

    private void updateUserProfileNumber(Long chatId, Long profileNumber) {
        try {
            User user = findUserByChatId(chatId);
            user.setLastProfileNumber(profileNumber);
            saveUser(user);
            logger.info("Статус пользователя chatId=" + chatId + " сохранен.");
        } catch (BusinessException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Метод находит идентификатор анкеты на основе chatId пользователя
     *
     * @param chatId идентификатор пользователя
     * @return идентификатор анкеты
     * @throws BusinessException если анкета не была найдена
     */
    public Long findProfileIdByChatId(Long chatId) throws BusinessException {
        try {
            return findProfileByChatId(chatId).getId();
        } catch (BusinessException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * Метод формирует строку с описанием анкеты "[пол], [имя]" для заданной анкеты
     *
     * @param id идентификатор анкеты
     * @return строку "[пол], [имя]"
     */
    @Override
    public String findNameAndGender(Long id) throws BusinessException {
        try {
            Profile profile = findProfileById(id);
            return profile.getGender() + ", " + profile.getName();
        } catch (BusinessException e) {
            throw new BusinessException(e.getMessage());
        }

    }

    /**
     * Метод валидирует необходимые для обновления поля
     *
     * @param chatId идентификатор пользователя, чьи данные необходимо обновить
     * @param key    название поля, которое необходимо обновить
     * @param value  новое значение
     */
    @Override
    public void updateUserKeyValue(Long chatId, String key, String value) {
        switch (key) {
            case ("gender") -> {
                try {
                    updateGender(chatId, value);
                } catch (BusinessException e) {
                    logger.error(e.getMessage());
                    throw new BusinessException(e.getMessage());
                }
            }
            case ("name") -> updateName(chatId, translatorClient.translateDescription(value));
            case ("description") -> updateDescription(chatId, translatorClient.translateDescription(value));
            case ("preference") -> {
                try {
                    updatePreference(chatId, value);
                } catch (BusinessException e) {
                    logger.error(e.getMessage());
                    throw new BusinessException(e.getMessage());
                }
            }
            default -> logger.info("Анкета пользователя chatId=" + chatId + " не была обновлена " +
                    "по причине некорректного значения key в" + " полученном запросе. " +
                    "Допустимые значения: gender, name, description, preference.");
        }
    }
}
