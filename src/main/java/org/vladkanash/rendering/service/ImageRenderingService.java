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

import java.io.StringWriter;
import java.util.Properties;
import java.util.stream.Stream;

public class ImageRenderingService {

    public static final String TEMPLATE_NAME = "reportTemplate.vm";

    public void renderWorklogReportImage(Stream<Worklog> worklogs) {

        initVelocity();

        VelocityContext context = new VelocityContext();
        context.put("context", WorklogContextConverter.convert(worklogs));

        try {
            Template template = Velocity.getTemplate(TEMPLATE_NAME);
            StringWriter sw = new StringWriter();
            template.merge(context, sw);

            var html = sw.toString();
            System.out.println(html);

            var imageRenderer = Html2Image.fromHtml(html).getImageRenderer();
            imageRenderer.setAutoHeight(true);
            imageRenderer.saveImage("hello-world.png");

        } catch (ResourceNotFoundException | MethodInvocationException | ParseErrorException e) {
            e.printStackTrace();
        }
    }

    private void initVelocity() {
        Properties p = new Properties();
        p.setProperty("resource.loader", "class");
        p.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(p);
    }
}
