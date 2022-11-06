//package org.rosstinder.prerevolutionarytinderserver.service;
//
//import lombok.NonNull;
//import org.rosstinder.prerevolutionarytinderserver.model.entity.Like;
//import org.rosstinder.prerevolutionarytinderserver.model.repository.LikeRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//@Component
//public class LikeService {
//    private final boolean LIKE = true;
//    private final boolean DISLIKE = false;
//    private final Logger logger = LoggerFactory.getLogger(LikeService.class);
//    private final LikeRepository likeRepository;
//
//    public LikeService(LikeRepository likeRepository) {
//        this.likeRepository = likeRepository;
//    }
//
//    public UUID like(UUID who, UUID whom) {
//        logger.debug("Starting method <like>...");
//        Like like = withLike(who, whom); //может вернуться NullPointerException, если who или whom null
//        if (likeRepository.isUserMatchesWithAnotherUser(who, whom)) {
//            //"Вы любимы"
//        } else {
//            //"ОК"
//        }
//        //void вероятно убрать
//        logger.debug("Ending <like> method...");
//        return like.getId();
//    }
//
//    public UUID dislike(UUID who, UUID whom) {
//        logger.debug("Starting method <dislike>...");
//        Like dislike = withDislike(who, whom); //может вернуться NullPointerException, если who или whom null
//        //void вероятно убрать
//        logger.debug("Ending <dislike> method...");
//        return dislike.getId();
//    }
//
//    public boolean isLikeOrDislikeExist(UUID who, UUID whom) {
//        logger.debug("Starting method <isLikeOrDislikeExist>...");
//        if (likeRepository.isUserLikedAnotherUser(who, whom)) {
//            logger.debug("Ending <isLikeOrDislikeExist> method...");
//            return true;
//        } else {
//            logger.debug("Ending <isLikeOrDislikeExist> method...");
//            return false;
//        }
//    }
//
//    public List<UUID> getAllUserMatches(UUID who) {
//        return likeRepository.getAllUserLikes(who).stream()
//                .filter(like -> likeRepository.isUserMatchesWithAnotherUser(like.getWhom(), who))
//                .map(Like::getWhom)
//                .collect(Collectors.toList());
//    }
//
//    public List<UUID> getAllUserLikes(UUID who) {
//        return likeRepository.getAllUserLikes(who).stream()
//                .map(Like::getWhom)
//                .collect(Collectors.toList());
//    }
//
//    public List<UUID> getUsersWhoLikesUser(UUID whom) {
//        return likeRepository.getAllLikes().stream()
//                .filter(like -> likeRepository.isUserLikedAnotherUser(like.getWho(), whom))
//                .map(Like::getWho)
//                .collect(Collectors.toList());
//    }
//
//    @NonNull
//    private Like withLike(UUID who, UUID whom) {
//        Like like = new Like(who, whom);
//        like.setLike(LIKE);
//        return like;
//    }
//    @NonNull
//    private Like withDislike(UUID who, UUID whom) {
//        Like dislike = new Like(who, whom);
//        dislike.setLike(DISLIKE);
//        return dislike;
//    }
//}
