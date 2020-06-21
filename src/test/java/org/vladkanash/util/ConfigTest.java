package org.vladkanash.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfigTest {

    private static final String TEST_KEY = "test.key";
    public static final String PROPERTIES = "properties";
    public static final String TEST_PATH = "config.properties";

    private Config testedInstance;

    @Mock
    private Properties properties;

    @BeforeEach
    void setUp() throws NoSuchFieldException {
        testedInstance = new Config(TEST_PATH);
        FieldSetter.setField(testedInstance,
                testedInstance.getClass().getDeclaredField(PROPERTIES), properties);
    }

    @Test
    void shouldReturnConfigProperty() {
        var value = "test-value";

        when(properties.getProperty(TEST_KEY)).thenReturn(value);

        assertEquals(value, testedInstance.get(TEST_KEY));
    }

    @Test
    void shouldReturnConfigListProperty() {
        var stringValue = "value1,value2,value3";
        var listValue = List.of("value1", "value2", "value3");

        when(properties.getProperty(TEST_KEY)).thenReturn(stringValue);

        assertEquals(listValue, testedInstance.getList(TEST_KEY));
    }
}