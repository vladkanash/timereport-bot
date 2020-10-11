package org.vladkanash.slack.service;

import java.awt.image.BufferedImage;

public interface SlackService {

    void sendReport(BufferedImage image);

    String generateReportMessage(BufferedImage image);

    void postMessage(String url, String messageBody);
}
