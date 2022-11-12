package org.rosstinder.prerevolutionarytindertgbotclient.service;

import org.rosstinder.prerevolutionarytindertgbotclient.model.ResponseDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
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
        return (byte[]) responseDto.getAttachment();
    }

    public String getUserStatus(Long chatId) {
        URI uri = getUri("http://localhost:8080/users/" + chatId);
        return restTemplate.getForObject(uri, ResponseDto.class).getUserStatus();
    }

    private URI getUri(String url) {
        try {
            return new URI(url);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
