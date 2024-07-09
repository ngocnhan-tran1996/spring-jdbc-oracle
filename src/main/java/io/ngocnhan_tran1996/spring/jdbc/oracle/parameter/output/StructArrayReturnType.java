package io.ngocnhan_tran1996.spring.jdbc.oracle.parameter.output;

import java.sql.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.ArrayList;

class StructArrayReturnType<T> extends ArrayReturnType<T> {

    @Override
    protected T convertStruct(Connection connection, Struct struct) throws SQLException {

        // FIXME add logic
        return null;
    }

    @Override
    protected Object convertArray(Connection connection, Array array) throws SQLException {

        var objects = (Object[]) array.getArray();
        var values = new ArrayList<T>(objects.length);

        for (var object : objects) {

            if (object instanceof Struct struct) {

                values.add(this.convertStruct(connection, struct));
                continue;
            }

            // FIXME throw exception
//            String errorMsg = object == null
//                ? null
//                : object.getClass().getName();
//            throw new OracleTypeException(String.format("Expected STRUCT but got '%s'", errorMsg));
        }

        // convert type object array
        return values.toArray();
    }

}