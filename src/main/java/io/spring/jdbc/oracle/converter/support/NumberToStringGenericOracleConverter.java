package io.spring.jdbc.oracle.converter.support;

import io.spring.jdbc.oracle.converter.ConvertKey;
import io.spring.jdbc.oracle.converter.GenericOracleConverter;
import java.util.Optional;
import org.springframework.core.convert.TypeDescriptor;

final class NumberToStringGenericOracleConverter implements GenericOracleConverter {

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {

        return Number.class.isAssignableFrom(sourceType.getType())
            && String.class.isAssignableFrom(targetType.getType());
    }

    @Override
    public ConvertKey getConvertKey() {

        return new ConvertKey(Number.class, String.class);
    }

    @Override
    public Object convert(Object source) {

        return Optional.ofNullable((Number) source)
            .map(Number::toString)
            .orElse(null);
    }

}