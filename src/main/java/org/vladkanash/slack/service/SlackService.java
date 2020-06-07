package org.vladkanash.slack.service;

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
        var url = CONFIG.get("slack.url.file.upload");
        var shareUrl = CONFIG.get("slack.url.file.share");
        var channel = CONFIG.get("slack.channel.id");
        var messageUrl = CONFIG.get("slack.url.webhook");

        var inputStream = getStreamForImage(image);

        try {
            var response = Unirest.post(url)
                    .header("Authorization", "Bearer " + token)
                    .field("file", inputStream, "time-report.png")
                    .field("initial_comment", CONFIG.get("slack.image.comment"))
                    .asJson();

            System.out.println(response.getBody().toString());

            var id = response.getBody()
                    .getObject()
                    .getJSONObject("file")
                    .getString("id");

            System.out.println(id);

            var shareResponse = Unirest.post(shareUrl)
                    .header("Authorization", "Bearer " + token)
                    .field("file", id)
                    .asJson();

            System.out.println(shareResponse.getBody().toString());

            var publicImageLink = shareResponse.getBody()
                    .getObject()
                    .getJSONObject("file")
                    .getString("permalink_public");

            var attachmentLink = publicImageLink.replaceFirst("https://slack-files.com/(.*?)-(.*?)-(.*?)",
                    "https://slack-files.com/files-pri/$1-$2/time-report.png?pub_secret=$3");

            System.out.println("Attachment Link: " + attachmentLink);

            var messageBody = String.format(CONFIG.get("slack.message.payload"), channel, attachmentLink);

            System.out.println("Message Body: " + messageBody);

            var messageResponse = Unirest.post(messageUrl)
                    .body(messageBody)
                    .asString();

            System.out.println(messageResponse.getStatusText());

        } catch (UnirestException e) {
            e.printStackTrace();
        }
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
