package io.spring.jdbc.oracle.converter;

import org.springframework.core.convert.TypeDescriptor;

public interface GenericOracleConverter extends OracleConverter<Object, Object> {

    boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType);

    ConvertKey getConvertKey();

}