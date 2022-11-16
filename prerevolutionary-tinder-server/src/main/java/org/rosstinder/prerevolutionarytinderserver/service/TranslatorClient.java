package org.rosstinder.prerevolutionarytinderserver.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Service
public class TranslatorClient {
    RestTemplate restTemplate = new RestTemplate();

    //todo: в ресурсы
    public String translateDescription(String description) {
        return restTemplate.getForObject("http://localhost:5006/translate?resource=" + description, String.class);
    }
}
