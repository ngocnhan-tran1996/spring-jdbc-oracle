package io.ngocnhan_tran1996.spring.jdbc.oracle.input;

import static io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Strings.NOT_NULL;

import io.ngocnhan_tran1996.spring.jdbc.oracle.exception.ValueException;
import java.sql.Types;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.springframework.jdbc.core.SqlInOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlTypeValue;

public final class ParameterInput<T> {

    private final String parameterName;
    private final Collection<T> values;
    private AbstractTypeValue typeValue;
    private Integer type;

    private ParameterInput(String parameterName, Collection<T> values) {

        this.parameterName = parameterName;
        this.values = values;
    }

    public static <T> ParameterInput<T> withValue(String parameterName, T value) {

        var inputValues = value == null
            ? null
            : List.of(value);
        return withValues(parameterName, inputValues);
    }

    public static <T> ParameterInput<T> withValues(String parameterName, Collection<T> values) {

        return new ParameterInput<>(parameterName, values);
    }

    public ParameterInput<T> withArray(String arrayTypeName) {

        this.typeValue = new ArrayTypeValue<>(arrayTypeName, values);
        this.type = Types.ARRAY;
        return this;
    }

    public ParameterInput<T> withStruct(String structTypeName) {

        var value = values == null || values.isEmpty()
            ? null
            : values.stream()
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
        this.typeValue = new StructTypeValue<>(structTypeName, value);
        this.type = Types.STRUCT;
        return this;
    }

    public ParameterInput<T> withStructArray(String arrayTypeName, String structTypeName) {

        this.typeValue = new StructArrayTypeValue<>(arrayTypeName, values, structTypeName);
        this.type = Types.ARRAY;
        return this;
    }

    public SqlTypeValue typeValue() {

        return this.typeValue;
    }

    public SqlParameter sqlParameter() {

        this.validateTypeValue();
        return new SqlParameter(
            this.parameterName,
            this.type,
            this.typeValue.getTypeName()
        );
    }

    public SqlInOutParameter sqlInOutParameter() {

        this.validateTypeValue();
        return new SqlInOutParameter(
            this.parameterName,
            this.type,
            this.typeValue.getTypeName(),
            // FIXME add logic
            null
        );
    }

    private void validateTypeValue() {

        if (this.type == null || this.typeValue == null) {

            throw new ValueException(NOT_NULL.formatted("Type"));
        }

    }
}