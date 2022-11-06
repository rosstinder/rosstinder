package org.rosstinder.prerevolutionarytinderserver.service;

import org.rosstinder.prerevolutionarytinderserver.model.Gender;
import org.rosstinder.prerevolutionarytinderserver.model.Preference;
import org.rosstinder.prerevolutionarytinderserver.model.entity.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserService {

    private List<User> users;

    public UserService() {
        this.users = new ArrayList<>();
        this.users.add(new User(Long.valueOf(1), "user1", Gender.MALE, "Некто", "Интеллигентный",
                "Одинокий купец, 37 лет, имеет около двадцати лет большое торговое дело. Ежегодный оборот около ста тысяч рублей, желает познакомиться в целях брака, с барышней или вдовой не старше 30 лет. Предпочитаю брюнетку высокого роста, полную, с капиталом. Предложение серьезное.",
                Preference.ALL));
        this.users.add(new User(Long.valueOf(2), "user2", Gender.FEMALE, "Никто", "Желаю выйти замуж",
                "Брюнетка, выше среднего роста, стройная, неполная, 25 л., интеллигентная, говорят очень недурненькая, но бедна, приданого нет. Надоело одиночество в Сибири, хочется выйти замуж в России или на Кавказе за господина, способного и мне оказать материальную помощь. Буду любящей преданной женой. Люблю семью и хозяйство. Ищущих приключений и любопытных прошу не беспокоиться. Таким не отвечу.",
                Preference.MALE));
    }


    public User findById(Long chatId) {
        Optional<User> optUser = users.stream()
                .filter(u -> u.getChatId() == chatId)
                .findAny();
        return optUser.get();
    }


    public int create(User user) {
        users.add(user);
        return 0;
    }

    public void update(User user) {
        User userToEdit = users.stream()
                .filter(u -> u.getChatId() == user.getChatId())
                .findAny()
                .get();

    }

    public void deleteById(Long chatId) {
        users.removeIf(u -> u.getChatId() == chatId);
    }

    public List<User> findAll() {
        return users;
    }

    public void updateGender(Long id, Gender gender) {
        User user = findById(id);
        user.setGender(gender);
    }

    public void updateDescription(Long id, String description) {
        User user = findById(id);
        String title;
        String[] splitted = description.split("\n");
        if (splitted.length == 1) {
            title = description.split(" ")[0];
            description = description.replaceFirst("[^ ]+", "");
        } else {
            title = splitted[0];
            description = description.replaceFirst("[^\n]+", "");
        }
        user.setTitle(title);
        user.setDescription(description);
    }

    public void updatePreference(Long id, Preference preference) {
        User user = findById(id);
        user.setPreference(preference);
    }

    public List<User> search(Long id) {
        User user = findById(id);
        return findAll().stream()
                .filter(u -> !u.getChatId().equals(id))
                .filter(u -> Preference.compareGenderAndPreference(user.getGender(), u.getPreference())
                        && Preference.compareGenderAndPreference(u.getGender(), user.getPreference()))
                .collect(Collectors.toList());
    }
}
