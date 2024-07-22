package io.spring.jdbc.oracle.converter.support;

import io.spring.jdbc.oracle.converter.GenericOracleConverter;
import java.util.Optional;
import org.springframework.core.convert.TypeDescriptor;

class NumberToStringGenericOracleConverter implements GenericOracleConverter<Number, String> {

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {

        return Number.class.isAssignableFrom(sourceType.getType())
            && String.class.isAssignableFrom(targetType.getType());
    }

    @Override
    public String convert(Number source) {

        return Optional.ofNullable(source)
            .map(Number::toString)
            .orElse(null);
    }

}