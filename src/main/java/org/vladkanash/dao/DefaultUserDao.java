package org.vladkanash.dao;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class DefaultUserDao implements UserDao {

    @Inject
    public DefaultUserDao() {
    }

    @Override
    public User getUserBySlackId(String slackId) {
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        return null;
    }
}
