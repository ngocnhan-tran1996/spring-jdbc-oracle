package io.spring.jdbc.oracle.parameter.input;

import static io.spring.jdbc.oracle.utils.Strings.NOT_BLANK;
import static io.spring.jdbc.oracle.utils.Strings.NOT_NULL;

import io.spring.jdbc.oracle.exception.ValueException;
import io.spring.jdbc.oracle.mapper.Mapper;
import io.spring.jdbc.oracle.utils.Strings;
import java.sql.Connection;

class StructTypeValue<T> extends AbstractTypeValue {

    private final String structTypeName;
    private final T value;
    private final Mapper mapper;

    StructTypeValue(String structTypeName, T value, Mapper mapper) {

        if (Strings.isBlank(structTypeName)) {

            throw new ValueException(NOT_BLANK.formatted("structTypeName"));
        }

        if (mapper == null) {

            throw new ValueException(NOT_NULL.formatted("mapper"));
        }

        this.structTypeName = structTypeName.toUpperCase();
        this.value = value;
        this.mapper = mapper;
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