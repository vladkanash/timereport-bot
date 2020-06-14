package org.vladkanash.slack.service;

import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.files.FilesSharedPublicURLRequest;
import com.slack.api.methods.request.files.FilesUploadRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.vladkanash.util.Config.CONFIG;

public class SlackService {

    public static void sendReport(BufferedImage image) {
        var webhookUrl = CONFIG.get("slack.url.webhook");
        var report = generateReport(image);

        try {
            postMessage(webhookUrl, report);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String generateReport(BufferedImage image) {
        try {
            var fileId = uploadImage(image);
            var publicLink = shareImage(fileId);
            return getMessageBody(publicLink);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String uploadImage(BufferedImage image)
            throws IOException, SlackApiException {

        var imageBytes = getBytesForImage(image);

        var request = FilesUploadRequest.builder()
                .fileData(imageBytes)
                .filename("time-report.png")
                .initialComment(CONFIG.get("slack.image.comment"))
                .token(CONFIG.get("slack.auth.token"))
                .build();

        return Slack.getInstance().methods().filesUpload(request).getFile().getId();
    }

    private static String shareImage(String fileId)
            throws IOException, SlackApiException {

        var request = FilesSharedPublicURLRequest.builder()
                .file(fileId)
                .token(CONFIG.get("slack.auth.token"))
                .build();

        return Slack.getInstance().methods().filesSharedPublicURL(request)
                .getFile()
                .getPermalinkPublic();
    }

    public static void postMessage(String webhookUrl, String messageBody)
            throws IOException {
        Slack.getInstance().send(webhookUrl, messageBody);
    }

    private static byte[] getBytesForImage(BufferedImage pngImage) {
        try {
            var outputStream = new ByteArrayOutputStream();
            ImageIO.write(pngImage, "png", outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getMessageBody(String publicLink) {
        var messagePayload = CONFIG.get("slack.message.payload");
        var channelId = CONFIG.get("slack.channel.id");
        var attachmentLink = getAttachmentLink(publicLink);
        return String.format(messagePayload, channelId, attachmentLink);
    }

    private static String getAttachmentLink(String publicLink) {
        var regex = CONFIG.get("slack.image.publicUrl.regex");
        var templateUrl = CONFIG.get("slack.image.publicUrl.template");
        return publicLink.replaceFirst(regex, templateUrl);
    }
}
