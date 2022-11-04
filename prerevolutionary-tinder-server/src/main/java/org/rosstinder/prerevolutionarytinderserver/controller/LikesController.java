package org.rosstinder.prerevolutionarytinderserver.controller;

import org.rosstinder.prerevolutionarytinderserver.service.LikeService;
import org.rosstinder.prerevolutionarytinderserver.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/likes")
public class LikesController {
    private final Logger logger = LoggerFactory.getLogger(LikesController.class);
    private final LikeService likeService;
    private final UserService userService;

    public LikesController(LikeService likeService, UserService userService) {
        this.likeService = likeService;
        this.userService = userService;
    }

    @PostMapping("/new_like")
    public UUID createLike(@RequestBody UUID who, UUID whom) {
        logger.debug("Creating {} user's like {} user", who.toString(), whom.toString());
        return likeService.like(who, whom);
    }

    @PostMapping("/new_dislike")
    public UUID createDislike(@RequestBody UUID who, UUID whom) {
        logger.debug("Creating {} user's like {} user", who.toString(), whom.toString());
        return likeService.dislike(who, whom);
    }

    @GetMapping("/matches")
    public List<UUID> getAllMatches(@RequestBody UUID who) {
        logger.debug("Getting {} user's matches", who.toString());
        return likeService.getAllUserLikes(who);
    }

    @GetMapping("/likes_you")
    public List<UUID> getUsersWhoLikesYou(@RequestBody UUID whom) {
        logger.debug("Getting users who likes {} user", whom.toString());
        return likeService.getUsersWhoLikesUser(whom);
    }

    @GetMapping("/you_like")
    public List<UUID> getUsersYouLike(@RequestBody UUID who) {
        logger.debug("Getting users who {} user like", who.toString());
        return likeService.getAllUserLikes(who);
    }
}
