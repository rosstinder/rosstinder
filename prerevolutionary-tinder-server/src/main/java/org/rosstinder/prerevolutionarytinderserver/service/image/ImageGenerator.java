package org.rosstinder.prerevolutionarytinderserver.service.image;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.rosstinder.prerevolutionarytinderserver.exception.ServiceException;
import org.rosstinder.prerevolutionarytinderserver.model.entity.Profile;
import org.rosstinder.prerevolutionarytinderserver.service.ImageGeneratorService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageGenerator implements ImageGeneratorService {

    @Value("${image-background.path}")
    private String BACKGROUND_PATH;

    private final AutoscalingFont autoscalingFont;
    private final CustomFont customFont;
    private final TextSplitter textSplitter;

    private final int FIRST_LINE = 0;
    private final int DEFAULT_TITLE_FONT_SIZE = 50;
    private final int DEFAULT_BODY_FONT_SIZE = 30;
    private final int LEFT_SHARE_BORDER = 10;
    private final int TOP_SHARE_BORDER = 7;
    private final int RIGHT_BORDER_MULTIPLIER = 2;
    private final float LINE_SPACING_MULTIPLIER = 1.5f;
    private final String FORMAT_NAME_FOR_IMAGE = "png";
    private final String CHARSET_FOR_ENCODING_NAME = "ISO-8859-2";


    public String getGeneratedImage(Profile profile) {
        try (InputStream input = this.getClass().getResourceAsStream(BACKGROUND_PATH)) {
            BufferedImage image = ImageIO.read(Objects.requireNonNull(input));
            Graphics layout = image.getGraphics();

            layout.setColor(Color.BLACK);

            int width = image.getWidth();
            int height = image.getHeight();

            int leftBorder = getLeftBorder(width);
            int topBorder = getTopBorder(height);
            int widthForText = getWidthForText(width, leftBorder);

            Font titleFont = autoscalingFont.scaleTitleFontByWidth(customFont.getTitleFont(DEFAULT_TITLE_FONT_SIZE));
            Font bodyFont = autoscalingFont.scaleBodyFontByWidth(customFont.getBodyFont(DEFAULT_BODY_FONT_SIZE), profile.getDescription(), widthForText);

            List<String> descriptionLines = textSplitter.splitTextByFontAndTextSpaceWidth(layout.getFontMetrics(titleFont),
                    layout.getFontMetrics(bodyFont),
                    profile.getDescription(),
                    widthForText);

            writeDescription(layout, descriptionLines, titleFont, bodyFont, leftBorder, topBorder);
            layout.dispose();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(image, FORMAT_NAME_FOR_IMAGE, byteArrayOutputStream);

            return new String(Base64.encodeBase64(byteArrayOutputStream.toByteArray()), CHARSET_FOR_ENCODING_NAME);
        } catch (IOException ioException) {
            log.error("Ошибка при чтении или записи анкеты chatId=" + profile.getChatId());
            throw new ServiceException("Ошибка при чтении или записи анкеты chatId=" + profile.getChatId());
        }
    }

    private int getWidthForText(int width, int leftBorder) {
        return width - leftBorder * RIGHT_BORDER_MULTIPLIER;
    }

    private int getLeftBorder(int width) {
        return width / LEFT_SHARE_BORDER;
    }

    private int getTopBorder(int height) {
        return height / TOP_SHARE_BORDER;
    }

    private void writeDescription(Graphics layout,
                                  List<String> descriptionLines,
                                  Font titleFont,
                                  Font bodyFont,
                                  int leftBorder,
                                  int topBorder) {
        layout.setFont(titleFont);
        if (descriptionLines.size() == 1) {
            String[] words = descriptionLines.get(0).split(" ");
            layout.drawString(words[0], leftBorder, topBorder);
            layout.setFont(bodyFont);
            String descriptionBody = joinListString(Arrays.stream(words)
                    .skip(1)
                    .toList());
            topBorder += bodyFont.getSize() * LINE_SPACING_MULTIPLIER;
            layout.drawString(descriptionBody, leftBorder, topBorder);
        } else {
            layout.drawString(descriptionLines.get(FIRST_LINE), leftBorder, topBorder);
            layout.setFont(bodyFont);
            for (int i = 1; i < descriptionLines.size(); i++) {
                topBorder += bodyFont.getSize() * LINE_SPACING_MULTIPLIER;
                layout.drawString(descriptionLines.get(i), leftBorder, topBorder);
            }
        }

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
