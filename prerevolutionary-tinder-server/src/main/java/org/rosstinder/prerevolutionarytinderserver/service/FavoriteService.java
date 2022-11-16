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
     * @param isLike    флаг лайка (true) / отказа (false)
     * @return возвращает "Вы любимы" в случае взаимного лайка, иначе пустую строку
     */
    public void makeLikeOrDislike(Long whoChatId, boolean isLike) throws BusinessException {
        try {
            Long who = userService.findProfileIdByChatId(whoChatId);
            Long whom = userService.findUserByChatId(whoChatId).getLastProfileNumber();
            if (userService.isProfileDoesNotExistById(whom)) {
                logger.error("Попытка лайкнуть/дислайкнуть пользователя chatId={}, которого не существует.", whom);
                throw new BusinessException("Попытка лайкнуть/дислайкнуть пользователя chatId=" +
                        whom + ", которого не существует.");
            }
            if (isLikeAlreadyExist(who, whom)) {
                editLikeOrDislike(who, whom, isLike);
            } else {
                saveFavorite(new Favorite(who, whom, isLike));
            }
        } catch (BusinessException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * Получить все содержимое таблицы likes
     *
     * @return список записей таблицы likes
     */
    private List<Favorite> findAllFavorites() {
        return favoriteRepository.findAllFavorites();
    }

    /**
     * Метод редактирует или добавляет новые отношения
     *
     * @param who    кто лайкнул/отверг
     * @param whom   кого лайкнули/отвергли
     * @param isLike true - лайк, false - отказ
     */
    private void editLikeOrDislike(Long who, Long whom, boolean isLike) {
        Favorite favorite = favoriteRepository.findFavoriteByWhoAndWhom(who, whom);
        favorite.setLike(isLike);
        saveFavorite(favorite);
    }

    /**
     * Метод проверяет, существовал ли лайк/отказ ранее
     *
     * @param who  кто поставил лайк/отказ
     * @param whom кому поставили лайк/отказ
     * @return true если лайк/отказ уже сущетвует в БД, false если записи о лайке/отказе нет
     */
    private boolean isLikeAlreadyExist(Long who, Long whom) {
        return Optional.ofNullable(favoriteRepository.findFavoriteByWhoAndWhom(who, whom)).isPresent();
    }

    /**
     * Метод вычисляет id следующей анкеты Любимцев
     *
     * @param chatId пользователь, который просматривает своих Любимцев
     * @return chatId пользователя, которого следует отобразить следующим при просмотре Любимцев
     */
    public Long findNextFavoriteChatId(Long chatId) throws BusinessException {
        Long result;
        try {
            User user = userService.findUserByChatId(chatId);
            Long who = userService.findProfileIdByChatId(chatId);
            Optional<Long> nextFavorite = favoriteRepository.findFavoritesLikeByWho(who).stream()
                    .map(Favorite::getWhomId)
                    .sorted(Long::compareTo)
                    .filter(id -> id.compareTo(user.getLastFavoriteNumber()) > 0)
                    .findFirst();
            if (nextFavorite.isEmpty()) {
                user.setLastFavoriteNumber(User.ZERO_VALUE);
                nextFavorite = favoriteRepository.findFavoritesLikeByWho(who).stream()
                        .filter(l -> l.isLike() && l.getWhoId().equals(who))
                        .map(Favorite::getWhomId)
                        .sorted(Long::compareTo)
                        .filter(id -> id.compareTo(user.getLastFavoriteNumber()) > 0)
                        .findFirst();
                if (nextFavorite.isEmpty()) {
                    logger.info("Пользователь chatId=" + chatId + " не проявлял ни к кому любви.");
                    throw new BusinessException("Пользователь chatId=" + chatId + " не проявлял ни к кому любви.");
                }
            }
            result = nextFavorite.get();
            updateUserFavoriteNumber(chatId, result);
        } catch (BusinessException e) {
            throw new BusinessException(e.getMessage());
        }

        return result;
    }

    /**
     * Метод обновляет последнюю просматриваему анкету в разделе Любимцы
     *
     * @param chatId         идентификатор пользователя
     * @param favoriteNumber идентификатор просмотренной анкеты
     * @throws BusinessException если пользователь не найден
     */
    public void updateUserFavoriteNumber(Long chatId, Long favoriteNumber) throws BusinessException {
        try {
            User user = userService.findUserByChatId(chatId);
            user.setLastFavoriteNumber(favoriteNumber);
            userService.saveUser(user);
        } catch (BusinessException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * Метод находит идендификатор предыдущей анкеты для просмотра
     *
     * @param chatId идентификатор пользователя
     * @return идентификатор анкеты
     * @throws BusinessException если пользователь не был найден или у пользователя нет любимцев
     */
    public Long findPreviousFavoriteChatId(Long chatId) throws BusinessException {
        Long result;
        try {
            User user = userService.findUserByChatId(chatId);
            Long who = userService.findProfileIdByChatId(chatId);
            Optional<Long> previousFavorite = favoriteRepository.findFavoritesLikeByWho(who).stream()
                    .map(Favorite::getWhomId)
                    .sorted(Long::compareTo)
                    .filter(id -> id.compareTo(user.getLastFavoriteNumber()) < 0)
                    .findFirst();
            if (previousFavorite.isEmpty()) {
                user.setLastFavoriteNumber(Long.MAX_VALUE);
                previousFavorite = favoriteRepository.findFavoritesLikeByWho(who).stream()
                        .map(Favorite::getWhomId)
                        .sorted(Long::compareTo)
                        .filter(id -> id.compareTo(user.getLastFavoriteNumber()) < 0)
                        .reduce((first, second) -> second);
                if (previousFavorite.isEmpty()) {
                    logger.info("Пользователь chatId=" + chatId + " не проявлял ни к кому любви.");
                    throw new BusinessException("Пользователь chatId=" + chatId + " не проявлял ни к кому любви.");
                }
            }
            result = previousFavorite.get();
            updateUserFavoriteNumber(chatId, result);
        } catch (BusinessException e) {
            throw new BusinessException(e.getMessage());
        }
        return result;
    }

    /**
     * Метод находит статус отношений между текущим пользователем и просматриваемой им анкеты другого пользователя
     *
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
     *
     * @param who  идентификатор первого пользователя
     * @param whom идетификатор второго пользователя
     * @return true - взаимность; false - не взаимность
     */
    private boolean isMatch(Long who, Long whom) {
        return isUserFavoritesAnotherUser(who, whom) && (isUserFavoritesAnotherUser(whom, who));
    }

    /**
     * Метод проверяет, любит ли пользователь другого пользователя
     *
     * @param who  кто любит
     * @param whom кого любит
     * @return true - пользователь whoChatId любит пользователя whomChatId; false - в противном случае
     */
    private boolean isUserFavoritesAnotherUser(Long who, Long whom) {
        Optional<Favorite> optFavorite = Optional.ofNullable(favoriteRepository.findFavoriteByWhoAndWhom(who, whom));
        if (optFavorite.isEmpty()) {
            return false;
        } else {
            return optFavorite.get().isLike();
        }
    }

    /**
     * Сохранение отношения
     *
     * @param favorite экземпляр отношения
     */
    private void saveFavorite(Favorite favorite) {
        favoriteRepository.save(favorite);
    }

    /**
     * Нахождение картинки анкеты
     *
     * @param id идентификатор пользоветеля, чья анкета нужна
     * @return массив байтов картинки
     * @throws BusinessException если пользователь не найден
     * @throws ServiceException  если возникла ошибка при генерации картинки
     */
    public String findProfileUrl(Long id) throws BusinessException, ServiceException {
        String result;
        try {
            result = userService.findProfileUrl(id);
        } catch (BusinessException e) {
            throw new BusinessException(e.getMessage());
        } catch (ServiceException e) {
            throw new ServiceException(e.getMessage());
        }
        return result;
    }

    /**
     * Метод формирования сообщения в случает взаимности
     *
     * @param whoChatId идентификтаор пользователя
     * @return строку с описанием взаимности, либо пустую строку, если в заимность не состоялась
     */
    public String getMassageIfMatch(Long whoChatId) throws BusinessException {
        try {
            Long who = userService.findProfileIdByChatId(whoChatId);
            Long whom = userService.findUserByChatId(whoChatId).getLastProfileNumber();
            if (isMatch(who, whom)) {
                return "Вы любимы";
            } else {
                return "";
            }
        } catch (BusinessException e) {
            throw new BusinessException(e.getMessage());
        }
    }
}
