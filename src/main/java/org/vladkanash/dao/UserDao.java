package org.vladkanash.dao;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    Optional<User> getUserBySlackId(String slackId);

    List<User> getAllUsers();
}
