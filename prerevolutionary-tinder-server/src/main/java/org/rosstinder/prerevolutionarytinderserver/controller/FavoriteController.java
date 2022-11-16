package org.rosstinder.prerevolutionarytinderserver.controller;

import lombok.AllArgsConstructor;
import org.rosstinder.prerevolutionarytinderserver.exception.BusinessException;
import org.rosstinder.prerevolutionarytinderserver.exception.ServiceException;
import org.rosstinder.prerevolutionarytinderserver.model.Response;
import org.rosstinder.prerevolutionarytinderserver.service.FavoriteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favorites")
@AllArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;

    @GetMapping(value = "/{chatId}")
    @ResponseStatus(HttpStatus.OK)
    public Response findIsMatch(@PathVariable("chatId") Long chatId) {
        return new Response(null, HttpStatus.OK.toString(),
                favoriteService.getMassageIfMatch(chatId), null);
    }

    @PostMapping(value = "/{chatId}")
    @ResponseStatus(HttpStatus.OK)
    public Response makeLikeOrDislike(@PathVariable("chatId") Long chatId, boolean isLike) {
        favoriteService.makeLikeOrDislike(chatId, isLike);
        return new Response(null, HttpStatus.OK.toString(), null, null);
    }

    @GetMapping(value = "/{chatId}/next")
    @ResponseStatus(HttpStatus.OK)
    public Response searchNextFavorite(@PathVariable("chatId") Long chatId) {
        Long favoriteProfileId = favoriteService.findNextFavoriteChatId(chatId);
        return new Response(null, HttpStatus.OK.toString(),
                favoriteService.findFavoriteRelation(chatId),
                favoriteService.findProfileUrl(favoriteProfileId));
    }

    @GetMapping(value = "/{chatId}/previous")
    @ResponseStatus(HttpStatus.OK)
    public Response searchPreviousFavorite(@PathVariable("chatId") Long chatId) {
        Long favoriteProfileId = favoriteService.findPreviousFavoriteChatId(chatId);
        return new Response(null, HttpStatus.OK.toString(),
                favoriteService.findFavoriteRelation(chatId),
                favoriteService.findProfileUrl(favoriteProfileId));
    }

    @ExceptionHandler(BusinessException.class)
    public Response handleException(BusinessException e, String httpStatus) {
        return new Response(null, httpStatus, e.getMessage(), null);
    }

    @ExceptionHandler(ServiceException.class)
    public Response handleException(ServiceException e, String httpStatus) {
        return new Response(null, httpStatus, e.getMessage(), null);
    }
}
