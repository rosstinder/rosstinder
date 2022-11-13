package org.rosstinder.prerevolutionarytindertgbotclient.service;

import lombok.extern.slf4j.Slf4j;
import org.rosstinder.prerevolutionarytindertgbotclient.model.ResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.LinkedHashMap;

@Slf4j
@Component
public class RosstinderClient {
    RestTemplate restTemplate = new RestTemplate();
    @Value("${rosstinder.server}")
    private String server;

    public byte[] getImageProfile(Long chatId) {
        URI uri = getUri(server + "/users/" + chatId + "/profile");
        ResponseDto responseDto = restTemplate.getForObject(uri, ResponseDto.class);
        log.debug(MessageFormat.format("Отправлен GET-запрос: {0}", uri.toString()));
        byte[] decoded = Base64.getDecoder().decode((String) responseDto.getAttachment2());
        return decoded;
    }

    public String getUserStatus(Long chatId) {
        URI uri = getUri(server + "/users/" + chatId);
        log.debug(MessageFormat.format("Отправлен GET-запрос: {0}", uri.toString()));
        return restTemplate.getForObject(uri, ResponseDto.class).getUserStatus();
    }

    public String getGenderAndName(Long chatId) {
        URI uri = getUri(server + "/users/" + chatId + "/profile");
        ResponseDto responseDto = restTemplate.getForObject(uri, ResponseDto.class);
        log.debug(MessageFormat.format("Отправлен GET-запрос: {0}", uri.toString()));
        return (String) responseDto.getAttachment();
    }

    public void setNewStatus(Long chatId, String status) {
        String url = server + "/users/" + chatId + "/status" + "?status=" + status;
        restTemplate.put(url, Void.class);
        log.debug(MessageFormat.format("Отправлен PUT-запрос: {0}", url));
    }

    public void setGender(Long chatId, String gender) {
        String url = server + "/users/" + chatId + "?key=gender&value=" + gender;
        restTemplate.put(url, Void.class);
        log.debug(MessageFormat.format("Отправлен PUT-запрос: {0}", url));
    }

    public void setName(Long chatId, String name) {
        String url = server + "/users/" + chatId + "?key=name&value=" + name;
        restTemplate.put(url, Void.class);
        log.debug(MessageFormat.format("Отправлен PUT-запрос: {0}", url));

    }

    public void setDescription(Long chatId, String description) {
        String url = server + "/users/" + chatId + "?key=description&value=" + description;
        restTemplate.put(url, Void.class);
        log.debug(MessageFormat.format("Отправлен PUT-запрос: {0}", url));
    }

    public void setPreference(Long chatId, String preference) {
        String url = server + "/users/" + chatId + "?key=preference&value=" + preference;
        restTemplate.put(url, Void.class);
        log.debug(MessageFormat.format("Отправлен PUT-запрос: {0}", url));
    }

    public LinkedHashMap<String, Object> getNextProfile(Long chatId) {
        LinkedHashMap<String, Object> nextProfile = new LinkedHashMap<>();
        URI uri = getUri(server + "/users/" + chatId + "/search/nextProfile");
        ResponseDto responseDto = restTemplate.getForObject(uri, ResponseDto.class);
        log.debug(MessageFormat.format("Отправлен GET-запрос: {0}", uri.toString()));
        String nameAndGender = (String) responseDto.getAttachment();
        byte[] image = Base64.getDecoder().decode((String) responseDto.getAttachment2());
        nextProfile.put("nameAndGender", nameAndGender);
        nextProfile.put("image", image);
        return nextProfile;
    }

    public void setLikeOrDislike(Long chatId, String likeOrDislike) {
        String url = server + "/favorites/" + chatId + "?isLike=" + likeOrDislike;
        restTemplate.postForLocation(url, Void.class);
        log.debug(MessageFormat.format("Отправлен PUT-запрос: {0}", url));
    }

    public String getRelationship(Long chatId) {
        URI uri = getUri(server + "/favorites/" + chatId);
        ResponseDto responseDto = restTemplate.getForObject(uri, ResponseDto.class);
        log.debug(MessageFormat.format("Отправлен GET-запрос: {0}", uri.toString()));
        return (String) responseDto.getAttachment();
    }

    public LinkedHashMap<String, Object> getNextFavorite(Long chatId) {
        LinkedHashMap<String, Object> nextFavorite = new LinkedHashMap<>();
        URI uri = getUri(server + "/favorites/" + chatId + "/nextFavorite");
        ResponseDto responseDto = restTemplate.getForObject(uri, ResponseDto.class);
        log.debug(MessageFormat.format("Отправлен GET-запрос: {0}", uri.toString()));
        String nameAndGender = (String) responseDto.getAttachment();
        byte[] image = Base64.getDecoder().decode((String) responseDto.getAttachment2());
        nextFavorite.put("nameAndGender", nameAndGender);
        nextFavorite.put("image", image);
        return nextFavorite;
    }

    public LinkedHashMap<String, Object> getPreviousFavorite(Long chatId) {
        LinkedHashMap<String, Object> previousFavorite = new LinkedHashMap<>();
        URI uri = getUri(server + "/favorites/" + chatId + "/previousFavorite");
        ResponseDto responseDto = restTemplate.getForObject(uri, ResponseDto.class);
        log.debug(MessageFormat.format("Отправлен GET-запрос: {0}", uri.toString()));
        String nameAndGender = (String) responseDto.getAttachment();
        byte[] image = Base64.getDecoder().decode((String) responseDto.getAttachment2());
        previousFavorite.put("nameAndGender", nameAndGender);
        previousFavorite.put("image", image);
        return previousFavorite;
    }

    private URI getUri(String url) {
        try {
            return new URI(url);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
