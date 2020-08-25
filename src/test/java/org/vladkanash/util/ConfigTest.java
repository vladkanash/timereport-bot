package org.vladkanash.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class ConfigTest {

    public static final String CONFIG = "config";
    public static final String TEST_PATH = "config.properties";
    public static final String TOKEN = "1234567890";

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

    private Config testedInstance;

    @BeforeEach
    void setUp() throws NoSuchFieldException {
        testedInstance = new Config(TEST_PATH);
        FieldSetter.setField(testedInstance,
                testedInstance.getClass().getDeclaredField(CONFIG), CONFIG_MAP);
    }

    @Test
    void shouldReturnConfigProperty() {
        assertEquals(TOKEN, testedInstance.get("jira.auth.token"));
    }

    @Test
    void shouldReturnNullIfPropertyNotFound1() {
        assertNull(testedInstance.get("jira.auth.invalid.property"));
    }

    @Test
    void shouldReturnNullIfPropertyNotFound2() {
        assertNull(testedInstance.get("jira.auth.token.id"));
    }

    @Test
    void shouldReturnNullIfPropertyNotFound3() {
        assertNull(testedInstance.get("invalid"));
    }

    @Test
    void shouldReturnNullIfPropertyNotFound4() {
        assertNull(testedInstance.get("invalid."));
    }

    @Test
    void shouldReturnNullIfPropertyNotString() {
        assertNull(testedInstance.get("jira.auth"));
    }

    @Test
    void shouldReturnNullIfKeyIsNull() {
        assertNull(testedInstance.get(null));
    }

    @Test
    void shouldReturnConfigMapProperty() {
        assertEquals(USERS_MAP, testedInstance.getMap("test.users"));
    }

    @Test
    void shouldReturnNullIfConfigMapPropertyNotFound() {
        assertNull(testedInstance.getMap("test.values"));
    }
}