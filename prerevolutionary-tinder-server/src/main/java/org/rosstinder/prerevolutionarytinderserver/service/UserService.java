package org.rosstinder.prerevolutionarytinderserver.service;

import org.rosstinder.prerevolutionarytinderserver.model.Gender;
import org.rosstinder.prerevolutionarytinderserver.model.Preference;
import org.rosstinder.prerevolutionarytinderserver.model.entity.Profile;
import org.rosstinder.prerevolutionarytinderserver.model.entity.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserService {

    private List<User> users;
    private List<Profile> profiles;

    public UserService() {
        this.users = new ArrayList<>();
        this.users.add(new User(Long.valueOf(1), "start"));
        this.users.add(new User(Long.valueOf(2), "start"));

        this.profiles = new ArrayList<>();
        this.profiles.add(new Profile(Long.valueOf(1)));
        this.profiles.add(new Profile(Long.valueOf(2)));

        this.profiles.get(0).setGender(Gender.fromString("Сударъ"));
        this.profiles.get(0).setName("Некто");
        this.profiles.get(0).setTitle("Интеллигентный");
        this.profiles.get(0).setTitle("Одинокий купец, 37 лет, имеет около двадцати лет большое торговое дело. Ежегодный оборот около ста тысяч рублей, желает познакомиться в целях брака, с барышней или вдовой не старше 30 лет. Предпочитаю брюнетку высокого роста, полную, с капиталом. Предложение серьезное.");
        this.profiles.get(0).setPreference(Preference.FEMALE);

        this.profiles.get(1).setGender(Gender.fromString("Сударыня"));
        this.profiles.get(1).setName("Никто");
        this.profiles.get(1).setTitle("Желаю выйти замуж");
        this.profiles.get(1).setTitle("Брюнетка, выше среднего роста, стройная, неполная, 25 л., интеллигентная, говорят очень недурненькая, но бедна, приданого нет. Надоело одиночество в Сибири, хочется выйти замуж в России или на Кавказе за господина, способного и мне оказать материальную помощь. Буду любящей преданной женой. Люблю семью и хозяйство. Ищущих приключений и любопытных прошу не беспокоиться. Таким не отвечу.");
        this.profiles.get(1).setPreference(Preference.MALE);
    }


    public User findUserByChatId(Long chatId) {
        Optional<User> optUser = users.stream()
                .filter(u -> u.getChatId() == chatId)
                .findAny();
        return optUser.get();
    }

    private Profile findProfileByChatId(Long chatId) {
        return profiles.stream()
                .filter(p -> p.getChatId().equals(chatId))
                .findAny()
                .get();
    }

    public void updateGender(Long chatId, Gender gender) {
        Profile profile = findProfileByChatId(chatId);
        profile.setGender(gender);
    }

    public void updateName(Long chatId, String name) {
        Profile profile = findProfileByChatId(chatId);
        profile.setName(name);
    }

    public void updateDescription(Long chatId, String description) {
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
    }

    public void updatePreference(Long chatId, Preference preference) {
        Profile profile = findProfileByChatId(chatId);
        profile.setPreference(preference);
    }

    public void updateUserStatus(Long chatId, String status) {
        User user = findUserByChatId(chatId);
        user.setStatus(status);
    }

    public void createUser(Long chatId, String status) {
        users.add(new User(chatId, status));
    }

    public void createProfile(Long chatId) {
        profiles.add(new Profile(chatId));
    }

    public List<User> findAllUsers() {
        return users;
    }

    public List<Profile> findAllProfiles() {
        return profiles;
    }

    public String findProfileUrl(Long chatId) {
        return profiles.stream()
                .filter(p -> p.getChatId().equals(chatId))
                .map(Profile::toString)
                .findAny()
                .get();
    }

    public Long findNextProfileChatId(Long chatId) {
        User user = findUserByChatId(chatId);
        Profile profile = findProfileByChatId(chatId);
        Long lastSeenProfile = user.getLastProfileNumber();
        Optional<Long> nextProfile = findAllProfiles().stream()
                .filter(p -> !p.getChatId().equals(chatId))
                .filter(p -> Preference.compareGenderAndPreference(profile.getGender(), p.getPreference())
                        && Preference.compareGenderAndPreference(p.getGender(), profile.getPreference()))
                .map(Profile::getChatId)
                .sorted(Long::compareTo)
                .filter(id -> id.compareTo(user.getLastProfileNumber()) > 0)
                .findFirst();
        if(nextProfile.isEmpty()) {
            user.setLastProfileNumber(User.ZERO);
            nextProfile = findAllProfiles().stream()
                    .filter(p -> !p.getChatId().equals(chatId))
                    .filter(p -> Preference.compareGenderAndPreference(profile.getGender(), p.getPreference())
                            && Preference.compareGenderAndPreference(p.getGender(), profile.getPreference()))
                    .map(Profile::getChatId)
                    .sorted(Long::compareTo)
                    .filter(id -> id.compareTo(user.getLastProfileNumber()) > 0)
                    .findFirst();
        }
        updateUserProfileNumber(chatId, nextProfile.get());
        return nextProfile.get();
    }

    private void updateUserProfileNumber(Long chatId, Long profileNumber) {
        User user = findUserByChatId(chatId);
        user.setLastProfileNumber(profileNumber);
    }

}
