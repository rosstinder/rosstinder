package org.rosstinder.prerevolutionarytinderserver.controller;

import org.rosstinder.prerevolutionarytinderserver.exception.BusinessException;
import org.rosstinder.prerevolutionarytinderserver.exception.ServiceException;
import org.rosstinder.prerevolutionarytinderserver.model.Response;
import org.rosstinder.prerevolutionarytinderserver.service.FavoriteService;
import org.rosstinder.prerevolutionarytinderserver.service.ImageGenerator;
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
            Long favoriteChatId = favoriteService.findNextFavoriteChatId(chatId, status);
            response = new Response(favoriteChatId,
                    status, HttpStatus.OK.toString(), favoriteService.checkLike(chatId),
                    favoriteService.findProfileUrl(favoriteChatId));
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
            Long favoriteChatId = favoriteService.findNextFavoriteChatId(chatId, status);
            response = new Response(favoriteChatId,
                    status, HttpStatus.OK.toString(), favoriteService.checkLike(chatId),
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
