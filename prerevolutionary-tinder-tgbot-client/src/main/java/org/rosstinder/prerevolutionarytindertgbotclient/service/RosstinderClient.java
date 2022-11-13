package org.rosstinder.prerevolutionarytindertgbotclient.service;

import org.rosstinder.prerevolutionarytindertgbotclient.model.ResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.LinkedHashMap;

@Component
public class RosstinderClient {
    RestTemplate restTemplate = new RestTemplate();
    @Value("${rosstinder.server}")
    private String server;

    public LinkedHashMap<String, Object> getUser(Long chatId) {
        URI uri = getUri(server + "/users/" + chatId);
        ResponseDto responseDto = restTemplate.getForObject(uri, ResponseDto.class);
        return (LinkedHashMap<String, Object>) responseDto.getAttachment();
    }

    public byte[] getImageProfile(Long chatId) {
        URI uri = getUri(server + "/users/" + chatId + "/profile");
        ResponseDto responseDto = restTemplate.getForObject(uri, ResponseDto.class);
        byte[] decoded = Base64.getDecoder().decode((String) responseDto.getAttachment2());
        return decoded;
    }

    public String getUserStatus(Long chatId) {
        URI uri = getUri(server + "/users/" + chatId);
        return restTemplate.getForObject(uri, ResponseDto.class).getUserStatus();
    }

    public String getGenderAndName(Long chatId) {
        URI uri = getUri(server + "/users/" + chatId + "/profile");
        ResponseDto responseDto = restTemplate.getForObject(uri, ResponseDto.class);
        return (String) responseDto.getAttachment();
    }

    public void setNewStatus(Long chatId, String status) {
        restTemplate.put(server + "/users/" + chatId + "/status" + "?status=" + status, Void.class);
    }

    public void setGender(Long chatId, String gender) {
        restTemplate.put(server + "/users/" + chatId + "?key=gender&value=" + gender, Void.class);
    }

    public void setName(Long chatId, String name) {
        restTemplate.put(server + "/users/" + chatId + "?key=name&value=" + name, Void.class);
    }

    public void setDescription(Long chatId, String description) {
        restTemplate.put(server + "/users/" + chatId + "?key=description&value=" + description, Void.class);
    }

    public void setPreference(Long chatId, String preference) {
        restTemplate.put(server + "/users/" + chatId + "?key=preference&value=" + preference, Void.class);
    }

    public LinkedHashMap<String, Object> getNextProfile(Long chatId) {
        LinkedHashMap<String, Object> nextProfile = new LinkedHashMap<>();
        URI uri = getUri(server + "/users/" + chatId + "/search/nextProfile");
        ResponseDto responseDto = restTemplate.getForObject(uri, ResponseDto.class);
        String nameAndGender = (String) responseDto.getAttachment();
        byte[] image = Base64.getDecoder().decode((String) responseDto.getAttachment2());
        nextProfile.put("nameAndGender", nameAndGender);
        nextProfile.put("image", image);
        return nextProfile;
    }

    public void setLikeOrDislike(Long chatId, String likeOrDislike) {
        restTemplate.postForLocation(server + "/favorites/" + chatId + "?isLike=" + likeOrDislike, Void.class);
    }

    public String getRelationship(Long chatId) {
        URI uri = getUri(server + "/favorites/" + chatId);
        ResponseDto responseDto = restTemplate.getForObject(uri, ResponseDto.class);
        return (String) responseDto.getAttachment();
    }

    public LinkedHashMap<String, Object> getNextFavorite(Long chatId) {
        LinkedHashMap<String, Object> nextFavorite = new LinkedHashMap<>();
        URI uri = getUri(server + "/favorites/" + chatId + "/nextFavorite");
        ResponseDto responseDto = restTemplate.getForObject(uri, ResponseDto.class);
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
