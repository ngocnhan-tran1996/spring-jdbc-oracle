package io.spring.jdbc.oracle.converter.support;

import io.spring.jdbc.oracle.converter.OracleConverter;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

final class LocalDatetimeToTimestampConverter implements OracleConverter<LocalDateTime, Timestamp> {

    @Override
    public Timestamp convert(LocalDateTime source) {

        return Optional.ofNullable(source)
            .map(Timestamp::valueOf)
            .orElse(null);
    }

}