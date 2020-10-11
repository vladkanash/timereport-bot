package org.vladkanash.jira.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vladkanash.util.Config;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.Base64;
import java.util.Optional;

@Singleton
public class DefaultJiraRestApiService implements JiraRestApiService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Config config;
    private final HttpClient httpClient;

    @Inject
    public DefaultJiraRestApiService(Config config) {
        this.config = config;
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public Optional<String> getWorklogsForIssue(String issueCode) {
        var serverUri = config.get("jira.rest.server");
        var worklogEndpoint = config.get("jira.rest.endpoint.worklog");
        var worklogUrl = MessageFormat.format(worklogEndpoint, issueCode);

        var uri = URI.create(serverUri + worklogUrl);
        LOG.info("Sending request to {}", uri);

        var request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Content-Type", "application/json")
                .header("Authorization", basicAuth())
                .build();

        return performRequest(request);
    }

    @Override
    public Optional<String> searchQuery(String query) {
        var serverUri = config.get("jira.rest.server");
        var searchEndpoint = config.get("jira.rest.endpoint.search");

        var uri = URI.create(serverUri + searchEndpoint);
        LOG.info("Sending search query request to {}", uri);
        var request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(query))
                .uri(uri)
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
            LOG.error("An exception while performing http request to {}", request.uri(), e);
            return Optional.empty();
        }
    }

    private String basicAuth() {
        var user = config.get("jira.auth.user");
        var token = config.get("jira.auth.token");
        return "Basic " + Base64.getEncoder().encodeToString((user + ":" + token).getBytes());
    }
}
