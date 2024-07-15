package io.ngocnhan_tran1996.spring.jdbc.oracle.converter.support;

import io.ngocnhan_tran1996.spring.jdbc.oracle.converter.OracleConverter;
import io.ngocnhan_tran1996.spring.jdbc.oracle.converter.OracleConverterFactory;
import java.util.Optional;

class NumberToStringConverterFactory implements OracleConverterFactory<Number, String> {

    @Override
    public boolean matches(Class<?> sourceType, Class<?> targetType) {

        return Number.class.isAssignableFrom(sourceType)
            && String.class.isAssignableFrom(targetType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends String> OracleConverter<Number, T> getOracleConverter(Class<T> targetType) {

        return (OracleConverter<Number, T>) new NumberToString();
    }

    record NumberToString() implements OracleConverter<Number, String> {

        @Override
        public String convert(Number source) {

            return Optional.ofNullable(source)
                .map(Number::toString)
                .orElse(null);
        }

    }

}