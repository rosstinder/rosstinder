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

    @PostMapping(value = "/{chatId}")
    @ResponseStatus(HttpStatus.OK)
    public Response makeLikeOrDislike(@PathVariable("chatId") Long chatId, boolean isLike, String status) {
        Response response;
        try {
            response = new Response(chatId, status, HttpStatus.OK.toString(),
                    favoriteService.makeLikeOrDislike(chatId, isLike, status), null);
        } catch (BusinessException e) {
            response = handleException(e, HttpStatus.NOT_FOUND.toString());
        }
        return response;
    }

    @GetMapping(value = "/{chatId}/nextFavorite")
    @ResponseStatus(HttpStatus.OK)
    public Response searchNextFavorite(@PathVariable("chatId") Long chatId, String status) {
        Response response;
        try {
            Long favoriteProfileId = favoriteService.findNextFavoriteChatId(chatId, status);
            response = new Response(chatId,
                    status, HttpStatus.OK.toString(), favoriteService.findFavoriteRelation(chatId),
                    favoriteService.findProfileUrl(favoriteProfileId));
        } catch (BusinessException e) {
            response = handleException(e, HttpStatus.NOT_FOUND.toString());
        } catch (ServiceException e) {
            response = handleException(e, HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }
        return response;
    }

    @GetMapping(value = "/{chatId}/previousFavorite")
    @ResponseStatus(HttpStatus.OK)
    public Response searchPreviousFavorite(@PathVariable("chatId") Long chatId, String status) {
        Response response;
        try {
            Long favoriteChatId = favoriteService.findPreviousFavoriteChatId(chatId, status);
            response = new Response(favoriteChatId,
                    status, HttpStatus.OK.toString(), favoriteService.findFavoriteRelation(chatId),
                    favoriteService.findProfileUrl(favoriteChatId));
        } catch (BusinessException e) {
            response = handleException(e, HttpStatus.NOT_FOUND.toString());
        } catch (ServiceException e) {
            response = handleException(e, HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }
        return response;
    }

    @ExceptionHandler(BusinessException.class)
    public Response handleException(BusinessException e, String httpStatus) {
        return new Response(null, null, httpStatus, e.getMessage(), null);
    }

    @ExceptionHandler(ServiceException.class)
    public Response handleException(ServiceException e, String httpStatus) {
        return new Response(null, null, httpStatus, e.getMessage(), null);
    }
}
