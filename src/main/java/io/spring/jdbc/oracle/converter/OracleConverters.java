package io.spring.jdbc.oracle.converter;

import org.springframework.core.convert.TypeDescriptor;

public interface OracleConverters {

    void addGenericConverter(GenericOracleConverter<?, ?> converterFactory);

    void addConverter(OracleConverter<?, ?> converter);

    Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType);

    Class<?> determineJavaClassForJdbcTypeCode(Class<?> sourceType);

}