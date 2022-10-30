package org.rosstinder.prerevolutionarytinderserver.controller;

import org.rosstinder.prerevolutionarytinderserver.model.User;
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
    public User findById(@PathVariable("id") int id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public int create(@RequestBody User resource) {
        return service.create(resource);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable("id") int id, @RequestBody User resource) {
        service.findById(resource.getChatId());
        service.update(resource);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") int id) {
        service.deleteById(id);
    }
}
