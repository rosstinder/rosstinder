package org.rosstinder.prerevolutionarytinderserver.service;

import org.rosstinder.prerevolutionarytinderserver.exception.BusinessException;
import org.rosstinder.prerevolutionarytinderserver.model.entity.Favorite;
import org.rosstinder.prerevolutionarytinderserver.model.entity.Profile;
import org.rosstinder.prerevolutionarytinderserver.model.entity.User;
import org.rosstinder.prerevolutionarytinderserver.model.repository.FavoriteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FavoriteService {
    private final Logger logger = LoggerFactory.getLogger(FavoriteService.class);
    private final UserService userService;
    private final FavoriteRepository favoriteRepository;

    public FavoriteService(FavoriteRepository favoriteRepository, UserService userService) {
        this.favoriteRepository = favoriteRepository;
        this.userService = userService;
    }

    /**
     * Метод проставляет метку лайка/отказа
     *
     * @param who кто поставил лайк/отказ
     * @param isLike флаг лайка (true) / отказа (false)
     * @return возвращает "Вы любимы" в случае взаимного лайка, иначе пустую строку
     */
    public String makeLikeOrDislike(Long who, boolean isLike, String status) throws BusinessException {
        String result;
        try {
            Long whom = userService.findUserByChatId(who).getLastProfileNumber();
            if (userService.isUserDoesNotExist(whom)) {
                logger.error("Попытка лайкнуть/дислайкнуть пользователя chatId={}, которого не существует.", whom);
                throw new BusinessException("Попытка лайкнуть/дислайкнуть пользователя chatId="+whom+", которого не существует.");
            }
            if (isLikeAlreadyExist(who, whom)) {
                editLikeOrDislike(who, whom, isLike);
            } else {
                save(new Favorite(who, whom, isLike));
            }
            if (isMatch(who, whom)) {
                userService.updateUserStatus(who, status);
                result = "Вы любимы";
            }
            else {
                userService.updateUserStatus(who, status);
                result = "";
            }
        } catch (BusinessException e) {
            throw new BusinessException(e.getMessage());
        }
        return result;
    }

    /**
     * Получить все содержимое таблицы likes
     * @return список записей таблицы likes
     */
    private List<Favorite> findAllFavorites() {
        return favoriteRepository.findAll();
    }

    private void editLikeOrDislike(Long who, Long whom, boolean isLike) {
        Favorite favorite = findAllFavorites().stream()
                .filter(l -> l.getWhoChatId().equals(who) && l.getWhomChatId().equals(whom))
                .findAny()
                .get();
        favorite.setLike(isLike);
    }

    /**
     * Метод проверяет, существовал ли лайк/отказ ранее
     * @param who кто поставил лайк/отказ
     * @param whom кому поставили лайк/отказ
     * @return true если лайк/отказ уже сущетвует в БД, false если записи о лайке/отказе нет
     */
    private boolean isLikeAlreadyExist(Long who, Long whom) {
        return findAllFavorites().stream()
                .anyMatch(favorite -> favorite.getWhoChatId().equals(who) && favorite.getWhomChatId().equals(whom));
    }

    /**
     * Метод вычисляет id следующей анкеты Любимцев
     * @param chatId пользователь, который просматривает своих Любимцев
     * @return chatId пользователя, которого следует отобразить следующим при просмотре Любимцев
     */
    public Long findNextFavoriteChatId(Long chatId, String status) throws BusinessException {
        Long result;
        try {
            User user = userService.findUserByChatId(chatId);
            Optional<Long> nextFavorite = findAllFavorites().stream()
                    .filter(l -> l.getWhoChatId().equals(chatId))
                    .map(Favorite::getWhomChatId)
                    .sorted(Long::compareTo)
                    .filter(id -> id.compareTo(user.getLastFavoriteNumber()) > 0)
                    .findFirst();
            if (nextFavorite.isEmpty()) {
                user.setLastFavoriteNumber(User.ZERO_VALUE);
                nextFavorite = findAllFavorites().stream()
                        .filter(l -> l.getWhoChatId().equals(chatId))
                        .map(Favorite::getWhomChatId)
                        .sorted(Long::compareTo)
                        .filter(id -> id.compareTo(user.getLastFavoriteNumber()) > 0)
                        .findFirst();
                if(nextFavorite.isEmpty()) {
                    userService.updateUserStatus(chatId, status);
                    logger.info("Отсутствуют анкеты, удовлетворяющие критериям пользователя chatId="+chatId+".");
                    throw new BusinessException("Отсутствуют анкеты, удовлетворяющие критериям пользователя chatId="+chatId+".");
                }
            }
            result = nextFavorite.get();
            updateUserFavoriteNumber(chatId, result);
            userService.updateUserStatus(chatId, status);
        } catch (BusinessException e) {
            throw new BusinessException(e.getMessage());
        }

        return result;
    }

    public void updateUserFavoriteNumber(Long chatId, Long favoriteNumber) throws BusinessException {
        try {
            User user = userService.findUserByChatId(chatId);
            user.setLastFavoriteNumber(favoriteNumber);
        }
        catch (BusinessException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    public Long findPreviousFavoriteChatId(Long chatId, String status) throws BusinessException {
        Long result;
        try {
            User user = userService.findUserByChatId(chatId);
            Optional<Long> previousFavorite = findAllFavorites().stream()
                    .filter(l -> l.getWhoChatId().equals(chatId))
                    .map(Favorite::getWhomChatId)
                    .sorted(Long::compareTo)
                    .filter(id -> id.compareTo(user.getLastFavoriteNumber()) < 0)
                    .findFirst();
            if (previousFavorite.isEmpty()) {
                user.setLastFavoriteNumber(Long.MAX_VALUE);
                previousFavorite = findAllFavorites().stream()
                        .filter(l -> l.getWhoChatId().equals(chatId))
                        .map(Favorite::getWhomChatId)
                        .sorted(Long::compareTo)
                        .filter(id -> id.compareTo(user.getLastFavoriteNumber()) < 0)
                        .reduce((first, second) -> second);
                if(previousFavorite.isEmpty()) {
                    userService.updateUserStatus(chatId, status);
                    logger.info("Пользователь chatId="+chatId+" никого не лайкал.");
                    throw new BusinessException("Пользователь chatId="+chatId+" никого не лайкал.");
                }
            }
            result = previousFavorite.get();
            updateUserFavoriteNumber(chatId, result);
            userService.updateUserStatus(chatId, status);
        } catch (BusinessException e) {
            throw new BusinessException(e.getMessage());
        }
        return result;
    }

    public String checkLike(Long whoChatId) throws BusinessException {
        StringBuilder result = new StringBuilder("");
        try {
            Long whomChatId = userService.findUserByChatId(whoChatId).getLastFavoriteNumber();
            Profile whomProfile = userService.findProfileByChatId(whomChatId);
            result.append(whomProfile.getGender().getGender()).append(", ").append(whomProfile.getName());
            if (isMatch(whoChatId, whomChatId)) {
                result.append(", Взаимность");
            } else if (isUserFavoritesAnotherUser(whomChatId, whoChatId)) {
                result.append(", Вы любимы");
            } else if (isUserFavoritesAnotherUser(whoChatId, whomChatId)) {
                result.append(", Любим вами");
            }
        } catch (BusinessException e) {
            throw new BusinessException(e.getMessage());
        }
        return result.toString();
    }

    private boolean isMatch(Long whoChatId, Long whomChatId) {
        return isUserFavoritesAnotherUser(whoChatId, whomChatId) && (isUserFavoritesAnotherUser(whomChatId, whoChatId));
    }

    private boolean isUserFavoritesAnotherUser(Long whoChatId, Long whomChatId) {
        Optional<Favorite> optFavorite = findAllFavorites().stream()
                .filter(f -> f.isLike() && f.getWhoChatId().equals(whoChatId) && f.getWhomChatId().equals(whomChatId))
                .findAny();
        return optFavorite.isPresent();
    }

    private void save(Favorite favorite) {
        favoriteRepository.save(favorite);
    }
}
