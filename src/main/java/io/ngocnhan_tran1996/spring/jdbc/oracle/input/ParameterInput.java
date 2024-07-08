package io.ngocnhan_tran1996.spring.jdbc.oracle.input;

import static io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Strings.NOT_NULL;

import io.ngocnhan_tran1996.spring.jdbc.oracle.exception.ValueException;
import io.ngocnhan_tran1996.spring.jdbc.oracle.mapper.BeanPropertyMapper;
import java.sql.Types;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.springframework.jdbc.core.SqlInOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlTypeValue;

public final class ParameterInput<T> {

    private final String parameterName;
    private final Class<T> mappedClass;
    private Collection<T> values;
    private AbstractTypeValue typeValue;
    private Integer type;

    private ParameterInput(String parameterName, Class<T> mappedClass) {

        this.parameterName = parameterName;
        this.mappedClass = mappedClass;
    }

    public static <T> ParameterInput<T> newInstance(String parameterName) {

        return newInstance(parameterName, null);
    }

    public static <T> ParameterInput<T> newInstance(String parameterName, Class<T> mappedClass) {

        return new ParameterInput<>(parameterName, mappedClass);
    }

    public ParameterInput<T> withValues(Collection<T> values) {

        this.values = values;
        return this;
    }

    public ParameterInput<T> withValue(T value) {

        this.values = value == null
            ? null
            : List.of(value);
        return this;
    }

    public ParameterInput<T> withArray(String arrayTypeName) {

        this.typeValue = new ArrayTypeValue<>(arrayTypeName, values);
        this.type = Types.ARRAY;
        return this;
    }

    public ParameterInput<T> withStruct(String structTypeName) {

        var value = this.values == null || this.values.isEmpty()
            ? null
            : this.values.stream()
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
        this.typeValue = new StructTypeValue<>(
            structTypeName,
            value,
            BeanPropertyMapper.newInstance(this.mappedClass)
        );
        this.type = Types.STRUCT;
        return this;
    }

    public ParameterInput<T> withStructArray(String arrayTypeName, String structTypeName) {

        this.typeValue = new StructArrayTypeValue<>(
            arrayTypeName,
            this.values,
            structTypeName,
            BeanPropertyMapper.newInstance(this.mappedClass)
        );
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