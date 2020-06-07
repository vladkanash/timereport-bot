package org.vladkanash.jira.service;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.BaseRequest;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.Optional;

import static org.vladkanash.util.Config.CONFIG;

public class JiraRestApiService {

    public static Optional<JSONObject> getWorklogsForIssue(String issueCode) {
        var token = CONFIG.get("jira.auth.token");
        var username = CONFIG.get("jira.auth.user");
        var serverUri = CONFIG.get("jira.server.uri");
        var worklogEndpoint = CONFIG.get("jira.rest.endpoint.worklog");
        var worklogUrl = MessageFormat.format(worklogEndpoint, issueCode);

        var request = Unirest.get(serverUri + worklogUrl)
                .basicAuth(username, token)
                .header("Content-Type", "application/json");

        return getJson(request);
    }

    public static Optional<JSONObject> searchQuery(String query) {
        var token = CONFIG.get("jira.auth.token");
        var username = CONFIG.get("jira.auth.user");
        var serverUri = CONFIG.get("jira.server.uri");
        var searchEndpoint = CONFIG.get("jira.rest.endpoint.search");

        var request = Unirest.post(serverUri + searchEndpoint)
                .basicAuth(username, token)
                .header("Content-Type", "application/json")
                .body(query);

        return getJson(request);
    }

    private static Optional<JSONObject> getJson(BaseRequest request) {
        try {
            return Optional.of(request.asJson().getBody().getObject());
        } catch (UnirestException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
