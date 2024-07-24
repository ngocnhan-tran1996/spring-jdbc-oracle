package io.spring.jdbc.oracle.converter.support;

import io.spring.jdbc.oracle.converter.OracleConverter;

public final class NoneConverter implements OracleConverter<Object, Object> {

    @Override
    public Object convert(Object source) {

        return source;
    }

}