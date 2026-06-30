package com.yilijishu.utils.jackson;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class LocalDateTimeMapper {

    public String asString(Date date) {
        return date != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(date) : null;
    }

    @SneakyThrows
    public Date asDate(String date) {
        return date != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .parse(date) : null;
    }

    public String asString(LocalDateTime date) {
        return date != null ? date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null;
    }


    public LocalDateTime asLocalDateTime(String date) {
        return StringUtils.isNotBlank(date) ?
                LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null;
    }


}
