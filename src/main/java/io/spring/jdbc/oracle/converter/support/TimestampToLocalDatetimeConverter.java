package io.spring.jdbc.oracle.converter.support;

import io.spring.jdbc.oracle.converter.OracleConverter;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

final class TimestampToLocalDatetimeConverter implements OracleConverter<Timestamp, LocalDateTime> {

    @Override
    public LocalDateTime convert(Timestamp source) {

        return Optional.ofNullable(source)
            .map(Timestamp::toLocalDateTime)
            .orElse(null);
    }

}