package org.rosstinder.prerevolutionarytindertgbotclient.service;

import org.rosstinder.prerevolutionarytindertgbotclient.model.ResponseDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.LinkedHashMap;

@Component
public class RosstinderClient {
    RestTemplate restTemplate = new RestTemplate();

    public LinkedHashMap<String, Object> getUser(Long chatId) {
        URI uri = getUri("http://localhost:8080/users/" + chatId);
        ResponseDto responseDto = restTemplate.getForObject(uri, ResponseDto.class);
        return (LinkedHashMap<String, Object>) responseDto.getAttachment();
    }

    public byte[] getImageProfile(Long chatId) {
        URI uri = getUri("http://localhost:8080/users/" + chatId + "/profile");
        ResponseDto responseDto = restTemplate.getForObject(uri, ResponseDto.class);
        byte[] decoded = Base64.getDecoder().decode((String) responseDto.getAttachment2());
        return decoded;
    }

    public String getUserStatus(Long chatId) {
        URI uri = getUri("http://localhost:8080/users/" + chatId);
        return restTemplate.getForObject(uri, ResponseDto.class).getUserStatus();
    }

    public String getGenderAndName(Long chatId) {
        URI uri = getUri("http://localhost:8080/users/" + chatId + "/profile");
        ResponseDto responseDto = restTemplate.getForObject(uri, ResponseDto.class);
        return (String) responseDto.getAttachment();
    }

    public void setNewStatus(Long chatId, String status) {
        restTemplate.put("http://localhost:8080/users/" + chatId + "/status" + "?status=" + status, Void.class);
    }

    public void setGender(Long chatId, String gender) {
        restTemplate.put("http://localhost:8080/users/" + chatId + "?key=gender&value=" + gender, Void.class);
    }

    public void setName(Long chatId, String name) {
        restTemplate.put("http://localhost:8080/users/" + chatId + "?key=name&value=" + name, Void.class);
    }

    public void setDescription(Long chatId, String description) {
        restTemplate.put("http://localhost:8080/users/" + chatId + "?key=description&value=" + description, Void.class);
    }

    public void setPreference(Long chatId, String preference) {
        restTemplate.put("http://localhost:8080/users/" + chatId + "?key=preference&value=" + preference, Void.class);
    }

    public byte[] getImageNextProfile(Long chatId) {
        URI uri = getUri("http://localhost:8080/users/" + chatId + "/search/nextProfile");
        ResponseDto responseDto = restTemplate.getForObject(uri, ResponseDto.class);
        byte[] decoded = Base64.getDecoder().decode((String) responseDto.getAttachment2());
        return decoded;
    }

    public String getNameAndGenderForNextProfile(Long chatId) {
        URI uri = getUri("http://localhost:8080/users/" + chatId + "/search/nextProfile");
        ResponseDto responseDto = restTemplate.getForObject(uri, ResponseDto.class);
        return (String) responseDto.getAttachment();
    }

    public void setLikeOrDislike(Long chatId, String likeOrDislike) {
        restTemplate.postForLocation("http://localhost:8080/favorites/" + chatId + "?isLike=" + likeOrDislike, Void.class);
    }

    public byte[] getImageNextFavorite(Long chatId) {
        URI uri = getUri("http://localhost:8080/favorites/" + chatId + "/nextFavorite");
        ResponseDto responseDto = restTemplate.getForObject(uri, ResponseDto.class);
        byte[] decoded = Base64.getDecoder().decode((String) responseDto.getAttachment2());
        return decoded;
    }

    public String getNameAndGenderAndStatusForNextFavorite(Long chatId) {
        URI uri = getUri("http://localhost:8080/favorites/" + chatId + "/nextFavorite");
        ResponseDto responseDto = restTemplate.getForObject(uri, ResponseDto.class);
        return (String) responseDto.getAttachment();
    }

    public byte[] getImagePreviousFavorite(Long chatId) {
        URI uri = getUri("http://localhost:8080/favorites/" + chatId + "/previousFavorite");
        ResponseDto responseDto = restTemplate.getForObject(uri, ResponseDto.class);
        byte[] decoded = Base64.getDecoder().decode((String) responseDto.getAttachment2());
        return decoded;
    }

    public String getNameAndGenderAndStatusForPreviousFavorite(Long chatId) {
        URI uri = getUri("http://localhost:8080/favorites/" + chatId + "/previousFavorite");
        ResponseDto responseDto = restTemplate.getForObject(uri, ResponseDto.class);
        return (String) responseDto.getAttachment();
    }

    private URI getUri(String url) {
        try {
            return new URI(url);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
