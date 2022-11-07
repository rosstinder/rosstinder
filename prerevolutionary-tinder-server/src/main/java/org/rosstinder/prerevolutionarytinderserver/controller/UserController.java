package org.rosstinder.prerevolutionarytinderserver.controller;

import org.rosstinder.prerevolutionarytinderserver.exception.BusinessException;
import org.rosstinder.prerevolutionarytinderserver.model.Gender;
import org.rosstinder.prerevolutionarytinderserver.model.Preference;
import org.rosstinder.prerevolutionarytinderserver.model.Response;
import org.rosstinder.prerevolutionarytinderserver.model.entity.Profile;
import org.rosstinder.prerevolutionarytinderserver.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;


@RestController
@RequestMapping("/users")
public class UserController {
    public final UserService service = new UserService();
    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    @GetMapping(value = "/{chatId}/status")
    @ResponseStatus(HttpStatus.OK)
    public Response getUserStatus(@PathVariable("chatId") Long chatId) {
        Response response;
        try {
            String status = service.findUserByChatId(chatId).getStatus();
            response = new Response(chatId, status, HttpStatus.OK.toString(), status);
        }
        catch (BusinessException e) {
            response = handleException(e, HttpStatus.NOT_FOUND.toString());
        }
        return response;
    }

    @GetMapping(value = "/{chatId}/profile")
    @ResponseStatus(HttpStatus.OK)
    public Response getProfilePictureUrl(@PathVariable("chatId") Long chatId, String status) {
        Response response;
        try {
            Profile profile = service.findProfileUrl(chatId);
            service.updateUserStatus(chatId, status);
            response = new Response(chatId, status, HttpStatus.OK.toString(), profile);
        } catch (BusinessException e) {
            response = handleException(e, HttpStatus.NOT_FOUND.toString());
        }
        return response;
    }

    @PutMapping(value = "/{chatId}")
    @ResponseStatus(HttpStatus.OK)
    public Response updateProfile(@PathVariable("chatId") Long chatId, String key, String value, String status) {
        Response response = null;
        switch (key) {
            case ("gender"):
                try {
                    service.updateGender(chatId, Gender.fromString(value));
                    response = new Response(chatId, status, HttpStatus.OK.toString(), null);
                } catch (BusinessException e) {
                    logger.error(e.getMessage());
                    response = handleException(e, HttpStatus.BAD_REQUEST.toString());
                } finally {
                    break;
                }
            case ("name"):
                service.updateName(chatId, value);
                response = new Response(chatId, status, HttpStatus.OK.toString(), null);
                break;
            case ("description"):
                service.updateDescription(chatId, value);
                response = new Response(chatId, status, HttpStatus.OK.toString(), null);
                break;
            case ("preference"):
                try {
                    service.updatePreference(chatId, Preference.fromString(value));
                    response = new Response(chatId, status, HttpStatus.OK.toString(), null);
                } catch (BusinessException e) {
                    logger.error(e.getMessage());
                    response = handleException(e, HttpStatus.BAD_REQUEST.toString());
                } finally {
                    break;
                }
            default:
                logger.info("Значения не были обновлены по причине некорректного значения key в полученном запросе");
                response = new Response(chatId, status, HttpStatus.NO_CONTENT.toString(), null);
                break;
        }
        try {
            service.updateUserStatus(chatId, status);
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
            response = new Response(chatId, status, HttpStatus.OK.toString(), null);
        } catch (BusinessException e) {
            response = handleException(e, HttpStatus.NOT_FOUND.toString());
        }
        return response;
    }

    //@PostMapping(value = "/{chatId}")
    //@ResponseStatus(HttpStatus.CREATED)
    //public void create(@PathVariable("chatId") Long chatId, String status) {
    //    service.createUser(chatId, status);
    //    service.createProfile(chatId);
    //    service.updateUserStatus(chatId, status);
    //}

    //@GetMapping(value = "/{chatId}/search/nextProfile")
    //@ResponseStatus(HttpStatus.OK)
    //public Long searchNextProfile(@PathVariable("chatId") Long chatId, String status) {
    //    Long profileChatId = service.findNextProfileChatId(chatId);
    //    try {
    //        service.updateUserStatus(chatId, status);
    //    }
    //    catch (NoSuchElementException e) {
//
    //    }
    //    return profileChatId;
    //}

    //удалить после тестирования
    //@GetMapping(value = "/{chatId}")
    //@ResponseStatus(HttpStatus.OK)
    //public String findUserByChatId(@PathVariable("chatId") Long chatId) {
    //    return service.findUserByChatId(chatId).toString();
    //}

    @ExceptionHandler(BusinessException.class)
    public Response handleException(BusinessException e, String httpStatus) {
        return new Response(null, null, httpStatus, e.getMessage());
    }
}
