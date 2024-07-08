package io.ngocnhan_tran1996.spring.jdbc.oracle.mapper;

import java.sql.Connection;
import java.sql.Struct;

public interface Mapper<T> {

    Struct toStruct(Connection connection, String typeName, T source);

    T fromStruct(Connection connection, Struct struct);

}