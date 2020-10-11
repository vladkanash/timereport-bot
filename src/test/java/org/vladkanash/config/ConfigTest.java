package org.vladkanash.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ConfigTest {

    public static final String TEST_PATH = "config.yml";
    public static final String TOKEN = "1111222233334444";

    private final static Map<String, String> USERS_MAP = Map.of(
            "user1", "id1",
            "user2", "id2",
            "user3", "id3");

    private final static Map<String, Object> CONFIG_MAP = Map.of(
            "jira",
            Map.of("auth",
                    Map.of("token", TOKEN,
                            "user", "test-user")),

            "test",
            Map.of("users", USERS_MAP)
    );

    private Cfg4jConfig testedInstance;

    @BeforeEach
    void setUp() {
        testedInstance = new Cfg4jConfig(TEST_PATH);
    }

    @Test
    void shouldReturnConfigProperty() {
        assertEquals(TOKEN, testedInstance.get("jira.auth.token"));
    }

    @Test
    void shouldThrowExceptionIfPropertyNotFound1() {
        assertThrows(NoSuchElementException.class,
                () -> testedInstance.get("jira.auth.invalid.property"));
    }

    @Test
    void shouldThrowExceptionIfPropertyNotFound2() {
        assertThrows(NoSuchElementException.class,
                () -> testedInstance.get("jira.auth.token.id"));
    }

    @Test
    void shouldThrowExceptionIfPropertyNotFound3() {
        assertThrows(NoSuchElementException.class,
                () -> testedInstance.get("invalid"));
    }

    @Test
    void shouldThrowExceptionIfPropertyNotFound4() {
        assertThrows(NoSuchElementException.class,
                () -> testedInstance.get("invalid."));
    }

    @Test
    void shouldThrowExceptionIfPropertyNotString() {
        assertThrows(NoSuchElementException.class,
                () -> testedInstance.get("jira.auth"));
    }

    @Test
    void shouldThrowExceptionIfKeyIsNull() {
        assertThrows(NullPointerException.class,
                () -> testedInstance.get(null));
    }
}