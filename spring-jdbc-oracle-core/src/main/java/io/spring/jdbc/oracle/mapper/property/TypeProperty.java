package io.spring.jdbc.oracle.mapper.property;

import io.spring.jdbc.oracle.converter.OracleConverter;

public class TypeProperty {

    private String fieldName;
    private String structName;
    private String arrayName;
    private Class<? extends OracleConverter<Object, Object>> converter;
    private Types type = Types.NONE;

    public String getFieldName() {

        return this.fieldName;
    }

    public void setFieldName(String fieldName) {

        this.fieldName = fieldName;
    }

    public String getStructName() {

        return this.structName;
    }

    public void setStructName(String structName) {

        this.structName = structName;
    }

    public String getArrayName() {

        return this.arrayName;
    }

    public void setArrayName(String arrayName) {

        this.arrayName = arrayName;
    }

    public Class<? extends OracleConverter<Object, Object>> getConverter() {

        return this.converter;
    }

    @SuppressWarnings("unchecked")
    public void setConverter(
        Class<? extends OracleConverter<?, ?>> converter) {

        this.converter = (Class<? extends OracleConverter<Object, Object>>) converter;
    }

    public Types getType() {

        return this.type;
    }

    public void setType(Types type) {

        this.type = type;
    }

    public enum Types {

        NONE,
        STRUCT,
        ARRAY,
        STRUCT_ARRAY,
        CONVERTER
    }

}