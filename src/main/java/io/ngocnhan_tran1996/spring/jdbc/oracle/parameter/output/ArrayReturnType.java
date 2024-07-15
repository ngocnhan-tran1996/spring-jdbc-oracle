package io.ngocnhan_tran1996.spring.jdbc.oracle.parameter.output;

import static io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Matchers.not;

import io.ngocnhan_tran1996.spring.jdbc.oracle.exception.ValueException;
import java.sql.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Struct;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import org.springframework.core.CollectionFactory;

class ArrayReturnType<T> extends AbstractReturnType<T> {

    private final Class<?> targetType;

    ArrayReturnType(Class<?> targetType) {

        if (targetType != null && not(Collection.class.isAssignableFrom(targetType))) {

            throw new ValueException("targetType must extend from Collection class");
        }

        this.targetType = targetType;
    }

    @Override
    protected T convertStruct(Connection connection, Struct struct) {

        throw new UnsupportedOperationException("not implement");
    }

    @Override
    protected Object convertArray(Connection connection, Array array) throws SQLException {

        if (this.targetType == null) {

            return array.getArray();
        }

        return this.arrayToCollection((Object[]) array.getArray());
    }

    @Override
    public int sqlType() {

        return Types.ARRAY;
    }

    public Class<?> getTargetType() {

        return this.targetType;
    }

    Collection<Object> arrayToCollection(Object[] objects) {

        var length = objects.length;
        var target = this.targetType.isInterface()
            && this.targetType.isAssignableFrom(ArrayList.class)
            ? new ArrayList<>(length)
            : CollectionFactory.createCollection(this.targetType, length);

        for (int i = 0; i < length; i++) {

            target.add(java.lang.reflect.Array.get(objects, i));
        }

        return target;
    }

}