package org.rosstinder.prerevolutionarytinderserver.service.image;

import java.awt.*;

public interface AutoscalingFont {
    Font scaleTitleFontByWidth(Font font);

    Font scaleBodyFontByWidth(Font font, String text, int textSpaceWidth);
}
