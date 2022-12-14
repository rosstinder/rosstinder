package org.rosstinder.prerevolutionarytinderserver.controller;

import lombok.AllArgsConstructor;
import org.rosstinder.prerevolutionarytinderserver.exception.BusinessException;
import org.rosstinder.prerevolutionarytinderserver.exception.ServiceException;
import org.rosstinder.prerevolutionarytinderserver.model.Response;
import org.rosstinder.prerevolutionarytinderserver.service.UserServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    public final UserServiceImpl service;

    @GetMapping(value = "/{chatId}/status")
    @ResponseStatus(HttpStatus.OK)
    public Response getUserStatus(@PathVariable("chatId") Long chatId) {
        return new Response(service.findUserByChatId(chatId).getStatus(),
                HttpStatus.OK.toString(), null, null);
    }

    @GetMapping(value = "/{chatId}/profile")
    @ResponseStatus(HttpStatus.OK)
    public Response getProfilePictureUrl(@PathVariable("chatId") Long chatId) {
        Long id = service.findProfileIdByChatId(chatId);
        return new Response(null, HttpStatus.OK.toString(),
                service.findNameAndGender(id), service.findProfileByteString(id));
    }

    @PutMapping(value = "/{chatId}")
    @ResponseStatus(HttpStatus.OK)
    public Response updateProfile(@PathVariable("chatId") Long chatId, String key, String value) {
        service.updateUserKeyValue(chatId, key, value);
        return new Response(null, HttpStatus.OK.toString(), null, null);
    }

    @PutMapping(value = "/{chatId}/status")
    @ResponseStatus(HttpStatus.OK)
    public Response updateUserStatus(@PathVariable("chatId") Long chatId, String status) {
        Response response = new Response(status, HttpStatus.OK.toString(), null, null);
        service.updateUserStatus(chatId, status);
        return response;
    }

    @GetMapping(value = "/{chatId}/search")
    @ResponseStatus(HttpStatus.OK)
    public Response searchNextProfile(@PathVariable("chatId") Long chatId) {
        Long nextProfileId = service.findNextProfileByChatId(chatId);
        return new Response(null, HttpStatus.OK.toString(),
                service.findNameAndGender(nextProfileId), service.findProfileByteString(nextProfileId));
    }

    @GetMapping(value = "/{chatId}")
    @ResponseStatus(HttpStatus.OK)
    public Response findUserByChatId(@PathVariable("chatId") Long chatId) {
        Response response;
        if (service.isUserDoesNotExist(chatId)) {
            service.createUser(chatId, "new");
            service.createProfile(chatId);
            service.updateUserStatus(chatId, "new");
        }
        response = new Response(service.findUserByChatId(chatId).getStatus(),
                HttpStatus.OK.toString(), null, null);
        return response;
    }

    @ExceptionHandler(BusinessException.class)
    public Response handleException(BusinessException e) {
        return new Response(null, HttpStatus.NOT_ACCEPTABLE.toString(),
                e.getMessage(), null);
    }

    @ExceptionHandler(ServiceException.class)
    public Response handleException(ServiceException e) {
        return new Response(null, HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                e.getMessage(), null);
    }
}
