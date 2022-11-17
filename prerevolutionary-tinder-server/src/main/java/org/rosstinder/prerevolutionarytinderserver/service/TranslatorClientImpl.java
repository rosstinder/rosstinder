package org.rosstinder.prerevolutionarytinderserver.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TranslatorClientImpl {
    @Value("${translator.server}")
    private String server;
    RestTemplate restTemplate = new RestTemplate();

    public String translateDescription(String description) {
        return restTemplate.getForObject(server + "/translate?resource=" + description, String.class);
    }
}
