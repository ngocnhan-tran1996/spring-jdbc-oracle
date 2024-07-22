package io.spring.jdbc.oracle.parameter.input;

import io.spring.jdbc.oracle.exception.ValueException;
import io.spring.jdbc.oracle.mapper.Mapper;
import io.spring.jdbc.oracle.utils.Strings;
import java.sql.Connection;
import java.util.Objects;

class StructTypeValue<T> extends AbstractTypeValue {

    private final String structTypeName;
    private final T value;
    private final Mapper mapper;

    StructTypeValue(String structTypeName, T value, Mapper mapper) {

        if (Strings.isBlank(structTypeName)) {

            throw new ValueException(Strings.NOT_BLANK.formatted("structTypeName"));
        }

        this.structTypeName = structTypeName.toUpperCase();
        this.value = value;
        this.mapper = Objects.requireNonNull(mapper, Strings.NOT_NULL.formatted("mapper"));
    }

    @Override
    protected String getTypeName() {

        return this.structTypeName;
    }

    @Override
    protected Object createTypeValue(Connection connection, String typeName) {

        return this.mapper.toStruct(connection, this.structTypeName, this.value);
    }

}