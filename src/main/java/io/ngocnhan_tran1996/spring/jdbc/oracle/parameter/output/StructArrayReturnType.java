package io.ngocnhan_tran1996.spring.jdbc.oracle.parameter.output;

import io.ngocnhan_tran1996.spring.jdbc.oracle.exception.ValueException;
import io.ngocnhan_tran1996.spring.jdbc.oracle.mapper.Mapper;
import java.sql.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Struct;

class StructArrayReturnType<T> extends ArrayReturnType<T> {

    private final Mapper<T> mapper;

    StructArrayReturnType(Mapper<T> mapper) {

        this(mapper, null);
    }

    StructArrayReturnType(Mapper<T> mapper, Class<?> targetType) {

        super(targetType);
        this.mapper = mapper;
    }

    @Override
    protected T convertStruct(Connection connection, Struct struct) {

        return this.mapper.fromStruct(connection, struct);
    }

    @Override
    protected Object convertArray(Connection connection, Array array) throws SQLException {

        var objects = (Object[]) array.getArray();
        var length = objects.length;
        var values = new Object[length];

        for (int i = 0; i < objects.length; i++) {

            var object = objects[i];
            if (object instanceof Struct struct) {

                values[i] = this.convertStruct(connection, struct);
                continue;
            }

            String className = object == null
                ? null
                : object.getClass().getName();
            throw new ValueException("Expected STRUCT but got '%s'".formatted(className));
        }

        return super.getTargetType() == null
            ? values
            : super.arrayToCollection(values);
    }

}