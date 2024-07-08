package io.ngocnhan_tran1996.spring.jdbc.oracle.input;

import static io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Strings.NOT_BLANK;

import io.ngocnhan_tran1996.spring.jdbc.oracle.exception.ValueException;
import io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Strings;
import java.sql.Connection;
import java.sql.SQLException;

class StructTypeValue<T> extends AbstractTypeValue {

    private final String structTypeName;

    public StructTypeValue(String structTypeName, T value) {

        if (Strings.isBlank(structTypeName)) {

            throw new ValueException(NOT_BLANK.formatted("structTypeName"));
        }
        this.structTypeName = structTypeName;
    }

    @Override
    protected String getTypeName() {

        return this.structTypeName;
    }

    @Override
    protected Object createTypeValue(Connection connection, String typeName) throws SQLException {

        // TODO add logic
        return null;
    }

}