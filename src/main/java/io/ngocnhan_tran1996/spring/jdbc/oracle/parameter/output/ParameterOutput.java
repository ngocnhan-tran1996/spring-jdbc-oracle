package io.ngocnhan_tran1996.spring.jdbc.oracle.parameter.output;

import static io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Strings.NOT_NULL;

import java.util.Objects;
import java.util.Optional;
import org.springframework.jdbc.core.SqlOutParameter;

public final class ParameterOutput<T> {

    private final String parameterName;
    private final Class<T> mappedClass;
    private String typeName;

    private ParameterOutput(String parameterName, Class<T> mappedClass) {

        this.parameterName = parameterName;
        this.mappedClass = mappedClass;
    }

    public static ParameterOutput<Object> withParameterName(String parameterName) {

        return new ParameterOutput<>(parameterName, null);
    }

    public static <T> ParameterOutput<T> withParameterName(
        String parameterName,
        Class<T> mappedClass) {

        Objects.requireNonNull(mappedClass, NOT_NULL.formatted("mapped class"));
        return new ParameterOutput<>(parameterName, mappedClass);
    }

    public SqlOutParameter toSqlOutParameter() {

        return new SqlOutParameter(
            this.parameterName,
            // TODO add logic
//            returnType.sqlType(),
            -1,
            Optional.ofNullable(this.typeName)
                .map(String::toUpperCase)
                .orElse(null),
            // TODO add logic
            null
        );
    }

}