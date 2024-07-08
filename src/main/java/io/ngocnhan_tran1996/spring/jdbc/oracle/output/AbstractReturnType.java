package io.ngocnhan_tran1996.spring.jdbc.oracle.output;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Struct;
import java.sql.Types;
import org.springframework.jdbc.core.SqlReturnType;

abstract class AbstractReturnType<T> implements SqlReturnType {

    @Override
    public Object getTypeValue(CallableStatement cs, int paramIndex, int sqlType, String typeName)
        throws SQLException {

        if (this.sqlType() == Types.STRUCT) {

            var struct = (Struct) cs.getObject(paramIndex);
            return struct == null
                ? null
                : this.convertStruct(cs.getConnection(), struct);
        }

        var array = cs.getArray(paramIndex);
        return array == null
            ? null
            : this.convertArray(cs.getConnection(), array);
    }

    protected abstract T convertStruct(Connection connection, Struct struct) throws SQLException;

    protected abstract Object convertArray(Connection connection, Array array) throws SQLException;

    public abstract int sqlType();

}