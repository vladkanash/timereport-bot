package org.vladkanash.dao;

import com.sybit.airtable.Airtable;
import com.sybit.airtable.Table;
import com.sybit.airtable.exception.AirtableException;
import org.apache.http.client.HttpResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vladkanash.config.Config;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Singleton
public class AirtableUserDao implements UserDao {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Config config;
    private final Airtable airtable;

    @Inject
    public AirtableUserDao(Config config) {
        this.config = config;
        this.airtable = getAirtable();
    }

    private Airtable getAirtable() {
        try {
            var apiKey = config.get("airtable.apikey");
            return new Airtable().configure(apiKey);
        } catch (AirtableException e) {
            throw new IllegalStateException("Can't configure airtable", e);
        }
    }

    @Override
    public Optional<User> getUserBySlackId(String slackId) {
        var query = new GetUsersBySlackIdQuery(slackId);
        return getUser(query);
    }

    private Optional<User> getUser(GetUsersBySlackIdQuery query) {
        try {
            var usersTable = getUsersTable();
            return usersTable.select(query).stream().findFirst();
        } catch (AirtableException e) {
            LOG.error("Error while trying to get user from airtable", e);
            return Optional.empty();
        }
    }

    @Override
    public List<User> getAllUsers() {
        try {
            return getUsersTable().select();
        } catch (AirtableException | HttpResponseException e) {
            LOG.error("Error while trying to get all users from airtable");
            return Collections.emptyList();
        }
    }

    @SuppressWarnings("unchecked")
    private Table<User> getUsersTable() throws AirtableException {
        var baseId = config.get("airtable.base");
        var tableName = config.get("airtable.table");
        return airtable.base(baseId).table(tableName, User.class);
    }
}
