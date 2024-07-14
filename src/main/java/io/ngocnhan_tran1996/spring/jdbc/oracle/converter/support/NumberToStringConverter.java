package io.ngocnhan_tran1996.spring.jdbc.oracle.converter.support;

import io.ngocnhan_tran1996.spring.jdbc.oracle.converter.OracleConverter;
import java.util.Optional;

class NumberToStringConverter implements OracleConverter<Number, String> {

    @Override
    public String convert(Number source) {

        return Optional.ofNullable(source)
            .map(Number::toString)
            .orElse(null);
    }

}