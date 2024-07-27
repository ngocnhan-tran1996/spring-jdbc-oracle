package io.spring.jdbc.oracle.mapper;

import io.spring.jdbc.oracle.accessor.ClassRecord;
import io.spring.jdbc.oracle.converter.OracleConverters;
import java.sql.Connection;
import java.sql.Struct;
import java.util.Map;

public final class DelegateMapper<T> {

    private final Mapper mapper;

    private DelegateMapper(Class<T> mappedClass) {

        this.mapper = new ClassRecord<>(mappedClass).isTypeRecord()
            ? RecordPropertyMapper.newInstance(mappedClass)
            : BeanPropertyMapper.newInstance(mappedClass);
    }

    public static <T> DelegateMapper<T> newInstance(Class<T> mappedClass) {

        return new DelegateMapper<>(mappedClass);
    }

    public Struct toStruct(Connection connection, String typeName, T source) {

        return this.mapper.toStruct(connection, typeName, source);
    }

    public T fromStruct(Connection connection, Struct struct) {

        return this.mapper.fromStruct(connection, struct);
    }

    public T convert(Map<String, Object> source) {

        return this.mapper.convert(source);
    }

    public Mapper get() {

        return this.mapper;
    }

    public void setConverters(OracleConverters converters) {

        ((BeanPropertyMapper<?>) mapper).setConverters(converters);
    }

}