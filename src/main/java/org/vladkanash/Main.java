package org.vladkanash;

import org.vladkanash.jira.service.JiraWorklogService;
import org.vladkanash.jira.util.RequestUtils;
import org.vladkanash.rendering.service.ImageRenderingService;
import org.vladkanash.rendering.service.VelocityRenderingService;
import org.vladkanash.slack.service.SlackService;

public class Main {

    public static void main(String[] args) {
        var query = RequestUtils.getWorklogSearchQuery();

        var worklogs = JiraWorklogService.getUserWorklogs(query)
                .peek(System.out::println);

        VelocityRenderingService.renderHtml(worklogs)
                .flatMap(ImageRenderingService::renderImage)
                .ifPresent(SlackService::uploadImage);
    }
}
