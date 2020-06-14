package org.vladkanash.slack.service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.vladkanash.util.Config.CONFIG;

public class SlackService {

    public static void uploadImage(BufferedImage image) {
        var token = CONFIG.get("slack.auth.token");
        var webhookUrl = CONFIG.get("slack.url.webhook");

        var inputStream = getStreamForImage(image);

        try {
            var uploadResponse = uploadImage(token, inputStream);
            var fileId = getFileId(uploadResponse, "id");
            var shareResponse = shareImage(token, fileId);
            var publicLink = getFileId(shareResponse, "permalink_public");
            var attachmentLink = getAttachmentLink(publicLink);
            var messageBody = getMessageBody(attachmentLink);
            var messageResponse = postMessage(webhookUrl, messageBody);

            System.out.println(messageResponse.getStatusText());

        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    private static HttpResponse<String> postMessage(String webhookUrl, String messageBody) throws UnirestException {
        return Unirest.post(webhookUrl)
                .body(messageBody)
                .asString();
    }

    private static String getMessageBody(String attachmentLink) {
        var messagePayload = CONFIG.get("slack.message.payload");
        var channelId = CONFIG.get("slack.channel.id");
        return String.format(messagePayload, channelId, attachmentLink);
    }

    private static String getAttachmentLink(String publicLink) {
        return publicLink.replaceFirst("https://slack-files.com/(.*?)-(.*?)-(.*?)",
                "https://slack-files.com/files-pri/$1-$2/time-report.png?pub_secret=$3");
    }

    private static HttpResponse<JsonNode> shareImage(String token, String fileId)
            throws UnirestException {
        var url = CONFIG.get("slack.url.file.share");

        return Unirest.post(url)
                .header("Authorization", "Bearer " + token)
                .field("file", fileId)
                .asJson();
    }

    private static HttpResponse<JsonNode> uploadImage(String token, ByteArrayInputStream inputStream)
            throws UnirestException {
        var url = CONFIG.get("slack.url.file.upload");

        return Unirest.post(url)
                .header("Authorization", "Bearer " + token)
                .field("file", inputStream, "time-report.png")
                .field("initial_comment", CONFIG.get("slack.image.comment"))
                .asJson();
    }

    private static String getFileId(HttpResponse<JsonNode> response, String id) {
        return response.getBody()
                .getObject()
                .getJSONObject("file")
                .getString(id);
    }

    private static ByteArrayInputStream getStreamForImage(BufferedImage pngImage) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(pngImage, "png", os);
            return new ByteArrayInputStream(os.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
