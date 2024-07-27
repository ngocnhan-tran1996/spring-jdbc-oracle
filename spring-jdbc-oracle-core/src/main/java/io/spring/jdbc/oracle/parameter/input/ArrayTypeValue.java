package io.spring.jdbc.oracle.parameter.input;

import io.spring.jdbc.oracle.utils.Validators;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import oracle.jdbc.OracleConnection;

class ArrayTypeValue<T> extends AbstractTypeValue {

    private final String arrayTypeName;
    private final Collection<T> values;

    ArrayTypeValue(String arrayTypeName, Collection<T> values) {

        this.arrayTypeName = Validators.requireNotBank(arrayTypeName, "arrayTypeName")
            .toUpperCase();
        this.values = values;
    }

    @Override
    protected String getTypeName() {

        return this.arrayTypeName;
    }

    @Override
    protected Object createTypeValue(Connection connection, String typeName) throws SQLException {

        final var oracleArray = this.values == null
            ? null
            : this.values.toArray();

        return connection
            .unwrap(OracleConnection.class)
            .createOracleArray(typeName, oracleArray);
    }

    protected Collection<T> values() {

        return this.values;
    }

}