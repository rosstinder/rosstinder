package org.rosstinder.prerevolutionarytinderserver.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TranslatorClientImpl implements TranslatorClientService {
    RestTemplate restTemplate = new RestTemplate();


    //todo: в ресурсы
    @Override
    public String translateDescription(String description) {
        return restTemplate.getForObject("http://localhost:5006/translate?resource=" + description, String.class);
    }
}
