package io.ngocnhan_tran1996.spring.jdbc.oracle.parameter.output;

import io.ngocnhan_tran1996.spring.jdbc.oracle.mapper.Mapper;
import java.sql.Types;

class StructReturnType<T> extends StructArrayReturnType<T> {

    StructReturnType(Mapper<T> mapper) {

        super(mapper);
    }

    @Override
    public int sqlType() {

        return Types.STRUCT;
    }

}