package org.rosstinder.prerevolutionarytinderserver.controller;

import org.rosstinder.prerevolutionarytinderserver.service.LikeService;
import org.rosstinder.prerevolutionarytinderserver.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/favorites")
public class LikesController {
    private final Logger logger = LoggerFactory.getLogger(LikesController.class);
    private final LikeService likeService = new LikeService();

    @PostMapping(value = "/{chatId}")
    @ResponseStatus(HttpStatus.OK)
    public String makeLikeOrDislike(@PathVariable("chatId") Long chatId, Long whom, boolean isLike) {
        return likeService.makeLikeOrDislike(chatId, whom, isLike);
    }

    @GetMapping(value = "/{chatId}/nextFavorite")
    @ResponseStatus(HttpStatus.OK)
    public Long searchNextFavorite(@PathVariable("chatId") Long chatId, String status) {
        Long profileChatId = likeService.findNextFavoriteChatId(chatId);
        return profileChatId;
    }

    @GetMapping(value = "/{chatId}/previousFavorite")
    @ResponseStatus(HttpStatus.OK)
    public Long searchPreviousFavorite(@PathVariable("chatId") Long chatId, String status) {
        Long profileChatId = likeService.findPreviousFavoriteChatId(chatId);
        return profileChatId;
    }
}
