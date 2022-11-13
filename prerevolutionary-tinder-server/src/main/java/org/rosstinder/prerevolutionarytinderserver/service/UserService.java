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

    /**
     * Метод нахождения всех пользователей
     * @return список пользователей
     */
    private List<User> findAllUsers() {
        return userRepository.findAllUsers();
    }

    /**
     * Метод нахождения пользователя по его chatId
     * @param chatId идентификатор
     * @return User пользователя
     * @throws BusinessException В случае отсутствия пользователя бросает BusinessException
     */
    public User findUserByChatId(Long chatId) throws BusinessException {
        Optional<User> optUser = Optional.ofNullable(userRepository.findUserByChatId(chatId));
        if (optUser.isEmpty()) {
            logger.info("Пользователь chatId={} не был найден.", chatId);
            throw new BusinessException("Пользователь chatId="+chatId+" не был найден.");
        }
        logger.debug("Пользователь chatId={} найден.", chatId);
        return optUser.get();
    }

    /**
     * Метод нахождения анкеты пользователя по его chatId
     * @param chatId индентификатор пользователя
     * @return анкета пользователя
     * @throws BusinessException в случае если анкета не была найдена
     */
    public Profile findProfileByChatId(Long chatId) throws BusinessException {
        Optional<Profile> optProfile = Optional.ofNullable(profileRepository.findProfileByChatId(chatId));
        if (optProfile.isEmpty()) {
            logger.info("Анкета chatId={} не была найдена.", chatId);
            throw new BusinessException("Анкета chatId="+chatId+" не была найдена.");
        }
        logger.debug("Анкета chatId={} найдена.", chatId);
        return optProfile.get();
    }

    /**
     * Метод нахождения анкеты по ее идентификатору
     * @param id идентификатор анкеты
     * @return анкета
     * @throws BusinessException в случае если анкета не была найдена
     */
    public Profile findProfileById(Long id) throws BusinessException {
        Optional<Profile> optProfile = Optional.ofNullable(profileRepository.findProfileById(id));
        if (optProfile.isEmpty()) {
            logger.info("Анкета id={} не была найдена.", id);
            throw new BusinessException("Анкета chatId="+id+" не была найдена.");
        }
        logger.debug("Анкета chatId={} найдена.", id);
        return optProfile.get();
    }

    /**
     * Сохранение пользователя и его изменений
     * @param user данные пользователя
     */
    protected void saveUser(User user) {
        userRepository.save(user);
        logger.debug("Данные пользователя chatId="+user.getChatId()+" сохранены.");
    }

    /**
     * Сохранение анкеты и ее изменений
     * @param profile данные анкеты
     */
    private void saveProfile(Profile profile) {
        profileRepository.save(profile);
        logger.debug("Данные анкеты пользователя chatId="+profile.getChatId()+" сохранены.");
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
            logger.info("Пол пользователя chatId="+chatId+" был обновлен.");
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
            logger.info("Имя пользователя chatId="+chatId+" было обновлено.");
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
            logger.info("Описание пользователя chatId="+chatId+" было обновлено.");
        } catch (BusinessException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Обновление предпочтений пользователя
     * @param chatId идентификатор пользователя
     * @param preference новое значение предпочтения
     * @throws BusinessException если новое значение предпочтения не удовлетворяет возможным значениям поля
     */
    public void updatePreference(Long chatId, String preference) throws BusinessException {
        try {
            Profile profile = findProfileByChatId(chatId);
            profile.setPreference(new Preference(preference));
            saveProfile(profile);
            logger.info("Предпочтения пользователя chatId="+chatId+" были обновлены.");
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
            logger.debug("Новый статус пользователя chatId="+chatId+" сохранен.");
        } catch (BusinessException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * Создание нового пользователя
     * @param chatId идентификатор пользователя
     * @param status статус пользователя после создания
     * @throws BusinessException если пользователь с данным chatId уже существует
     */
    public void createUser(Long chatId, String status) throws BusinessException {
        if (isUserDoesNotExist(chatId)) {
            saveUser(new User(chatId, status));
            logger.info("Новый пользователь chatId={} был добавлен.", chatId);
        }
        else {
            logger.info("Пользователь chatId={} не был добавлен.", chatId);
            throw new BusinessException("Пользователь с chatId={" + chatId + "} уже существует. ChatId должен быть уникальным.");
        }
    }

    /**
     * Создание анкеты пользователя
     * @param chatId идентификатор пользователя
     * @throws BusinessException если анкета с полем chatId не уникальна
     */
    public void createProfile(Long chatId) throws BusinessException {
        if (isProfileDoesNotExistByChatId(chatId)) {
            profileRepository.save(new Profile(chatId));
            logger.debug("Новая анкета пользователя chatId={} была добавлена.", chatId);
        } else {
            throw new BusinessException("Анкета с chatId={" + chatId + "} уже существует. ChatId должен быть уникальным.");
        }
    }

    /**
     * Метод проверяет, что пользователь не существует
     * @param chatId идентификтаор пользователя
     * @return true - не существует; false - существует
     */
    public boolean isUserDoesNotExist(Long chatId) {
        Optional<User> optUser = Optional.ofNullable(userRepository.findUserByChatId(chatId));
        if (optUser.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Метод проверяет, что анкета не существует
     * @param id идентификатор анкеты
     * @return true - анкета не существует; false - анкета существует
     */
    public boolean isProfileDoesNotExistById(Long id) {
        Optional<Profile> optProfile = Optional.ofNullable(profileRepository.findProfileById(id));
        if (optProfile.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Метод проверяет, что анкета не существует для пользователя с chatId
     * @param chatId идентификатор пользователя
     * @return true - анкета существует; false - анкета не существует
     */
    private boolean isProfileDoesNotExistByChatId(Long chatId) {
        Optional<Profile> optProfile = Optional.ofNullable(profileRepository.findProfileByChatId(chatId));
        if (optProfile.isEmpty()) {
            logger.info("Анкета для пользователя chatId="+chatId+" не найдена.");
            return true;
        } else {
            logger.info("Анкета для пользователя chatId="+chatId+" найдена.");
            return false;
        }
    }

    /**
     * Получение всех анкет
     * @return список анкет
     */
    public List<Profile> findAllProfiles() {
        return profileRepository.findAll();
    }

    /**
     * Метод возвращает сгенерированную картинку в виде byte[] на основе профиля
     * @param id идентификатор анкеты
     * @return картинка анкеты в виде byte[]
     * @throws BusinessException если анкета не существует
     * @throws ServiceException если не удалось сгенерировать картинку
     */
    public String findProfileUrl(Long id) throws BusinessException, ServiceException {
        String result;
        Optional<Profile> optProfile = findAllProfiles().stream()
                .filter(p -> p.getId().equals(id))
                .findAny();
        if (optProfile.isEmpty()) {
            logger.info("Анкета id={} не была найдена.", id);
            throw new BusinessException("Анкета id="+id+" не была найдена.");
        }
        logger.debug("Анкета id={} найдена.", id);
        try {
            result = imageGenerator.getGeneratedImage(optProfile.get());
        } catch (ServiceException e) {
            throw new ServiceException(e.getMessage());
        }
        logger.debug("Изображение с описанием анкеты id="+id+" сгенерировано.");
        return result;
    }

    /**
     * Метод находит следующую анкету для отображения в разделе Поиск с учетом предпочтений пользователей
     * @param chatId идентификатор пользователя, который желает просмотреть анкету
     * @return идентификатор анкеты (id)
     * @throws BusinessException если пользователь не найден или отсутствуют анкеты, удовлетворяющие предпочтениям пользователя
     */
    public Long findNextProfileByChatId(Long chatId) throws BusinessException {
        Long result;
        try {
            User user = findUserByChatId(chatId);
            Profile profile = findProfileByChatId(chatId);
            Optional<Long> nextProfile = findAllProfiles().stream()
                    .filter(p -> !p.getChatId().equals(chatId))
                    .filter(p -> p.getGender() != null && p.getPreference() != null && p.getName() != null)
                    .filter(p -> Preference.compareGenderAndPreference(profile.getGender(), p.getPreference())
                            && Preference.compareGenderAndPreference(p.getGender(), profile.getPreference()))
                    .map(Profile::getId)
                    .sorted(Long::compareTo)
                    .filter(id -> id.compareTo(user.getLastProfileNumber()) > 0)
                    .findFirst();
            if(nextProfile.isEmpty()) {
                logger.info("Поиск анкеты, удовлетворяющей критериям пользователя chatId={}, начат с начала", chatId);
                user.setLastProfileNumber(User.ZERO_VALUE);
                nextProfile = findAllProfiles().stream()
                        .filter(p -> !p.getChatId().equals(chatId))
                        .filter(p -> p.getGender() != null && p.getPreference() != null && p.getName() != null)
                        .filter(p -> Preference.compareGenderAndPreference(profile.getGender(), p.getPreference())
                                && Preference.compareGenderAndPreference(p.getGender(), profile.getPreference()))
                        .map(Profile::getId)
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

    /**
     * Обновление номера текущей просматриваемой анкеты
     * @param chatId идентификатор пользователя
     * @param profileNumber идентификатор просматриваемой анкеты
     */
    public void updateUserProfileNumber(Long chatId, Long profileNumber) {
        try {
            User user = findUserByChatId(chatId);
            user.setLastProfileNumber(profileNumber);
            saveUser(user);
            logger.info("Статус пользователя chatId="+chatId+" сохранен.");
        } catch (BusinessException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Логгирование факта не обновления данных анкеты.
     * @param chatId идентификатор пользователя
     * @return true
     */
    public boolean incorrectKey(Long chatId) {
        logger.info("Анкета пользователя chatId="+chatId+" не была обновлена по причине некорректного значения key в" +
                " полученном запросе. Допустимые значения: gender, name, description, preference.");
        return true;
    }

    /**
     * Метод находит идентификатор анкеты на основе chatId пользователя
     * @param chatId идентификатор пользователя
     * @return идентификатор анкеты
     * @throws BusinessException если анкета не была найдена
     */
    public Long findProfileIdByChatId(Long chatId) throws BusinessException {
        try {
            Long result = findProfileByChatId(chatId).getId();
            return result;
        } catch (BusinessException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * Метод формирует строку с описанием анкеты "[пол], [имя]" для заданной анкеты
     * @param id идентификатор анкеты
     * @return строку "[пол], [имя]"
     */
    public String findNameAndGender(Long id) throws BusinessException {
        try {
            Profile profile = findProfileById(id);
            return profile.getGender() + ", " + profile.getName();
        } catch (BusinessException e) {
            throw new BusinessException(e.getMessage());
        }

    }
}
