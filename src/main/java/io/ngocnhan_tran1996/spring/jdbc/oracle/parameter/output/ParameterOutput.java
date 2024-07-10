package io.ngocnhan_tran1996.spring.jdbc.oracle.parameter.output;

import io.ngocnhan_tran1996.spring.jdbc.oracle.mapper.BeanPropertyMapper;
import io.ngocnhan_tran1996.spring.jdbc.oracle.parameter.ParameterAccessor;
import java.util.Optional;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlReturnType;

public final class ParameterOutput<T> extends ParameterAccessor<T> {

    private AbstractReturnType<T> returnType;
    private String typeName;

    private ParameterOutput(String parameterName, Class<T> mappedClass) {

        super.setParameterName(parameterName);
        super.setMappedClass(mappedClass);
    }

    public static ParameterOutput<Object> withParameterName(String parameterName) {

        return new ParameterOutput<>(parameterName, null);
    }

    public static <T> ParameterOutput<T> withParameterName(
        String parameterName,
        Class<T> mappedClass) {

        return new ParameterOutput<>(parameterName, mappedClass);
    }

    public ParameterOutput<T> withArrayType(String typeName) {

        this.returnType = new ArrayReturnType<>();
        this.typeName = typeName;
        return this;
    }

    public ParameterOutput<T> withArrayStructType(String typeName) {

        this.returnType = new StructArrayReturnType<>(
            BeanPropertyMapper.newInstance(getMappedClass())
        );
        this.typeName = typeName;
        return this;
    }

    public ParameterOutput<T> withStructType(String typeName) {

        this.returnType = new StructReturnType<>(BeanPropertyMapper.newInstance(getMappedClass()));
        this.typeName = typeName;
        return this;
    }

    public SqlOutParameter sqlOutParameter() {

        return new SqlOutParameter(
            getParameterName(),
            this.returnType.sqlType(),
            Optional.ofNullable(this.typeName)
                .map(String::toUpperCase)
                .orElse(null),
            this.returnType
        );
    }

    public SqlReturnType sqlReturnType() {

        return this.returnType;
    }

}