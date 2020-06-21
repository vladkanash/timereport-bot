package org.vladkanash.slack.facade;

import org.vladkanash.jira.service.JiraWorklogService;
import org.vladkanash.rendering.service.HtmlRenderingService;
import org.vladkanash.rendering.service.VelocityService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Singleton
public class ReportGenerationFacade {

    private final JiraWorklogService worklogService;
    private final VelocityService velocityService;
    private final HtmlRenderingService imageService;

    @Inject
    public ReportGenerationFacade(JiraWorklogService worklogService,
                                  VelocityService velocityService,
                                  HtmlRenderingService imageService) {
        this.worklogService = worklogService;
        this.velocityService = velocityService;
        this.imageService = imageService;
    }

    public Optional<BufferedImage> getReport(List<String> userIds,
                                             LocalDate startDate,
                                             LocalDate endDate) {

        var worklogs = worklogService.getUserWorklogs(userIds, startDate, endDate);

        return velocityService.renderHtml(worklogs, startDate, endDate)
                .flatMap(imageService::renderImage);
    }
}
