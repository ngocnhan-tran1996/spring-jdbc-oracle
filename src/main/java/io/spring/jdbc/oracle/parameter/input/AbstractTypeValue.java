package io.spring.jdbc.oracle.parameter.input;

import java.sql.Connection;
import java.sql.SQLException;
import org.springframework.jdbc.core.support.AbstractSqlTypeValue;

abstract class AbstractTypeValue extends AbstractSqlTypeValue {

    @Override
    protected Object createTypeValue(Connection connection, int sqlType, String typeName)
        throws SQLException {

        return this.createTypeValue(connection, this.getTypeName());
    }

    protected abstract String getTypeName();

    protected abstract Object createTypeValue(Connection connection, String typeName)
        throws SQLException;

}