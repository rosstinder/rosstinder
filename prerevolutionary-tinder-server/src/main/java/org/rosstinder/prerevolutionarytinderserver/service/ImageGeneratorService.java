package org.rosstinder.prerevolutionarytinderserver.service;

import org.rosstinder.prerevolutionarytinderserver.model.entity.Profile;

public interface ImageGeneratorService {
    String getGeneratedImage(Profile profile);
}
