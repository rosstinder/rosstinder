package org.rosstinder.prerevolutionarytindertgbotclient.service;

import lombok.extern.slf4j.Slf4j;
import org.rosstinder.prerevolutionarytindertgbotclient.model.BotState;
import org.rosstinder.prerevolutionarytindertgbotclient.model.ProfileDto;
import org.rosstinder.prerevolutionarytindertgbotclient.model.ResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.Objects;

@Slf4j
@Component
public class RosstinderClientImpl implements RosstinderClient {
    RestTemplate restTemplate = new RestTemplate();

    @Value("${rosstinder.server}")
    private String server;

    @Override
    public String getUserStatus(Long chatId) {
        URI uri = getUri(server + "/users/" + chatId);

        log.debug(MessageFormat.format("Отправлен GET-запрос: {0}", uri.toString()));

        return Objects.requireNonNull(restTemplate.getForObject(uri, ResponseDto.class)).getUserStatus();
    }

    @Override
    public ProfileDto getProfile(Long chatId) {
        URI uri = getUri(server + "/users/" + chatId + "/profile");
        ResponseDto responseDto = restTemplate.getForObject(uri, ResponseDto.class);

        log.debug(MessageFormat.format("Отправлен GET-запрос: {0}", uri.toString()));

        return new ProfileDto(Objects.requireNonNull(responseDto).getMessage(), Base64.getDecoder().decode(responseDto.getImage()));
    }

    @Override
    public ProfileDto getNextProfile(Long chatId) {
        URI uri = getUri(server + "/users/" + chatId + "/search");
        ResponseDto responseDto = restTemplate.getForObject(uri, ResponseDto.class);

        log.debug(MessageFormat.format("Отправлен GET-запрос: {0}", uri.toString()));

        return new ProfileDto(Objects.requireNonNull(responseDto).getMessage(), Base64.getDecoder().decode(responseDto.getImage()));
    }

    @Override
    public ProfileDto getNextFavorite(Long chatId) {
        URI uri = getUri(server + "/favorites/" + chatId + "/next");
        ResponseDto responseDto = restTemplate.getForObject(uri, ResponseDto.class);

        log.debug(MessageFormat.format("Отправлен GET-запрос: {0}", uri.toString()));

        return new ProfileDto(Objects.requireNonNull(responseDto).getMessage(), Base64.getDecoder().decode(responseDto.getImage()));
    }

    @Override
    public ProfileDto getPreviousFavorite(Long chatId) {
        URI uri = getUri(server + "/favorites/" + chatId + "/previous");
        ResponseDto responseDto = restTemplate.getForObject(uri, ResponseDto.class);

        log.debug(MessageFormat.format("Отправлен GET-запрос: {0}", uri.toString()));

        return new ProfileDto(Objects.requireNonNull(responseDto).getMessage(), Base64.getDecoder().decode(responseDto.getImage()));
    }

    @Override
    public String getRelationship(Long chatId) {
        URI uri = getUri(server + "/favorites/" + chatId);
        ResponseDto responseDto = restTemplate.getForObject(uri, ResponseDto.class);

        log.debug(MessageFormat.format("Отправлен GET-запрос: {0}", uri.toString()));

        return Objects.requireNonNull(responseDto).getMessage();
    }

    @Override
    public void setNewStatus(Long chatId, BotState status) {
        String url = server + "/users/" + chatId + "/status" + "?status=" + status.getStatus();
        restTemplate.put(url, Void.class);

        log.debug(MessageFormat.format("Отправлен PUT-запрос: {0}", url));
    }

    @Override
    public void setGender(Long chatId, String gender) {
        String url = server + "/users/" + chatId + "?key=gender&value=" + gender;
        restTemplate.put(url, Void.class);

        log.debug(MessageFormat.format("Отправлен PUT-запрос: {0}", url));
    }

    @Override
    public void setName(Long chatId, String name) {
        String url = server + "/users/" + chatId + "?key=name&value=" + name;
        restTemplate.put(url, Void.class);

        log.debug(MessageFormat.format("Отправлен PUT-запрос: {0}", url));

    }

    @Override
    public void setDescription(Long chatId, String description) {
        String url = server + "/users/" + chatId + "?key=description&value=" + description;
        restTemplate.put(url, Void.class);

        log.debug(MessageFormat.format("Отправлен PUT-запрос: {0}", url));
    }

    @Override
    public void setPreference(Long chatId, String preference) {
        String url = server + "/users/" + chatId + "?key=preference&value=" + preference;
        restTemplate.put(url, Void.class);

        log.debug(MessageFormat.format("Отправлен PUT-запрос: {0}", url));
    }

    @Override
    public void setLikeOrDislike(Long chatId, String likeOrDislike) {
        String url = server + "/favorites/" + chatId + "?isLike=" + likeOrDislike;
        restTemplate.postForLocation(url, Void.class);

        log.debug(MessageFormat.format("Отправлен PUT-запрос: {0}", url));
    }

    @Override
    public URI getUri(String url) {
        try {
            return new URI(url);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
