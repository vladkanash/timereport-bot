package org.vladkanash.gson.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

    private final DateTimeFormatter isoFormatter = new DateTimeFormatterBuilder()
            .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            .optionalStart().appendOffset("+HHMM", "+0000").optionalEnd()
            .toFormatter();

    @Override
    public void write(final JsonWriter jsonWriter, final LocalDateTime localDate) throws IOException {
        jsonWriter.value(localDate.toString());
    }

    @Override
    public LocalDateTime read(final JsonReader jsonReader) throws IOException {
        return LocalDateTime.parse(jsonReader.nextString(), isoFormatter);
    }
}
