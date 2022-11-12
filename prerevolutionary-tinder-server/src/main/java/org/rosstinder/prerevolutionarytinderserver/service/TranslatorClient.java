package org.rosstinder.prerevolutionarytinderserver.service;

import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

public class TranslatorClient {
    RestTemplate restTemplate = new RestTemplate();

    public String translateDescription(String description) {
        URI uri = getUri(description);
        return uri.toASCIIString();
    }

    private static URI getUri(String description) {
        try {
            return new URI("http://localhost:5006/translate?resource=" + description);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
