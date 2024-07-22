package io.spring.jdbc.oracle.mapper;

import java.sql.Connection;
import java.sql.Struct;
import java.util.Map;

public interface Mapper {

    <T> Struct toStruct(Connection connection, String typeName, T source);

    <T> T fromStruct(Connection connection, Struct struct);

    <T> T convert(Map<String, Object> source);

}