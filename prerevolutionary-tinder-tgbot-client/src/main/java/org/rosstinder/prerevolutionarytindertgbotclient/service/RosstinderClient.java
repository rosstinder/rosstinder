package org.rosstinder.prerevolutionarytindertgbotclient.service;

import org.rosstinder.prerevolutionarytindertgbotclient.model.BotState;
import org.rosstinder.prerevolutionarytindertgbotclient.model.ProfileDto;

import java.net.URI;

public interface RosstinderClient {

    String getUserStatus(Long chatId);

    ProfileDto getProfile(Long chatId);

    ProfileDto getNextProfile(Long chatId);

    ProfileDto getNextFavorite(Long chatId);

    ProfileDto getPreviousFavorite(Long chatId);

    String getRelationship(Long chatId);

    void setNewStatus(Long chatId, BotState status);

    void setGender(Long chatId, String gender);

    void setName(Long chatId, String name);

    void setDescription(Long chatId, String description);

    void setPreference(Long chatId, String preference);

    void setLikeOrDislike(Long chatId, String likeOrDislike);

    URI getUri(String url);
}
