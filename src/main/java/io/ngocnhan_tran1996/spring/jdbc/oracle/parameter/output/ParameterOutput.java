package io.ngocnhan_tran1996.spring.jdbc.oracle.parameter.output;

import static io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Strings.NOT_NULL;

import io.ngocnhan_tran1996.spring.jdbc.oracle.accessor.ParameterAccessor;
import io.ngocnhan_tran1996.spring.jdbc.oracle.exception.ValueException;
import io.ngocnhan_tran1996.spring.jdbc.oracle.mapper.DelegateMapper;
import io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Strings;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlReturnType;

public final class ParameterOutput<T> extends ParameterAccessor<T> {

    private final DelegateMapper<T> mapper;
    private AbstractReturnType<T> returnType;
    private String typeName;

    private ParameterOutput(String parameterName, Class<T> mappedClass) {

        super(parameterName, mappedClass);
        this.mapper = DelegateMapper.newInstance(mappedClass);
    }

    public static ParameterOutput<Object> withParameterName(String parameterName) {

        return new ParameterOutput<>(parameterName, Object.class);
    }

    public static <T> ParameterOutput<T> withParameterName(
        String parameterName,
        Class<T> mappedClass) {

        return new ParameterOutput<>(parameterName, mappedClass);
    }

    public ParameterOutput<T> withArray(String typeName) {

        this.returnType = new ArrayReturnType<>();
        this.typeName = typeName;
        return this;
    }

    public ParameterOutput<T> withStructArray(String typeName) {

        this.returnType = new StructArrayReturnType<>(this.mapper.get());
        this.typeName = typeName;
        return this;
    }

    public ParameterOutput<T> withStruct(String typeName) {

        this.returnType = new StructReturnType<>(this.mapper.get());
        this.typeName = typeName;
        return this;
    }

    public SqlOutParameter sqlOutParameter() {

        this.validateReturnType();
        return new SqlOutParameter(
            getParameterName(),
            this.returnType.sqlType(),
            this.typeName,
            this.returnType
        );
    }

    public SqlReturnType sqlReturnType() {

        return this.returnType;
    }

    private void validateReturnType() {

        if (Strings.isBlank(this.typeName) || this.returnType == null) {

            throw new ValueException(NOT_NULL.formatted("returnType"));
        }

    }

}