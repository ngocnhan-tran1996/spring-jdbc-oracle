package io.ngocnhan_tran1996.spring.jdbc.oracle.converter.support;

import io.ngocnhan_tran1996.spring.jdbc.oracle.converter.OracleConverter;

public class NoneConverter implements OracleConverter<Object, Object> {

    @Override
    public Object convert(Object source) {

        return source;
    }

}