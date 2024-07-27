package io.spring.jdbc.oracle.parameter.output;

import io.spring.jdbc.oracle.mapper.Mapper;
import java.sql.Types;

class StructReturnType extends StructArrayReturnType {

    StructReturnType(Mapper mapper) {

        super(mapper);
    }

    @Override
    public int sqlType() {

        return Types.STRUCT;
    }

}