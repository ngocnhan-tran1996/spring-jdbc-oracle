package io.ngocnhan_tran1996.spring.jdbc.oracle.output;

import java.sql.Types;

class StructReturnType<T> extends StructArrayReturnType<T> {

    @Override
    public int sqlType() {

        return Types.STRUCT;
    }

}