package io.spring.jdbc.oracle.parameter.output;

import io.spring.jdbc.oracle.mapper.Mapper;
import java.sql.Types;

class StructReturnType<T> extends StructArrayReturnType<T> {

    StructReturnType(Mapper mapper) {

        super(mapper);
    }

    @Override
    public int sqlType() {

        return Types.STRUCT;
    }

}