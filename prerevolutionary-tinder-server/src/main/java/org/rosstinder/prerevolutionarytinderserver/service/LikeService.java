package org.rosstinder.prerevolutionarytinderserver.service;

import org.rosstinder.prerevolutionarytinderserver.model.Preference;
import org.rosstinder.prerevolutionarytinderserver.model.entity.Like;
//import org.rosstinder.prerevolutionarytinderserver.model.repository.LikeRepository;
import org.rosstinder.prerevolutionarytinderserver.model.entity.Profile;
import org.rosstinder.prerevolutionarytinderserver.model.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class LikeService {
    private final Logger logger = LoggerFactory.getLogger(LikeService.class);
    private final UserService userService = new UserService();
    private List<Like> likes = new ArrayList<>();
    public LikeService() {
        likes.add(new Like(Long.valueOf(2),Long.valueOf(1)));
    }

    /**
     * Метод проставляет метку лайка/отказа
     *
     * @param who кто поставил лайк/отказ
     * @param whom кому поставили лайк/отказ
     * @param isLike флаг лайка (true) / отказа (false)
     * @return возвращает "Вы любимы" в случае взаимного лайка, иначе пустую строку
     */
    public String makeLikeOrDislike(Long who, Long whom, boolean isLike) {
        //Long whom = userService.findUserByChatId(who).getLastProfileNumber();
        if (isLikeAlreadyExist(who, whom)) {
            editLikeOrDislike(who, whom, isLike);
        } else {
            likes.add(new Like(who, whom, isLike));
        }
        if (isLikeAlreadyExist(whom, who)) {
            return "Вы любимы";
        }
        else {
            return "";
        }
    }

    /**
     * Получить все содержимое таблицы likes
     * @return список записей таблицы likes
     */
    private List<Like> findAllLikes() {
        return likes;
    }

    private void editLikeOrDislike(Long who, Long whom, boolean isLike) {
        Like like = likes.stream()
                .filter(l -> l.getWhoChatId().equals(who) && l.getWhomChatId().equals(whom))
                .findAny()
                .get();
        like.setLike(isLike);
    }

    /**
     * Метод проверяет, существовал ли лайк/отказ ранее
     * @param who кто поставил лайк/отказ
     * @param whom кому поставили лайк/отказ
     * @return true если лайк/отказ уже сущетвует в БД, false если записи о лайке/отказе нет
     */
    private boolean isLikeAlreadyExist(Long who, Long whom) {
        return likes.stream()
                .filter(like -> like.getWhoChatId().equals(who) && like.getWhomChatId().equals(whom))
                .findAny()
                .isPresent();
    }

    /**
     * Метод вычисляет id следующей анкеты Любимцев
     * @param chatId пользователь, который просматривает своих Любимцев
     * @return chatId пользователя, которого следует отобразить следующим при просмотре Любимцев
     */
    public Long findNextFavoriteChatId(Long chatId) {
        User user = userService.findUserByChatId(chatId);
        Optional<Long> nextFavorite = findAllLikes().stream()
                .filter(l -> l.getWhoChatId().equals(chatId))
                .map(Like::getWhomChatId)
                .sorted(Long::compareTo)
                .filter(id -> id.compareTo(user.getLastFavoriteNumber()) > 0)
                .findFirst();
        if (nextFavorite.isEmpty()) {
            user.setLastFavoriteNumber(User.ZERO);
            nextFavorite = findAllLikes().stream()
                    .filter(l -> l.getWhoChatId().equals(chatId))
                    .map(Like::getWhomChatId)
                    .sorted(Long::compareTo)
                    .filter(id -> id.compareTo(user.getLastFavoriteNumber()) > 0)
                    .findFirst();
            if(nextFavorite.isEmpty()) {
                return null;
            }
        }
        userService.updateUserProfileNumber(chatId, nextFavorite.get());
        return nextFavorite.get();
    }


    //private final LikeRepository likeRepository;

    //public LikeService(LikeRepository likeRepository) {
    //    this.likeRepository = likeRepository;
    //}

    //public UUID like(UUID who, UUID whom) {
    //    logger.debug("Starting method <like>...");
    //    Like like = withLike(who, whom); //может вернуться NullPointerException, если who или whom null
    //    if (likeRepository.isUserMatchesWithAnotherUser(who, whom)) {
    //        //"Вы любимы"
    //    } else {
    //        //"ОК"
    //    }
    //    //void вероятно убрать
    //    logger.debug("Ending <like> method...");
    //    return like.getId();
    //}
//
    //public UUID dislike(UUID who, UUID whom) {
    //    logger.debug("Starting method <dislike>...");
    //    Like dislike = withDislike(who, whom); //может вернуться NullPointerException, если who или whom null
    //    //void вероятно убрать
    //    logger.debug("Ending <dislike> method...");
    //    return dislike.getId();
    //}
//
    //public boolean isLikeOrDislikeExist(UUID who, UUID whom) {
    //    logger.debug("Starting method <isLikeOrDislikeExist>...");
    //    if (likeRepository.isUserLikedAnotherUser(who, whom)) {
    //        logger.debug("Ending <isLikeOrDislikeExist> method...");
    //        return true;
    //    } else {
    //        logger.debug("Ending <isLikeOrDislikeExist> method...");
    //        return false;
    //    }
    //}
//
    //public List<UUID> getAllUserMatches(UUID who) {
    //    return likeRepository.getAllUserLikes(who).stream()
    //            .filter(like -> likeRepository.isUserMatchesWithAnotherUser(like.getWhom(), who))
    //            .map(Like::getWhom)
    //            .collect(Collectors.toList());
    //}
//
    //public List<UUID> getAllUserLikes(UUID who) {
    //    return likeRepository.getAllUserLikes(who).stream()
    //            .map(Like::getWhom)
    //            .collect(Collectors.toList());
    //}
//
    //public List<UUID> getUsersWhoLikesUser(UUID whom) {
    //    return likeRepository.getAllLikes().stream()
    //            .filter(like -> likeRepository.isUserLikedAnotherUser(like.getWho(), whom))
    //            .map(Like::getWho)
    //            .collect(Collectors.toList());
    //}
//
//
    //private Like withLike(UUID who, UUID whom) {
    //    Like like = new Like(who, whom);
    //    like.setLike(LIKE);
    //    return like;
    //}
    //
    //private Like withDislike(UUID who, UUID whom) {
    //    Like dislike = new Like(who, whom);
    //    dislike.setLike(DISLIKE);
    //    return dislike;
    //}
}
