package io.spring.jdbc.oracle.parameter.input;

import io.spring.jdbc.oracle.mapper.Mapper;
import io.spring.jdbc.oracle.utils.Validators;
import java.sql.Connection;
import java.sql.Struct;

class StructTypeValue<T> extends AbstractTypeValue {

    private final String structTypeName;
    private final T value;
    private final Mapper mapper;

    StructTypeValue(String structTypeName, Mapper mapper) {

        this(structTypeName, null, mapper);
    }

    StructTypeValue(String structTypeName, T value, Mapper mapper) {

        this.structTypeName = Validators.requireNotNull(structTypeName, "structTypeName")
            .toUpperCase();
        this.value = value;
        this.mapper = Validators.requireNotNull(mapper, "mapper");
    }

    @Override
    protected String getTypeName() {

        return this.structTypeName;
    }

    @Override
    protected Object createTypeValue(Connection connection, String typeName) {

        return this.createTypeValue(connection, this.value);
    }

    protected Struct createTypeValue(Connection connection, Object value) {

        return this.mapper.toStruct(connection, this.structTypeName, value);
    }

}