package io.spring.jdbc.oracle.parameter.input;

import io.spring.jdbc.oracle.mapper.Mapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Collection;
import oracle.jdbc.OracleConnection;

class StructArrayTypeValue<T> extends ArrayTypeValue<T> {

    private final StructTypeValue<T> structTypeValue;

    StructArrayTypeValue(
        String arrayTypeName,
        Collection<T> values,
        String structTypeName,
        Mapper mapper) {

        super(arrayTypeName, values);
        this.structTypeValue = new StructTypeValue<>(structTypeName, mapper);
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

            structs[i] = this.structTypeValue.createTypeValue(connection, values.get(i));
        }

        return connection
            .unwrap(OracleConnection.class)
            .createOracleArray(typeName, structs);
    }

}