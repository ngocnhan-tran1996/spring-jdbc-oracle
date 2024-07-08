package io.ngocnhan_tran1996.spring.jdbc.oracle.input;

import static io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Strings.NOT_BLANK;

import io.ngocnhan_tran1996.spring.jdbc.oracle.exception.ValueException;
import io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Strings;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import oracle.jdbc.OracleConnection;

class ArrayTypeValue<T extends Collection<?>> extends AbstractTypeValue {

    protected final T values;
    private final String arrayTypeName;

    public ArrayTypeValue(String arrayTypeName, T values) {

        if (Strings.isBlank(arrayTypeName)) {

            throw new ValueException(NOT_BLANK.formatted("arrayTypeName"));
        }

        this.arrayTypeName = arrayTypeName.toUpperCase();
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

}