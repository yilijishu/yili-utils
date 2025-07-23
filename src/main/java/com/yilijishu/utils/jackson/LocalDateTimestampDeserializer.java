package com.yilijishu.utils.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
public class LocalDateTimestampDeserializer extends JsonDeserializer {

    private LocalDateTimeDeserializer localDateTimeDeserializer;

    public LocalDateTimestampDeserializer(DateTimeFormatter formatter) {
        localDateTimeDeserializer = new LocalDateTimeDeserializer(formatter);
    }

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String str = parser.getValueAsString();
        if (StringUtils.isNotBlank(str) && str.length() == 13) { //添加long转date支持
            try {
                Timestamp timestamp = new Timestamp(Long.parseLong(str));
                LocalDateTime localDateTime = LocalDateTime.ofInstant(timestamp.toInstant(), ZoneId.systemDefault());
                return localDateTime;
            } catch (NumberFormatException e) {
                log.error("非long 类型转LocalDateTime失败 ", e);
            }
        }
        return localDateTimeDeserializer.deserialize(parser, context);
    }

}
