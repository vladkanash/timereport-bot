package org.vladkanash.dao;

import com.sybit.airtable.Query;
import com.sybit.airtable.Sort;

import java.util.List;

public class GetUsersBySlackIdQuery implements Query {

    private static final String SLACK_ID_QUERY = "{Slack ID} = \"%s\"";

    private final String slackId;

    public GetUsersBySlackIdQuery(String slackId) {
        this.slackId = slackId;
    }

    @Override
    public String filterByFormula() {
        return String.format(SLACK_ID_QUERY, slackId);
    }

    @Override
    public String[] getFields() {
        return new String[0];
    }

    @Override
    public Integer getPageSize() {
        return null;
    }

    @Override
    public Integer getMaxRecords() {
        return null;
    }

    @Override
    public String getView() {
        return null;
    }

    @Override
    public List<Sort> getSort() {
        return null;
    }

    @Override
    public String getOffset() {
        return null;
    }
}
