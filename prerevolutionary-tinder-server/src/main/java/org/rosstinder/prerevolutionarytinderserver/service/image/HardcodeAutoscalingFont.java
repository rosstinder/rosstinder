package org.rosstinder.prerevolutionarytinderserver.service.image;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.text.MessageFormat;
import java.util.Map;
import java.util.TreeMap;

@Component
public class HardcodeAutoscalingFont implements AutoscalingFont {

    @Value("#{${font-sizes-by-text-sizes}}")
    private TreeMap<Integer, Float> fontSizesByTextSizes;

    @Override
    public Font scaleBodyFontByWidth(Font font, String text, int textSpaceWidth) {
        for (Map.Entry<Integer, Float> entry : fontSizesByTextSizes.entrySet()) {
            if (text.length() < entry.getKey()) {
                return font.deriveFont(entry.getValue());
            }
        }
        throw new IllegalArgumentException(MessageFormat.format("В качестве аргумента передан слишком большой текст (максимум {} символов)", fontSizesByTextSizes.lastKey()));
    }

    @Override
    public Font scaleTitleFontByWidth(Font font) {
        return font.deriveFont(50f);
    }
}
