package org.vladkanash.jira.service;

import org.vladkanash.util.Config;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.Base64;
import java.util.Optional;

public class JiraRestApiService {

    private final Config config;
    private final HttpClient httpClient;

    @Inject
    public JiraRestApiService(Config config) {
        this.config = config;
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public Optional<String> getWorklogsForIssue(String issueCode) {
        var serverUri = config.get("jira.server.uri");
        var worklogEndpoint = config.get("jira.rest.endpoint.worklog");
        var worklogUrl = MessageFormat.format(worklogEndpoint, issueCode);

        var request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(serverUri + worklogUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", basicAuth())
                .build();

        return performRequest(request);
    }

    public Optional<String> searchQuery(String query) {
        var serverUri = config.get("jira.server.uri");
        var searchEndpoint = config.get("jira.rest.endpoint.search");

        var request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(query))
                .uri(URI.create(serverUri + searchEndpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", basicAuth())
                .build();

        return performRequest(request);
    }

    private Optional<String> performRequest(HttpRequest request) {
        try {
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return Optional.of(response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private String basicAuth() {
        var user = config.get("jira.auth.user");
        var token = config.get("jira.auth.token");
        return "Basic " + Base64.getEncoder().encodeToString((user + ":" + token).getBytes());
    }
}
