package io.ngocnhan_tran1996.spring.jdbc.oracle.converter;

public interface OracleConverterFactory<S, D> {

    boolean matches(Class<?> sourceType, Class<?> targetType);

    <T extends D> OracleConverter<S, T> getOracleConverter(Class<T> targetType);

}