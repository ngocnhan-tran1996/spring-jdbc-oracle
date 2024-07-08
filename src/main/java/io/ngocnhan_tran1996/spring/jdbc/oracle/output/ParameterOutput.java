package io.ngocnhan_tran1996.spring.jdbc.oracle.output;

import java.util.Objects;
import java.util.Optional;
import org.springframework.jdbc.core.SqlOutParameter;

public final class ParameterOutput<T> {

    private final String parameterName;
    private final String typeName;

    private ParameterOutput(String parameterName, String typeName) {

        this.parameterName = parameterName;
        this.typeName = typeName;
    }

    public static ParameterOutput<Object> withParameterName(String parameterName) {

        // TODO add logic
        return new ParameterOutput<>(parameterName, null);
    }

    public static <T> ParameterOutput<T> withParameterName(
        String parameterName,
        String typeName,
        Class<T> clazz) {

        Objects.requireNonNull(clazz);

        // TODO add logic
        return new ParameterOutput<>(parameterName, typeName);
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