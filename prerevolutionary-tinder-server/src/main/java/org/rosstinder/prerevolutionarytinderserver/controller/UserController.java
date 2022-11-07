package org.rosstinder.prerevolutionarytinderserver.controller;

import org.rosstinder.prerevolutionarytinderserver.model.Gender;
import org.rosstinder.prerevolutionarytinderserver.model.Preference;
import org.rosstinder.prerevolutionarytinderserver.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;


@RestController
@RequestMapping("/users")
public class UserController {
    public final UserService service = new UserService();
    @GetMapping(value = "/{chatId}/status")
    @ResponseStatus(HttpStatus.OK)
    public String getUserStatus(@PathVariable("chatId") Long chatId) {
        //nosuchelementexception
        return service.findUserByChatId(chatId).getStatus();
    }

    @GetMapping(value = "/{chatId}/profile")
    @ResponseStatus(HttpStatus.OK)
    public String getProfilePictureUrl(@PathVariable("chatId") Long chatId) {
        return service.findProfileUrl(chatId);
    }

    @PutMapping(value = "/{chatId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateProfile(@PathVariable("chatId") Long chatId, String key, String value) {
        switch (key) {
            case ("gender"):
                service.updateGender(chatId, Gender.fromString(value));
                break;
            case ("name"):
                service.updateName(chatId, value);
                break;
            case ("description"):
                service.updateDescription(chatId, value);
                break;
            case ("preference"):
                service.updatePreference(chatId, Preference.fromString(value));
                break;
            default:
                //исключение неправильный аргумент
                break;
        }
    }

    @PutMapping(value = "/{chatId}/status")
    @ResponseStatus(HttpStatus.OK)
    public void updateUserStatus(@PathVariable("chatId") Long chatId, String status) {
        service.updateUserStatus(chatId, status);
    }

    @PostMapping(value = "/{chatId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable("chatId") Long chatId, String status) {
        service.createUser(chatId, status);
        service.createProfile(chatId);
        service.updateUserStatus(chatId, status);
    }

    @GetMapping(value = "/{chatId}/search/nextProfile")
    @ResponseStatus(HttpStatus.OK)
    public Long searchNextProfile(@PathVariable("chatId") Long chatId, String status) {
        Long profileChatId = service.findNextProfileChatId(chatId);
        try {
            service.updateUserStatus(chatId, status);
        }
        catch (NoSuchElementException e) {

        }
        return profileChatId;
    }
}
