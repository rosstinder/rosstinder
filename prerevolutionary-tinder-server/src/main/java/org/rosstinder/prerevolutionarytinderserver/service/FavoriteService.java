package org.rosstinder.prerevolutionarytinderserver.service;

import org.rosstinder.prerevolutionarytinderserver.exception.BusinessException;
import org.rosstinder.prerevolutionarytinderserver.exception.ServiceException;
import org.rosstinder.prerevolutionarytinderserver.model.entity.Favorite;
import org.rosstinder.prerevolutionarytinderserver.model.entity.Profile;
import org.rosstinder.prerevolutionarytinderserver.model.entity.User;
import org.rosstinder.prerevolutionarytinderserver.repository.FavoriteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
     * @param whoChatId кто поставил лайк/отказ
     * @param isLike флаг лайка (true) / отказа (false)
     * @return возвращает "Вы любимы" в случае взаимного лайка, иначе пустую строку
     */
    public String makeLikeOrDislike(Long whoChatId, boolean isLike, String status) throws BusinessException {
        String result;
        try {
            Long who = userService.findProfileIdByChatId(whoChatId);
            Long whom = userService.findUserByChatId(whoChatId).getLastProfileNumber();
            if (userService.isProfileDoesNotExistById(whom)) {
                logger.error("Попытка лайкнуть/дислайкнуть пользователя chatId={}, которого не существует.", whom);
                throw new BusinessException("Попытка лайкнуть/дислайкнуть пользователя chatId="+whom+", которого не существует.");
            }
            if (isLikeAlreadyExist(who, whom)) {
                editLikeOrDislike(who, whom, isLike);
            } else {
                saveFavorite(new Favorite(who, whom, isLike));
            }
            if (isMatch(who, whom)) {
                userService.updateUserStatus(whoChatId, status);
                result = "Вы любимы";
            }
            else {
                userService.updateUserStatus(whoChatId, status);
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
                .filter(l -> l.getWhoId().equals(who) && l.getWhomId().equals(whom))
                .findAny()
                .get();
        favorite.setLike(isLike);
        saveFavorite(favorite);
    }

    /**
     * Метод проверяет, существовал ли лайк/отказ ранее
     * @param who кто поставил лайк/отказ
     * @param whom кому поставили лайк/отказ
     * @return true если лайк/отказ уже сущетвует в БД, false если записи о лайке/отказе нет
     */
    private boolean isLikeAlreadyExist(Long who, Long whom) {
        return findAllFavorites().stream()
                .anyMatch(favorite -> favorite.getWhoId().equals(who) && favorite.getWhomId().equals(whom));
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
            Long who = userService.findProfileIdByChatId(chatId);
            Optional<Long> nextFavorite = findAllFavorites().stream()
                    .filter(l -> l.isLike() && l.getWhoId().equals(who))
                    .map(Favorite::getWhomId)
                    .sorted(Long::compareTo)
                    .filter(id -> id.compareTo(user.getLastFavoriteNumber()) > 0)
                    .findFirst();
            if (nextFavorite.isEmpty()) {
                user.setLastFavoriteNumber(User.ZERO_VALUE);
                nextFavorite = findAllFavorites().stream()
                        .filter(l -> l.isLike() && l.getWhoId().equals(who))
                        .map(Favorite::getWhomId)
                        .sorted(Long::compareTo)
                        .filter(id -> id.compareTo(user.getLastFavoriteNumber()) > 0)
                        .findFirst();
                if(nextFavorite.isEmpty()) {
                    userService.updateUserStatus(chatId, status);
                    logger.info("Пользователь chatId="+chatId+" не проявлял ни к кому любви.");
                    throw new BusinessException("Пользователь chatId="+chatId+" не проявлял ни к кому любви.");
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

    /**
     * Метод обновляет последнюю просматриваему анкету в разделе Любимцы
     * @param chatId идентификатор пользователя
     * @param favoriteNumber идентификатор просмотренной анкеты
     * @throws BusinessException если пользователь не найден
     */
    public void updateUserFavoriteNumber(Long chatId, Long favoriteNumber) throws BusinessException {
        try {
            User user = userService.findUserByChatId(chatId);
            user.setLastFavoriteNumber(favoriteNumber);
            userService.saveUser(user);
        }
        catch (BusinessException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * Метод находит идендификатор предыдущей анкеты для просмотра
     * @param chatId идентификатор пользователя
     * @param status статус пользователя
     * @return идентификатор анкеты
     * @throws BusinessException если пользователь не был найден или у пользователя нет любимцев
     */
    public Long findPreviousFavoriteChatId(Long chatId, String status) throws BusinessException {
        Long result;
        try {
            User user = userService.findUserByChatId(chatId);
            Long who = userService.findProfileIdByChatId(chatId);
            Optional<Long> previousFavorite = findAllFavorites().stream()
                    .filter(l -> l.isLike() && l.getWhoId().equals(who))
                    .map(Favorite::getWhomId)
                    .sorted(Long::compareTo)
                    .filter(id -> id.compareTo(user.getLastFavoriteNumber()) < 0)
                    .findFirst();
            if (previousFavorite.isEmpty()) {
                user.setLastFavoriteNumber(Long.MAX_VALUE);
                previousFavorite = findAllFavorites().stream()
                        .filter(l -> l.isLike() && l.getWhoId().equals(who))
                        .map(Favorite::getWhomId)
                        .sorted(Long::compareTo)
                        .filter(id -> id.compareTo(user.getLastFavoriteNumber()) < 0)
                        .reduce((first, second) -> second);
                if(previousFavorite.isEmpty()) {
                    userService.updateUserStatus(chatId, status);
                    logger.info("Пользователь chatId="+chatId+" не проявлял ни к кому любви.");
                    throw new BusinessException("Пользователь chatId="+chatId+" не проявлял ни к кому любви.");
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

    /**
     * Метод находит статус отношений между текущим пользователем и просматриваемой им анкеты другого пользователя
     * @param whoChatId идентификатор пользователя, с которым проходит сравнение
     * @return строка с полом, именем анкеты и со статусом отношений
     * @throws BusinessException если один из пользователей не был найден
     */
    public String findFavoriteRelation(Long whoChatId) throws BusinessException {
        StringBuilder result = new StringBuilder("");
        try {
            Long whom = userService.findUserByChatId(whoChatId).getLastFavoriteNumber();
            Long who = userService.findProfileIdByChatId(whoChatId);
            Profile whomProfile = userService.findProfileById(whom);
            result.append(whomProfile.getGender()).append(", ").append(whomProfile.getName());
            if (isMatch(who, whom)) {
                result.append(", Взаимность");
            } else if (isUserFavoritesAnotherUser(whom, who)) {
                result.append(", Вы любимы");
            } else if (isUserFavoritesAnotherUser(who, whom)) {
                result.append(", Любим вами");
            }
        } catch (BusinessException e) {
            throw new BusinessException(e.getMessage());
        }
        return result.toString();
    }

    /**
     * Проверка на взаимность двух пользователей
     * @param who идентификатор первого пользователя
     * @param whom идетификатор второго пользователя
     * @return true - взаимность; false - не взаимность
     */
    private boolean isMatch(Long who, Long whom) {
        return isUserFavoritesAnotherUser(who, whom) && (isUserFavoritesAnotherUser(whom, who));
    }

    /**
     * Метод проверяет, любит ли пользователь другого пользователя
     * @param who кто любит
     * @param whom кого любит
     * @return true - пользователь whoChatId любит пользователя whomChatId; false - в противном случае
     */
    private boolean isUserFavoritesAnotherUser(Long who, Long whom) {
        Optional<Favorite> optFavorite = findAllFavorites().stream()
                .filter(f -> f.isLike() && f.getWhoId().equals(who) && f.getWhomId().equals(whom))
                .findAny();
        return optFavorite.isPresent();
    }

    /**
     * Сохранение отношения в БД
     * @param favorite
     */
    private void saveFavorite(Favorite favorite) {
        favoriteRepository.save(favorite);
    }

    /**
     * Нахождение картинки анкеты
     * @param id идентификатор пользоветеля, чья анкета нужна
     * @return массив байтов картинки
     * @throws BusinessException если пользователь не найден
     * @throws ServiceException если возникла ошибка при генерации картинки
     */
    public byte[] findProfileUrl(Long id) throws BusinessException, ServiceException {
        byte[] result;
        try {
            result = userService.findProfileUrl(id);
        } catch (BusinessException e) {
            throw new BusinessException(e.getMessage());
        } catch (ServiceException e) {
            throw new ServiceException(e.getMessage());
        }
        return result;
    }
}
