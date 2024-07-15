package io.ngocnhan_tran1996.spring.jdbc.oracle.converter;

public interface OracleConverters {

    void addConverterFactory(OracleConverterFactory<?, ?> converterFactory);

    void addConverter(OracleConverter<?, ?> converter);

    Object convert(Object source, Class<?> sourceType, Class<?> targetType);

}