package io.ngocnhan_tran1996.spring.jdbc.oracle.converter;

import org.springframework.core.convert.TypeDescriptor;

public interface GenericOracleConverter<S, D> extends OracleConverter<S, D> {

    boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType);

}