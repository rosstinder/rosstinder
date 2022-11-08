package org.rosstinder.prerevolutionarytinderserver.controller;

import org.rosstinder.prerevolutionarytinderserver.exception.BusinessException;
import org.rosstinder.prerevolutionarytinderserver.model.Response;
import org.rosstinder.prerevolutionarytinderserver.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/users")
public class UserController {
    public final UserService service = new UserService();
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
            response = new Response(chatId, status, HttpStatus.OK.toString(), service.findProfileUrl(chatId));
            service.updateUserStatus(chatId, status);
        } catch (BusinessException e) {
            response = handleException(e, HttpStatus.NOT_FOUND.toString());
        }
        return response; //озвращать не profile, а урл на картинку
    }

    @PutMapping(value = "/{chatId}")
    @ResponseStatus(HttpStatus.OK)
    public Response updateProfile(@PathVariable("chatId") Long chatId, String key, String value, String status) {
        Response response = null;
        boolean isIncorrectValue = false;
        switch (key) {
            case ("gender"):
                try {
                    service.updateGender(chatId, value);
                    response = new Response(chatId, status, HttpStatus.OK.toString(), null);
                } catch (BusinessException e) {
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
                    service.updatePreference(chatId, value);
                    response = new Response(chatId, status, HttpStatus.OK.toString(), null);
                } catch (BusinessException e) {
                    response = handleException(e, HttpStatus.BAD_REQUEST.toString());
                } finally {
                    break;
                }
            default:
                isIncorrectValue = service.incorrectKey(chatId);
                response = new Response(chatId, status, HttpStatus.NO_CONTENT.toString(), null);
                break;
        }
        try {
            if (isIncorrectValue) {
                service.updateUserStatus(chatId, status);
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
            response = new Response(chatId, status, HttpStatus.OK.toString(), null);
        } catch (BusinessException e) {
            response = handleException(e, HttpStatus.NOT_FOUND.toString());
        }
        return response;
    }

    @PostMapping(value = "/{chatId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Response create(@PathVariable("chatId") Long chatId, String status) {
        Response response;
        try {
            service.createUser(chatId, status);
            service.createProfile(chatId);
            service.updateUserStatus(chatId, status);
            response = new Response(chatId, status, HttpStatus.OK.toString(), null);
        } catch (BusinessException e) {
            response = handleException(e, HttpStatus.NOT_ACCEPTABLE.toString());
        }
        return response;
    }

    @GetMapping(value = "/{chatId}/search/nextProfile")
    @ResponseStatus(HttpStatus.OK)
    public Response searchNextProfile(@PathVariable("chatId") Long chatId, String status) {
        Response response;
        try {
            Long nextProfileChatId = service.findNextProfileChatId(chatId);
            service.updateUserStatus(chatId, status);
            response = new Response(chatId, status, HttpStatus.OK.toString(), nextProfileChatId);
        }
        catch (BusinessException e) {
            response = handleException(e, HttpStatus.NOT_FOUND.toString());
        }
        return response;    //возвращать не nextProfileChatId, а url на картинку
    }

    //удалить после тестирования
    @GetMapping(value = "/{chatId}")
    @ResponseStatus(HttpStatus.OK)
    public Response findUserByChatId(@PathVariable("chatId") Long chatId) {
        Response response;
        try {
            response = new Response(chatId, service.findUserByChatId(chatId).getStatus(),
                    HttpStatus.OK.toString(), service.findUserByChatId(chatId));
        } catch (BusinessException e) {
            response = handleException(e, HttpStatus.NOT_FOUND.toString());
        }
        return response;
    }

    @ExceptionHandler(BusinessException.class)
    public Response handleException(BusinessException e, String httpStatus) {
        return new Response(null, null, httpStatus, e.getMessage());
    }
}
