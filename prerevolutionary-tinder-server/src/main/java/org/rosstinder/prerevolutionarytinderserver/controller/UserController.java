package org.rosstinder.prerevolutionarytinderserver.controller;

import lombok.AllArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.rosstinder.prerevolutionarytinderserver.exception.BusinessException;
import org.rosstinder.prerevolutionarytinderserver.exception.ServiceException;
import org.rosstinder.prerevolutionarytinderserver.model.Response;
import org.rosstinder.prerevolutionarytinderserver.service.TranslatorClient;
import org.rosstinder.prerevolutionarytinderserver.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;


@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    public final UserService service;

    public final TranslatorClient translatorClient;

    @GetMapping(value = "/{chatId}/status")
    @ResponseStatus(HttpStatus.OK)
    public Response getUserStatus(@PathVariable("chatId") Long chatId) {
        Response response;
        try {
            String status = service.findUserByChatId(chatId).getStatus();
            response = new Response(chatId, status, HttpStatus.OK.toString(), status, null);
        }
        catch (BusinessException e) {
            response = handleException(e, HttpStatus.NOT_FOUND.toString());
        }
        return response;
    }

    @GetMapping(value = "/{chatId}/profile")
    @ResponseStatus(HttpStatus.OK)
    public Response getProfilePictureUrl(@PathVariable("chatId") Long chatId) {
        Response response;
        try {
            Long id = service.findProfileIdByChatId(chatId);
            response = new Response(chatId, "", HttpStatus.OK.toString(),
                    service.findNameAndGender(id), service.findProfileUrl(id));
        } catch (BusinessException e) {
            response = handleException(e, HttpStatus.NOT_FOUND.toString());
        } catch (ServiceException e) {
            response = handleException(e, HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }
        return response;
    }

    @PutMapping(value = "/{chatId}")
    @ResponseStatus(HttpStatus.OK)
    public Response updateProfile(@PathVariable("chatId") Long chatId, String key, String value) {
        Response response;
        boolean isIncorrectValue = false;
        switch (key) {
            case ("gender") -> {
                try {
                    service.updateGender(chatId, value);
                    response = new Response(chatId, "", HttpStatus.OK.toString(), value, null);
                } catch (BusinessException e) {
                    response = handleException(e, HttpStatus.BAD_REQUEST.toString());
                }
            }
            case ("name") -> {
                value = translatorClient.translateDescription(value);
                service.updateName(chatId, value);
                response = new Response(chatId, "", HttpStatus.OK.toString(), value, null);
            }
            case ("description") -> {
                value = translatorClient.translateDescription(value);
                service.updateDescription(chatId, value);
                response = new Response(chatId, "", HttpStatus.OK.toString(), value, null);
            }
            case ("preference") -> {
                try {
                    service.updatePreference(chatId, value);
                    response = new Response(chatId, "", HttpStatus.OK.toString(), value, null);
                } catch (BusinessException e) {
                    response = handleException(e, HttpStatus.BAD_REQUEST.toString());
                }
            }
            default -> {
                isIncorrectValue = service.incorrectKey(chatId);
                response = new Response(chatId, "", HttpStatus.NO_CONTENT.toString(), null, null);
            }
        }
        try {
            if (isIncorrectValue) {
                service.updateUserStatus(chatId, "");
            }
        } catch (BusinessException e) {
            response = handleException(e, HttpStatus.NOT_FOUND.toString());
        }
        return response;
    }

    @PutMapping(value = "/{chatId}/status")
    @ResponseStatus(HttpStatus.OK)
    public Response updateUserStatus(@PathVariable("chatId") Long chatId, String status) {
        Response response;
        try {
            service.updateUserStatus(chatId, status);
            response = new Response(chatId, status, HttpStatus.OK.toString(), null, null);
        } catch (BusinessException e) {
            response = handleException(e, HttpStatus.NOT_FOUND.toString());
        }
        return response;
    }

    @GetMapping(value = "/{chatId}/search/nextProfile")
    @ResponseStatus(HttpStatus.OK)
    public Response searchNextProfile(@PathVariable("chatId") Long chatId) {
        Response response;
        try {
            Long nextProfileId = service.findNextProfileByChatId(chatId);
            String uri = service.findProfileUrl(nextProfileId);
            String profileDescription = service.findNameAndGender(nextProfileId);
            response = new Response(chatId, "", HttpStatus.OK.toString(),
                    profileDescription, uri);
        }
        catch (BusinessException e) {
            response = handleException(e, HttpStatus.NOT_FOUND.toString());
        } catch (ServiceException e) {
            response = handleException(e, HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }
        return response;
    }

    @GetMapping(value = "/{chatId}")
    @ResponseStatus(HttpStatus.OK)
    public Response findUserByChatId(@PathVariable("chatId") Long chatId) {
        Response response;
        try {
            if (!service.isUserDoesNotExist(chatId)) {
                response = new Response(chatId, service.findUserByChatId(chatId).getStatus(),
                        HttpStatus.OK.toString(), service.findUserByChatId(chatId), null);
            } else {
                service.createUser(chatId, "new");
                service.createProfile(chatId);
                service.updateUserStatus(chatId, "new");
                response = new Response(chatId, service.findUserByChatId(chatId).getStatus(), HttpStatus.OK.toString(), service.findUserByChatId(chatId), null);
            }

        } catch (BusinessException e) {
            response = handleException(e, HttpStatus.NOT_FOUND.toString());
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
