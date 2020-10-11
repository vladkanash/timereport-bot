package org.vladkanash.jira.gson.adapter;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocalDateTimeAdapterTest {

    private static final String DATE_STRING = "2020-06-20T22:33:44.707";
    private static final String OFFSET = "+0200";
    private static final LocalDateTime DATE = LocalDateTime.of(
            2020, Month.JUNE, 20,
            22, 33, 44,
            707_000_000);

    private final LocalDateTimeAdapter testedInstance = new LocalDateTimeAdapter();

    @Mock
    private JsonReader jsonReader;
    @Mock
    private JsonWriter jsonWriter;

    @Test
    void shouldReadValue() throws IOException {
        when(jsonReader.nextString()).thenReturn(DATE_STRING + OFFSET);

        var actual = testedInstance.read(jsonReader);

        assertEquals(DATE, actual);
    }

    @Test
    void shouldReadValueWithoutOffset() throws IOException {
        when(jsonReader.nextString()).thenReturn(DATE_STRING);

        var actual = testedInstance.read(jsonReader);

        assertEquals(DATE, actual);
    }

    @Test
    void shouldWriteValue() throws IOException {
        testedInstance.write(jsonWriter, DATE);

        verify(jsonWriter, atLeastOnce()).value(DATE_STRING);
    }
}