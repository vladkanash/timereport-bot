package org.vladkanash.rendering.service;

import gui.ava.html.Html2Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.lang.invoke.MethodHandles;
import java.util.Optional;

@Singleton
public class HtmlRenderingService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Inject
    public HtmlRenderingService() {}

    public Optional<BufferedImage> renderImage(String html) {
        var imageRenderer = Html2Image.fromHtml(html).getImageRenderer();
        imageRenderer.setAutoHeight(true);
        imageRenderer.setWidth(800);
        LOG.info("HTML converted to image");
        return Optional.ofNullable(imageRenderer.getBufferedImage());
    }
}
