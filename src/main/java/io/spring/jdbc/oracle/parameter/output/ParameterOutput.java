package io.spring.jdbc.oracle.parameter.output;

import static io.spring.jdbc.oracle.utils.Strings.NOT_NULL;

import io.spring.jdbc.oracle.accessor.ParameterAccessor;
import io.spring.jdbc.oracle.exception.ValueException;
import io.spring.jdbc.oracle.mapper.DelegateMapper;
import io.spring.jdbc.oracle.mapper.Mapper;
import io.spring.jdbc.oracle.utils.Strings;
import java.sql.Array;
import java.sql.Connection;
import java.sql.Struct;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlReturnType;

public final class ParameterOutput<T> extends ParameterAccessor<T> {

    private final Mapper mapper;
    private AbstractReturnType returnType;
    private String typeName;

    private ParameterOutput(String parameterName, Class<T> mappedClass) {

        super(parameterName, mappedClass);
        this.mapper = DelegateMapper.newInstance(mappedClass).get();
    }

    public static <T> ParameterOutput<T> withParameterName(
        String parameterName,
        Class<T> mappedClass) {

        return new ParameterOutput<>(parameterName, mappedClass);
    }

    public static ParameterOutput<Object> withParameterName(String parameterName) {

        return withParameterName(parameterName, Object.class);
    }

    public ParameterOutput<T> withArray(String typeName) {

        this.typeName = typeName;
        this.returnType = new ArrayReturnType();
        return this;
    }

    public ParameterOutput<T> withStruct(String typeName) {

        this.typeName = typeName;
        this.returnType = new StructReturnType(this.mapper);
        return this;
    }

    public ParameterOutput<T> withStructArray(String typeName) {

        this.typeName = typeName;
        this.returnType = new StructArrayReturnType(this.mapper);
        return this;
    }

    public SqlOutParameter sqlOutParameter() {

        if (Strings.isBlank(this.typeName)) {

            throw new ValueException(NOT_NULL.formatted("returnType"));
        }

        return new SqlOutParameter(
            super.getParameterName(),
            this.returnType.sqlType(),
            this.typeName.toUpperCase(),
            this.returnType
        );
    }

    public Object convert(Connection connection, Object value) {

        try {

            var sqlReturnType = this.sqlReturnType();

            return sqlReturnType instanceof StructReturnType structReturnType
                ? structReturnType.convertStruct(connection, (Struct) value)
                : ((AbstractReturnType) sqlReturnType).convertArray(connection, (Array) value);
        } catch (Exception ex) {

            return null;
        }

    }

    public SqlReturnType sqlReturnType() {

        return this.returnType;
    }

}