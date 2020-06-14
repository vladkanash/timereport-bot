package org.vladkanash.rendering.service;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.vladkanash.jira.entity.Worklog;
import org.vladkanash.rendering.converter.WorklogContextConverter;

import java.io.StringWriter;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

public class VelocityRenderingService {

    public static final String TEMPLATE_NAME = "reportTemplate.vm";

    public static Optional<String> renderHtml(Stream<Worklog> worklogs) {
        initVelocity();

        try {
            var template = Velocity.getTemplate(TEMPLATE_NAME);
            var writer = new StringWriter();
            var context = createContext(worklogs);

            template.merge(context, writer);

            System.out.println(writer.toString());

            return Optional.of(writer.toString());
        } catch (ResourceNotFoundException | MethodInvocationException | ParseErrorException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private static VelocityContext createContext(Stream<Worklog> worklogs) {
        VelocityContext context = new VelocityContext();
        context.put("context", WorklogContextConverter.convert(worklogs));
        return context;
    }

    private static void initVelocity() {
        Properties p = new Properties();
        p.setProperty("resource.loader", "class");
        p.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(p);
    }
}
