package org.vladkanash.slack.service;

import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.files.FilesSharedPublicURLRequest;
import com.slack.api.methods.request.files.FilesUploadRequest;
import org.vladkanash.util.Config;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SlackService {

    public static final String IMAGE_NAME = "time-report.png";

    private final Config config;

    @Inject
    public SlackService(Config config) {
        this.config = config;
    }

    public void sendReport(BufferedImage image) {
        var webhookUrl = config.get("slack.url.webhook");
        var report = generateReportMessage(image);
        postMessage(webhookUrl, report);
    }

    public String generateReportMessage(BufferedImage image) {
        try {
            var fileId = uploadImage(image);
            var publicLink = shareImage(fileId);
            return getMessageBody(publicLink);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void postMessage(String url, String messageBody) {
        try {
            Slack.getInstance().send(url, messageBody);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String uploadImage(BufferedImage image)
            throws IOException, SlackApiException {

        var imageBytes = getBytesForImage(image);

        var request = FilesUploadRequest.builder()
                .fileData(imageBytes)
                .filename(IMAGE_NAME)
                .initialComment(config.get("slack.image.comment"))
                .token(config.get("slack.auth.token"))
                .build();

        return Slack.getInstance().methods().filesUpload(request).getFile().getId();
    }

    private String shareImage(String fileId)
            throws IOException, SlackApiException {

        var request = FilesSharedPublicURLRequest.builder()
                .file(fileId)
                .token(config.get("slack.auth.token"))
                .build();

        return Slack.getInstance().methods().filesSharedPublicURL(request)
                .getFile()
                .getPermalinkPublic();
    }

    private byte[] getBytesForImage(BufferedImage pngImage) {
        try {
            var outputStream = new ByteArrayOutputStream();
            ImageIO.write(pngImage, "png", outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getMessageBody(String publicLink) {
        var messagePayload = config.get("slack.message.payload");
        var channelId = config.get("slack.channel.id");
        var attachmentLink = getAttachmentLink(publicLink);
        return String.format(messagePayload, channelId, attachmentLink);
    }

    private String getAttachmentLink(String publicLink) {
        var regex = config.get("slack.image.publicUrl.regex");
        var templateUrl = config.get("slack.image.publicUrl.template");
        return publicLink.replaceFirst(regex, templateUrl);
    }
}
