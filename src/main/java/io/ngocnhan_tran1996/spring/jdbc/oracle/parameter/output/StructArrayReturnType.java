package io.ngocnhan_tran1996.spring.jdbc.oracle.parameter.output;

import io.ngocnhan_tran1996.spring.jdbc.oracle.exception.ValueException;
import io.ngocnhan_tran1996.spring.jdbc.oracle.mapper.Mapper;
import java.sql.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.ArrayList;

class StructArrayReturnType<T> extends ArrayReturnType<T> {

    private final Mapper<T> mapper;

    StructArrayReturnType(Mapper<T> mapper) {

        this.mapper = mapper;
    }

    @Override
    protected T convertStruct(Connection connection, Struct struct) {

        return this.mapper.fromStruct(connection, struct);
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

            String className = object == null
                ? null
                : object.getClass().getName();
            throw new ValueException("Expected STRUCT but got '%s'".formatted(className));
        }

        // convert type object array
        return values.toArray();
    }

}