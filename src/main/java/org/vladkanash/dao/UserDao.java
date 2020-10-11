package org.vladkanash.dao;

import java.util.List;

public interface UserDao {

    User getUserBySlackId(String slackId);

    List<User> getAllUsers();
}
