package org.vladkanash.rendering.service;

import gui.ava.html.Html2Image;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.util.Optional;

@Singleton
public class HtmlRenderingService {

    @Inject
    public HtmlRenderingService() {}

    public Optional<BufferedImage> renderImage(String html) {
        var imageRenderer = Html2Image.fromHtml(html).getImageRenderer();
        imageRenderer.setAutoHeight(true);
        imageRenderer.setWidth(800);
        return Optional.ofNullable(imageRenderer.getBufferedImage());
    }
}
