package com.yilijishu.utils.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimestampSer extends JsonSerializer<LocalDateTime> {

    private LocalDateTimeSerializer localDateTimeSerializer;

    public LocalDateTimestampSer(DateTimeFormatter formatter) {
        localDateTimeSerializer = new LocalDateTimeSerializer(formatter);
    }

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        localDateTimeSerializer.serialize(value, gen, serializers);

    }
}
