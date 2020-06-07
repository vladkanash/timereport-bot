package org.vladkanash.rendering.service;

import gui.ava.html.Html2Image;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.vladkanash.jira.entity.Worklog;
import org.vladkanash.rendering.converter.WorklogContextConverter;

import java.awt.image.BufferedImage;
import java.io.StringWriter;
import java.util.Properties;
import java.util.stream.Stream;

public class ImageRenderingService {

    public static final String TEMPLATE_NAME = "reportTemplate.vm";

    public BufferedImage renderWorklogReportImage(Stream<Worklog> worklogs) {
        initVelocity();

        try {
            Template template = Velocity.getTemplate(TEMPLATE_NAME);
            StringWriter sw = new StringWriter();
            var context = createContext(worklogs);
            template.merge(context, sw);
            var html = sw.toString();
            System.out.println(html);

            return createImage(html);
        } catch (ResourceNotFoundException | MethodInvocationException | ParseErrorException e) {
            e.printStackTrace();
            return null;
        }
    }

    private BufferedImage createImage(String html) {
        var imageRenderer = Html2Image.fromHtml(html).getImageRenderer();
        imageRenderer.setAutoHeight(true);
        return imageRenderer.getBufferedImage();
    }

    private VelocityContext createContext(Stream<Worklog> worklogs) {
        VelocityContext context = new VelocityContext();
        context.put("context", WorklogContextConverter.convert(worklogs));
        return context;
    }

    private void initVelocity() {
        Properties p = new Properties();
        p.setProperty("resource.loader", "class");
        p.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(p);
    }
}
