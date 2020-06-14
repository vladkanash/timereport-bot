package org.vladkanash.rendering.service;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.vladkanash.jira.entity.Worklog;
import org.vladkanash.rendering.converter.WorklogContextConverter;

import javax.inject.Inject;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

public class VelocityService {

    private static final String TEMPLATE_NAME = "reportTemplate.vm";
    private static final String CONTEXT = "context";

    private final WorklogContextConverter contextConverter;

    @Inject
    public VelocityService(WorklogContextConverter contextConverter) {
        this.contextConverter = contextConverter;

        Properties p = new Properties();
        p.setProperty("resource.loader", "class");
        p.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(p);
    }

    public Optional<String> renderHtml(Stream<Worklog> worklogs, LocalDate startDate, LocalDate endDate) {
        try {
            var template = Velocity.getTemplate(TEMPLATE_NAME);
            var writer = new StringWriter();
            var context = createContext(worklogs, startDate, endDate);

            template.merge(context, writer);

            return Optional.of(writer.toString());
        } catch (ResourceNotFoundException | MethodInvocationException | ParseErrorException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private VelocityContext createContext(Stream<Worklog> worklogs, LocalDate startDate, LocalDate endDate) {
        VelocityContext context = new VelocityContext();
        context.put(CONTEXT, contextConverter.convert(worklogs, startDate, endDate));
        return context;
    }
}
