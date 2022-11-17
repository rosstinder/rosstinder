package org.rosstinder.prerevolutionarytinderserver.service;

public interface FavoriteService {
    void makeLikeOrDislike(Long whoChatId, boolean isLike);

    Long findNextFavoriteChatId(Long chatId);

    Long findPreviousFavoriteChatId(Long chatId);

    String findFavoriteRelation(Long whoChatId);

    String findProfileByteString(Long id);

    String getMassageIfMatch(Long whoChatId);
}
