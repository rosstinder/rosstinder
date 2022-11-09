package org.rosstinder.prerevolutionarytinderserver.service;


import org.rosstinder.prerevolutionarytinderserver.model.entity.Profile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ImageGenerator {

    public static final String BACKGROUND = "/img/prerev-background.jpg";
    private static final int TITLE_FONT_SIZE = 50;
    public static final int BODY_FONT_SIZE = 30;
    public static final double MULTIPLIER = 1.5;
    public Font TITLE_FONT;
    public Font BODY_FONT;

    {
        try {
            InputStream inputStream = ImageGenerator.class.getResourceAsStream("/fonts/OldStandard-Bold.ttf");
            Font oldStandardBold = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            TITLE_FONT = oldStandardBold.deriveFont(Font.PLAIN, TITLE_FONT_SIZE);
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    {
        try {
            InputStream inputStream = ImageGenerator.class.getResourceAsStream("/fonts/OldStandard-Regular.ttf");
            Font oldStandardBold = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            BODY_FONT = oldStandardBold.deriveFont(Font.PLAIN, BODY_FONT_SIZE);
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ByteArrayOutputStream getGeneratedImage(Profile profile) throws IOException, FontFormatException {
        try (InputStream input = this.getClass().getResourceAsStream(BACKGROUND)) {
            BufferedImage image = ImageIO.read(Objects.requireNonNull(input));
            Graphics layout = image.getGraphics();
            layout.setColor(Color.BLACK);
            int width = image.getWidth();
            int height = image.getHeight();
            int leftBorder = width / 10;
            int topBorder = height / 7;
            List<String> descriptionLines = splitDescription(layout.getFontMetrics(TITLE_FONT),
                    layout.getFontMetrics(BODY_FONT),
                    profile.getDescription(),
                    width - leftBorder * 2);
            writeDescription(layout, descriptionLines, leftBorder, topBorder);
            layout.dispose();
            ImageIO.write(image, "png", new File("example.png"));
            return (ByteArrayOutputStream) ImageIO.createImageOutputStream(image);
        } catch (IOException ioException) {
            // todo: заменить логированием
            throw new RuntimeException(ioException);
        }
    }

    private void writeDescription(Graphics layout,
                                  List<String> descriptionLines,
                                  int leftBorder,
                                  int topBorder) {
        layout.setFont(TITLE_FONT);
        if (descriptionLines.size() == 1) {
            String[] words = descriptionLines.get(0).split(" ");
            layout.drawString(words[0], leftBorder, topBorder);
            layout.setFont(BODY_FONT);
            String descriptionBody = joinListString(Arrays.stream(words)
                    .skip(1)
                    .toList());
            topBorder += BODY_FONT_SIZE * MULTIPLIER;
            layout.drawString(descriptionBody, leftBorder, topBorder);
        } else {
            layout.drawString(descriptionLines.get(0), leftBorder, topBorder);
            layout.setFont(BODY_FONT);
            for (int i = 1; i < descriptionLines.size(); i++) {
                topBorder += BODY_FONT_SIZE * MULTIPLIER;
                layout.drawString(descriptionLines.get(i), leftBorder, topBorder);
            }
        }

    }

    private List<String> splitDescription(FontMetrics titleFontMetrics,
                                          FontMetrics bodyFontMetrics,
                                          String description,
                                          int widthForText)
    {
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
