package io.ngocnhan_tran1996.spring.jdbc.oracle.parameter.output;

import java.sql.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Struct;
import java.sql.Types;

class ArrayReturnType<T> extends AbstractReturnType<T> {

    @Override
    protected T convertStruct(Connection connection, Struct struct) {

        return null;
    }

    @Override
    protected Object convertArray(Connection connection, Array array) throws SQLException {

        return array.getArray();
    }

    @Override
    public int sqlType() {

        return Types.ARRAY;
    }

}