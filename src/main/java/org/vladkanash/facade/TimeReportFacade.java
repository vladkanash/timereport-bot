package org.vladkanash.facade;

import org.vladkanash.jira.service.JiraWorklogService;
import org.vladkanash.rendering.service.HtmlRenderingService;
import org.vladkanash.rendering.service.VelocityService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

@Singleton
public class TimeReportFacade {

    private final JiraWorklogService worklogService;
    private final VelocityService velocityService;
    private final HtmlRenderingService imageService;

    @Inject
    public TimeReportFacade(JiraWorklogService worklogService,
                            VelocityService velocityService,
                            HtmlRenderingService imageService) {
        this.worklogService = worklogService;
        this.velocityService = velocityService;
        this.imageService = imageService;
    }

    public Optional<BufferedImage> getReport(Set<String> userIds,
                                             LocalDate startDate,
                                             LocalDate endDate) {

        var worklogs = worklogService.getUserWorklogs(userIds, startDate, endDate);

        return velocityService.renderHtml(worklogs, startDate, endDate)
                .flatMap(imageService::renderImage);
    }
}
