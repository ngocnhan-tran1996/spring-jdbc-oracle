package io.spring.jdbc.oracle.parameter.output;

import java.sql.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Struct;
import java.sql.Types;

class ArrayReturnType extends AbstractReturnType {

    @Override
    protected Object convertStruct(Connection connection, Struct struct) {

        throw new UnsupportedOperationException("not implement");
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