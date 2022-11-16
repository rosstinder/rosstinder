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


@Service
public class FavoriteServiceImpl implements FavoriteService {
    private final Logger logger = LoggerFactory.getLogger(FavoriteServiceImpl.class);
    private final UserServiceImpl userService;
    private final FavoriteRepository favoriteRepository;

    public FavoriteServiceImpl(FavoriteRepository favoriteRepository, UserServiceImpl userService) {
        this.favoriteRepository = favoriteRepository;
        this.userService = userService;
    }

    /**
     * Метод проставляет метку лайка/отказа
     *
     * @param whoChatId кто поставил лайк/отказ
     * @param isLike    флаг лайка (true) / отказа (false)
     */
    @Override
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

    private void editLikeOrDislike(Long who, Long whom, boolean isLike) {
        Favorite favorite = favoriteRepository.findFavoriteByWhoAndWhom(who, whom);
        favorite.setLike(isLike);
        saveFavorite(favorite);
    }

    private boolean isLikeAlreadyExist(Long who, Long whom) {
        return favoriteRepository.findFavoriteByWhoAndWhom(who, whom) != null;
    }

    /**
     * Метод вычисляет id следующей анкеты Любимцев
     *
     * @param chatId пользователь, который просматривает своих Любимцев
     * @return chatId пользователя, которого следует отобразить следующим при просмотре Любимцев
     */
    @Override
    public Long findNextFavoriteChatId(Long chatId) throws BusinessException {
        Long nextFavoriteId;
        try {
            User user = userService.findUserByChatId(chatId);
            Long who = userService.findProfileIdByChatId(chatId);
            nextFavoriteId = favoriteRepository.findFavoritesLikeByWho(who).stream()
                    .map(Favorite::getWhomId)
                    .sorted(Long::compareTo)
                    .filter(id -> id.compareTo(user.getLastFavoriteNumber()) > 0)
                    .findFirst()
                    .orElse(null);
            if (nextFavoriteId == null) {
                user.setLastFavoriteNumber(User.ZERO_VALUE);
                nextFavoriteId = favoriteRepository.findFavoritesLikeByWho(who).stream()
                        .filter(l -> l.isLike() && l.getWhoId().equals(who))
                        .map(Favorite::getWhomId)
                        .sorted(Long::compareTo)
                        .filter(id -> id.compareTo(user.getLastFavoriteNumber()) > 0)
                        .findFirst()
                        .orElse(null);
                if (nextFavoriteId == null) {
                    logger.info("Пользователь chatId=" + chatId + " не проявлял ни к кому любви.");
                    throw new BusinessException("Пользователь chatId=" + chatId + " не проявлял ни к кому любви.");
                }
            }
            updateUserFavoriteNumber(chatId, nextFavoriteId);
        } catch (BusinessException e) {
            throw new BusinessException(e.getMessage());
        }
        return nextFavoriteId;
    }

    private void updateUserFavoriteNumber(Long chatId, Long favoriteNumber) throws BusinessException {
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
    @Override
    public Long findPreviousFavoriteChatId(Long chatId) throws BusinessException {
        Long previousFavoriteId;
        try {
            User user = userService.findUserByChatId(chatId);
            Long who = userService.findProfileIdByChatId(chatId);
            previousFavoriteId = favoriteRepository.findFavoritesLikeByWho(who).stream()
                    .map(Favorite::getWhomId)
                    .sorted(Long::compareTo)
                    .filter(id -> id.compareTo(user.getLastFavoriteNumber()) < 0)
                    .findFirst()
                    .orElse(null);
            if (previousFavoriteId == null) {
                user.setLastFavoriteNumber(Long.MAX_VALUE);
                previousFavoriteId = favoriteRepository.findFavoritesLikeByWho(who).stream()
                        .map(Favorite::getWhomId)
                        .sorted(Long::compareTo)
                        .filter(id -> id.compareTo(user.getLastFavoriteNumber()) < 0)
                        .reduce((first, second) -> second)
                        .orElse(null);
                if (previousFavoriteId == null) {
                    logger.info("Пользователь chatId=" + chatId + " не проявлял ни к кому любви.");
                    throw new BusinessException("Пользователь chatId=" + chatId + " не проявлял ни к кому любви.");
                }
            }
            updateUserFavoriteNumber(chatId, previousFavoriteId);
        } catch (BusinessException e) {
            throw new BusinessException(e.getMessage());
        }
        return previousFavoriteId;
    }

    /**
     * Метод находит статус отношений между текущим пользователем и просматриваемой им анкеты другого пользователя
     *
     * @param whoChatId идентификатор пользователя, с которым проходит сравнение
     * @return строка с полом, именем анкеты и со статусом отношений
     * @throws BusinessException если один из пользователей не был найден
     */
    @Override
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

    private boolean isMatch(Long who, Long whom) {
        return isUserFavoritesAnotherUser(who, whom) && (isUserFavoritesAnotherUser(whom, who));
    }

    private boolean isUserFavoritesAnotherUser(Long who, Long whom) {
        Favorite favorite = favoriteRepository.findFavoriteByWhoAndWhom(who, whom);
        if (favorite == null) {
            return false;
        } else {
            return favorite.isLike();
        }
    }

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
    @Override
    public String findProfileByteString(Long id) throws BusinessException, ServiceException {
        String result;
        try {
            result = userService.findProfileByteString(id);
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
    @Override
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
