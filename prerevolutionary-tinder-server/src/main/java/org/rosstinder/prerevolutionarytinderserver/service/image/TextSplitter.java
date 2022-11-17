package org.rosstinder.prerevolutionarytinderserver.service.image;

import java.awt.*;
import java.util.List;

public interface TextSplitter {
    List<String> splitTextByFontAndTextSpaceWidth(FontMetrics titleFontMetrics,
                                                  FontMetrics bodyFontMetrics,
                                                  String text,
                                                  int widthForText);
}
