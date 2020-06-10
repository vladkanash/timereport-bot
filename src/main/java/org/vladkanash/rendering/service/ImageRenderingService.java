package org.vladkanash.rendering.service;

import gui.ava.html.Html2Image;

import java.awt.image.BufferedImage;
import java.util.Optional;

public class ImageRenderingService {

    public static Optional<BufferedImage> renderImage(String html) {
        var imageRenderer = Html2Image.fromHtml(html).getImageRenderer();
        imageRenderer.setAutoHeight(true);
        imageRenderer.setWidth(800);
        return Optional.ofNullable(imageRenderer.getBufferedImage());
    }
}
