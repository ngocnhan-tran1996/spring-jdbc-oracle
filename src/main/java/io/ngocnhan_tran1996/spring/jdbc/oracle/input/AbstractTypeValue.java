package io.ngocnhan_tran1996.spring.jdbc.oracle.input;

import io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Strings;
import java.sql.Connection;
import java.sql.SQLException;
import org.springframework.jdbc.core.support.AbstractSqlTypeValue;

abstract class AbstractTypeValue extends AbstractSqlTypeValue {

    @Override
    protected Object createTypeValue(Connection connection, int sqlType, String typeName)
        throws SQLException {

        var findTypeName = Strings.firstNoneBlank(this.getTypeName(), typeName);
        return this.createTypeValue(connection, findTypeName);
    }

    protected abstract String getTypeName();

    protected abstract Object createTypeValue(Connection connection, String typeName)
        throws SQLException;

}