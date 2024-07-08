package io.ngocnhan_tran1996.spring.jdbc.oracle.input;

import static io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Strings.NOT_BLANK;
import static io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Strings.NOT_NULL;

import io.ngocnhan_tran1996.spring.jdbc.oracle.exception.ValueException;
import io.ngocnhan_tran1996.spring.jdbc.oracle.mapper.Mapper;
import io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Strings;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

class StructTypeValue<T> extends AbstractTypeValue {

    private final String structTypeName;
    private final T value;
    private final Mapper<T> mapper;

    public StructTypeValue(String structTypeName, T value, Mapper<T> mapper) {

        if (Strings.isBlank(structTypeName)) {

            throw new ValueException(NOT_BLANK.formatted("structTypeName"));
        }

        this.structTypeName = structTypeName.toUpperCase();
        this.value = value;
        this.mapper = Objects.requireNonNull(mapper, NOT_NULL.formatted("mapper"));
    }

    @Override
    protected String getTypeName() {

        return this.structTypeName;
    }

    @Override
    protected Object createTypeValue(Connection connection, String typeName) throws SQLException {

        return this.mapper.toStruct(connection, this.structTypeName, this.value);
    }

}