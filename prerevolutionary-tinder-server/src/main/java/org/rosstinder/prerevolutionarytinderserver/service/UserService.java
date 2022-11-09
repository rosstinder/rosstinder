package org.rosstinder.prerevolutionarytinderserver.service;

import org.rosstinder.prerevolutionarytinderserver.exception.BusinessException;
import org.rosstinder.prerevolutionarytinderserver.exception.ServiceException;
import org.rosstinder.prerevolutionarytinderserver.model.Gender;
import org.rosstinder.prerevolutionarytinderserver.model.Preference;
import org.rosstinder.prerevolutionarytinderserver.model.entity.Profile;
import org.rosstinder.prerevolutionarytinderserver.model.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class UserService {

    private List<User> users;
    private List<Profile> profiles;

    public final ImageGenerator imageGenerator = new ImageGenerator();

    public UserService() {
        this.users = new ArrayList<>();
        this.users.add(new User(Long.valueOf(1), "start"));
        this.users.add(new User(Long.valueOf(2), "start"));
        this.users.add(new User(Long.valueOf(3), "start"));

        this.profiles = new ArrayList<>();
        this.profiles.add(new Profile(Long.valueOf(1)));
        this.profiles.add(new Profile(Long.valueOf(2)));
        this.profiles.add(new Profile(Long.valueOf(3)));

        try {
            this.profiles.get(0).setGender(new Gender("Сударъ"));
        } catch (BusinessException e) {
            throw new RuntimeException(e);
        }
        this.profiles.get(0).setName("Некто");
        this.profiles.get(0).setTitle("Интеллигентный");
        this.profiles.get(0).setDescription("Одинокий купец, 37 лет, имеет около двадцати лет большое торговое дело. Ежегодный оборот около ста тысяч рублей, желает познакомиться в целях брака, с барышней или вдовой не старше 30 лет. Предпочитаю брюнетку высокого роста, полную, с капиталом. Предложение серьезное.");
        try {
            this.profiles.get(0).setPreference(new Preference("Сударыня"));
        } catch (BusinessException e) {
            throw new RuntimeException(e);
        }

        try {
            this.profiles.get(1).setGender(new Gender("Сударыня"));
        } catch (BusinessException e) {
            throw new RuntimeException(e);
        }
        this.profiles.get(1).setName("Никто");
        this.profiles.get(1).setTitle("Желаю выйти замуж");
        this.profiles.get(1).setDescription("Брюнетка, выше среднего роста, стройная, неполная, 25 л., интеллигентная, говорят очень недурненькая, но бедна, приданого нет. Надоело одиночество в Сибири, хочется выйти замуж в России или на Кавказе за господина, способного и мне оказать материальную помощь. Буду любящей преданной женой. Люблю семью и хозяйство. Ищущих приключений и любопытных прошу не беспокоиться. Таким не отвечу.");
        try {
            this.profiles.get(1).setPreference(new Preference("Сударъ"));
        } catch (BusinessException e) {
            throw new RuntimeException(e);
        }

        try {
            this.profiles.get(2).setGender(new Gender("Сударъ"));
        } catch (BusinessException e) {
            throw new RuntimeException(e);
        }
        this.profiles.get(2).setName("Ничто");
        this.profiles.get(2).setTitle("Холостой человек");
        this.profiles.get(2).setDescription("33 лет, желает познакомиться с особой, при взаимном сочувствии брак. Прелестного отзывчивого характера, коммерсант, не долюбливаю спиртные напитки, не курю, в карты не играю. Тайну переписки гарантирую честным словом.");
        try {
            this.profiles.get(2).setPreference(new Preference("Сударыня"));
        } catch (BusinessException e) {
            throw new RuntimeException(e);
        }
    }

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    public User findUserByChatId(Long chatId) throws BusinessException {
        Optional<User> optUser = users.stream()
                .filter(u -> u.getChatId() == chatId)
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
            String title;
            String[] splitted = description.split("\n");
            if (splitted.length == 1) {
                title = description.split(" ")[0];
                description = description.replaceFirst("[^ ]+", "");
            } else {
                title = splitted[0];
                description = description.replaceFirst("[^\n]+", "");
            }
            profile.setTitle(title);
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
            users.add(new User(chatId, status));
            logger.debug("Новый пользователь chatId={} был добавлен.", chatId);
        }
        else {
            throw new BusinessException("Пользователь с chatId={" + chatId + "} уже существует. ChatId должен быть уникальным.");
        }
    }

    public void createProfile(Long chatId) throws BusinessException {
        if (isProfileDoesNotExist(chatId)) {
            profiles.add(new Profile(chatId));
            logger.debug("Новая анкета пользователя chatId={} была добавлена.", chatId);
        } else {
            throw new BusinessException("Анкета с chatId={" + chatId + "} уже существует. ChatId должен быть уникальным.");
        }
    }

    public boolean isUserDoesNotExist(Long chatId) {
        Optional<User> optUser = users.stream()
                .filter(u -> u.getChatId() == chatId)
                .findAny();
        if (optUser.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isProfileDoesNotExist(Long chatId) {
        Optional<Profile> optProfile = findAllProfiles().stream()
                .filter(p -> p.getChatId().equals(chatId))
                .findAny();
        if (optProfile.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public List<User> findAllUsers() {
        return users;
    }

    public List<Profile> findAllProfiles() {
        return profiles;
    }

    public ByteArrayOutputStream findProfileUrl(Long chatId) throws BusinessException, ServiceException {
        ByteArrayOutputStream result;
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
        } catch (BusinessException e) {
            logger.error(e.getMessage());
        }
    }

    private void save(User user) {
        //логика
        logger.debug("Данные пользователя chatId="+user.getChatId()+" сохранены.");
    }

    public boolean incorrectKey(Long chatId) {
        logger.info("Значения для пользователя chatId="+chatId+" были обновлены по причине некорректного значения key в" +
                " полученном запросе. Допустимые значения: gender, name, description, preference.");
        return true;
    }
}
