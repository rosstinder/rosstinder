package org.rosstinder.prerevolutionarytinderserver.controller;

import org.rosstinder.prerevolutionarytinderserver.service.FavoriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {
    private final Logger logger = LoggerFactory.getLogger(FavoriteController.class);
    private final FavoriteService favoriteService = new FavoriteService();

    @PostMapping(value = "/{chatId}")
    @ResponseStatus(HttpStatus.OK)
    public String makeLikeOrDislike(@PathVariable("chatId") Long chatId, Long whom, boolean isLike, String status) {
        return favoriteService.makeLikeOrDislike(chatId, whom, isLike, status);
    }

    @GetMapping(value = "/{chatId}/nextFavorite")
    @ResponseStatus(HttpStatus.OK)
    public Long searchNextFavorite(@PathVariable("chatId") Long chatId, String status) {
        Long profileChatId = favoriteService.findNextFavoriteChatId(chatId, status);
        return profileChatId;
    }

    @GetMapping(value = "/{chatId}/previousFavorite")
    @ResponseStatus(HttpStatus.OK)
    public Long searchPreviousFavorite(@PathVariable("chatId") Long chatId, String status) {
        Long profileChatId = favoriteService.findPreviousFavoriteChatId(chatId, status);
        return profileChatId;
    }

   @GetMapping(value = "/{chatId}/likeChecker")
   @ResponseStatus(HttpStatus.OK)
   public String checkLike(@PathVariable("chatId") Long chatId) {
       return favoriteService.checkLike(chatId);
   }
}
