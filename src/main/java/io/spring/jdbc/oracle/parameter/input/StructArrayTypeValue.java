package io.spring.jdbc.oracle.parameter.input;

import static io.spring.jdbc.oracle.utils.Strings.NOT_BLANK;
import static io.spring.jdbc.oracle.utils.Strings.NOT_NULL;

import io.spring.jdbc.oracle.exception.ValueException;
import io.spring.jdbc.oracle.mapper.Mapper;
import io.spring.jdbc.oracle.utils.Strings;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Collection;
import oracle.jdbc.OracleConnection;

class StructArrayTypeValue<T> extends ArrayTypeValue<T> {

    private final String structTypeName;
    private final Mapper mapper;

    StructArrayTypeValue(
        String arrayTypeName,
        Collection<T> values,
        String structTypeName,
        Mapper mapper) {

        super(arrayTypeName, values);

        if (Strings.isBlank(structTypeName)) {

            throw new ValueException(NOT_BLANK.formatted("structTypeName"));
        }

        if (mapper == null) {

            throw new ValueException(NOT_NULL.formatted("mapper"));
        }

        this.structTypeName = structTypeName.toUpperCase();
        this.mapper = mapper;
    }

    @Override
    protected Object createTypeValue(Connection connection, String typeName) throws SQLException {

        if (super.values() == null) {

            return super.createTypeValue(connection, typeName);
        }

        var values = new ArrayList<>(super.values());
        var size = values.size();
        Struct[] structs = new Struct[size];

        for (int i = 0; i < size; i++) {

            structs[i] = this.mapper.toStruct(connection, this.structTypeName, values.get(i));
        }

        return connection
            .unwrap(OracleConnection.class)
            .createOracleArray(typeName, structs);
    }

}