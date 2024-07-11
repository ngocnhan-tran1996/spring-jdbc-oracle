package io.ngocnhan_tran1996.spring.jdbc.oracle.mapper;

import io.ngocnhan_tran1996.spring.jdbc.oracle.accessor.ClassRecord;
import java.sql.Connection;
import java.sql.Struct;
import java.util.Map;

public final class DelegateMapper<T> implements Mapper<T> {

    private final Mapper<T> mapper;

    private DelegateMapper(Class<T> mappedClass) {

        this.mapper = new ClassRecord<>(mappedClass).isTypeRecord()
            ? RecordPropertyMapper.newInstance(mappedClass)
            : BeanPropertyMapper.newInstance(mappedClass);
    }

    public static <T> DelegateMapper<T> newInstance(Class<T> mappedClass) {

        return new DelegateMapper<>(mappedClass);
    }

    public Mapper<T> get() {

        return this.mapper;
    }

    @Override
    public Struct toStruct(Connection connection, String typeName, T source) {

        return this.mapper.toStruct(connection, typeName, source);
    }

    @Override
    public T fromStruct(Connection connection, Struct struct) {

        return this.mapper.fromStruct(connection, struct);
    }

    @Override
    public T convert(Map<String, Object> source) {

        return this.mapper.convert(source);
    }

}