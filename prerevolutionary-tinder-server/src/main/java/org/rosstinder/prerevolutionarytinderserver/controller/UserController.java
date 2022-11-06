package org.rosstinder.prerevolutionarytinderserver.controller;

import org.rosstinder.prerevolutionarytinderserver.model.Gender;
import org.rosstinder.prerevolutionarytinderserver.model.Preference;
import org.rosstinder.prerevolutionarytinderserver.model.entity.User;
import org.rosstinder.prerevolutionarytinderserver.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    public final UserService service = new UserService();

    @GetMapping
    public List<User> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/{id}")
    public User findById(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public int create(@RequestBody User resource) {
        return service.create(resource);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable("id") Long id, @RequestBody User resource) {
        service.findById(resource.getChatId());
        service.update(resource);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") Long id) {
        service.deleteById(id);
    }



    @PutMapping(value = "/{id}/gender")
    @ResponseStatus(HttpStatus.OK)
    public void updateGender(@PathVariable("id") Long id, Gender gender) {
        service.updateGender(id, gender);
    }

    @PutMapping(value = "/{id}/description")
    @ResponseStatus(HttpStatus.OK)
    public void updateDescription(@PathVariable("id") Long id, String description) {
        service.updateDescription(id, description);
    }

    @PutMapping(value = "/{id}/preference")
    @ResponseStatus(HttpStatus.OK)
    public void updatePreference(@PathVariable("id") Long id, Preference preference) {
        service.updatePreference(id, preference);
    }

    @GetMapping(value = "/search")
    @ResponseStatus(HttpStatus.OK)
    public List<User> search(@PathVariable("id") Long id) {
        return service.search(id);
    }
}
