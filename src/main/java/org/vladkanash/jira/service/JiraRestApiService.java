package org.vladkanash.jira.service;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.BaseRequest;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.Optional;

public class JiraRestApiService {

    private final String token;
    private final String username;

    private final String serverUri;
    private final String searchEndpoint;
    private final String worklogEndpoint;

    public static Builder builder() {
        return new Builder();
    }

    private JiraRestApiService(String token,
                               String username,
                               String serverUri,
                               String searchEndpoint,
                               String worklogEndpoint) {
        this.token = token;
        this.username = username;
        this.serverUri = serverUri;
        this.searchEndpoint = searchEndpoint;
        this.worklogEndpoint = worklogEndpoint;
    }

    Optional<JSONObject> getWorklogsForIssue(String issueCode) {
        var worklogUri = MessageFormat.format(worklogEndpoint, issueCode);

        var request = Unirest.get(serverUri + worklogUri)
                .basicAuth(username, token)
                .header("Content-Type", "application/json");

        return getJson(request);
    }

    Optional<JSONObject> searchQuery(String query) {
        var request = Unirest.post(serverUri + searchEndpoint)
                .basicAuth(username, token)
                .header("Content-Type", "application/json")
                .body(query);

        return getJson(request);
    }

    private Optional<JSONObject> getJson(BaseRequest request) {
        try {
            return Optional.of(request.asJson().getBody().getObject());
        } catch (UnirestException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static class Builder {

        private String token;
        private String username;
        private String serverUri;
        private String searchEndpoint;
        private String worklogEndpoint;

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder serverUri(String serverUri) {
            this.serverUri = serverUri;
            return this;
        }

        public Builder searchEndpoint(String searchEndpoint) {
            this.searchEndpoint = searchEndpoint;
            return this;
        }

        public Builder worklogEndpoint(String worklogEndpoint) {
            this.worklogEndpoint = worklogEndpoint;
            return this;
        }

        public JiraRestApiService build() {
            return new JiraRestApiService(token, username, serverUri, searchEndpoint, worklogEndpoint);
        }
    }
}
