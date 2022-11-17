package org.rosstinder.prerevolutionarytinderserver.service.image;

import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class DescriptionSplitter implements TextSplitter {

    @Override
    public List<String> splitTextByFontAndTextSpaceWidth(FontMetrics titleFontMetrics,
                                                         FontMetrics bodyFontMetrics,
                                                         String description,
                                                         int widthForText) {
        String[] wordsInDescription = description.split("\\s");

        List<String> descriptionLines = new ArrayList<>();
        List<String> line = new ArrayList<>();
        int numberLines = 0;

        for (String word : wordsInDescription) {
            if (numberLines == 0 && titleFontMetrics.stringWidth(joinListString(line) + " " + word) < widthForText) {
                line.add(word);
            } else if (numberLines > 0 && bodyFontMetrics.stringWidth(joinListString(line) + " " + word) < widthForText) {
                line.add(word);
            } else {
                numberLines++;
                descriptionLines.add(joinListString(line));
                line = new ArrayList<>();
                line.add(word);
            }
        }
        if (!line.isEmpty()) {
            descriptionLines.add(joinListString(line));
        }
        return descriptionLines;
    }

    private String joinListString(List<String> words) {
        StringBuilder text = new StringBuilder();
        if (words.size() != 0) {
            for (String word : words) {
                text.append(word).append(" ");
            }
            text.deleteCharAt(text.length() - 1);
        }
        return text.toString();
    }
}
