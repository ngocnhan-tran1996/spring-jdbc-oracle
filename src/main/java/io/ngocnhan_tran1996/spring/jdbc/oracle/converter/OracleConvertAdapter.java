package io.ngocnhan_tran1996.spring.jdbc.oracle.converter;

import io.ngocnhan_tran1996.spring.jdbc.oracle.converter.support.NoneConverter;

public class OracleConvertAdapter {

    public static final OracleConvertAdapter NONE = new OracleConvertAdapter(new NoneConverter());

    private final OracleConverter<Object, Object> converter;

    @SuppressWarnings("unchecked")
    public OracleConvertAdapter(OracleConverter<?, ?> converter) {

        this.converter = (OracleConverter<Object, Object>) converter;
    }

    public Object convert(Object source) {

        return this.converter.convert(source);
    }

}