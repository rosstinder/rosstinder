package org.rosstinder.prerevolutionarytinderserver.service.image;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

@Slf4j
@Component
public class OldStandardAutoscalingFont implements CustomFont {

    @Value("${old-standard-bold-font.path}")
    private String FONTS_OLD_STANDARD_BOLD_TTF_PATH;
    @Value("${old-standard-regular-font.path}")
    private String FONTS_OLD_STANDARD_REGULAR_TTF_PATH;

    @Override
    public Font getTitleFont(int titleFontSize) {
        try (InputStream inputStream = OldStandardAutoscalingFont.class.getResourceAsStream(FONTS_OLD_STANDARD_BOLD_TTF_PATH)) {
            return getFont(titleFontSize, inputStream);
        } catch (FontFormatException | IOException e) {
            String message = "Ошибка при попытке получить TitleFont";
            throw new RuntimeException(message, e);
        }
    }

    @Override
    public Font getBodyFont(int bodyFontSize) {
        try (InputStream inputStream = OldStandardAutoscalingFont.class.getResourceAsStream(FONTS_OLD_STANDARD_REGULAR_TTF_PATH)) {
            return getFont(bodyFontSize, inputStream);
        } catch (FontFormatException | IOException e) {
            String message = "Ошибка при попытке получить BodyFont";
            throw new RuntimeException(message, e);
        }
    }

    private Font getFont(int titleFontSize, InputStream inputStream) throws FontFormatException, IOException {
        if (inputStream != null) {
            Font oldStandardBold  = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            return oldStandardBold.deriveFont(Font.PLAIN, titleFontSize);
        } else {
            throw new IOException(MessageFormat.format("InputStream для {0} is null", FONTS_OLD_STANDARD_BOLD_TTF_PATH));
        }
    }
}
