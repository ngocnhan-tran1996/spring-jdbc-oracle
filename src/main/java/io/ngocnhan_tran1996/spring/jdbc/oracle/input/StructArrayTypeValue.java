package io.ngocnhan_tran1996.spring.jdbc.oracle.input;

import static io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Strings.NOT_BLANK;

import io.ngocnhan_tran1996.spring.jdbc.oracle.exception.ValueException;
import io.ngocnhan_tran1996.spring.jdbc.oracle.mapper.Mapper;
import io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Strings;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import oracle.jdbc.OracleConnection;

class StructArrayTypeValue<T> extends ArrayTypeValue<T> {

    private final String structTypeName;
    private final Mapper<T> mapper;

    public StructArrayTypeValue(
        String arrayTypeName,
        Collection<T> values,
        String structTypeName,
        Mapper<T> mapper) {

        super(arrayTypeName, values);
        if (Strings.isBlank(structTypeName)) {

            throw new ValueException(NOT_BLANK.formatted("structTypeName"));
        }
        this.structTypeName = structTypeName.toUpperCase();
        this.mapper = mapper;
    }

    @Override
    protected Object createTypeValue(Connection connection, String typeName) throws SQLException {

        if (this.values == null) {

            return super.createTypeValue(connection, typeName);
        }

        List<Struct> structs = new ArrayList<>(this.values.size());
        this.values.forEach(value -> {

            Struct struct = this.mapper.toStruct(connection, this.structTypeName, value);
            structs.add(struct);
        });

        return connection
            .unwrap(OracleConnection.class)
            .createOracleArray(typeName, structs.toArray(Struct[]::new));
    }

}