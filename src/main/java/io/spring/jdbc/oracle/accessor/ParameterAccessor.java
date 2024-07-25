package io.spring.jdbc.oracle.accessor;

import static io.spring.jdbc.oracle.utils.Strings.NOT_NULL;

import io.spring.jdbc.oracle.exception.ValueException;
import io.spring.jdbc.oracle.utils.Strings;

public abstract class ParameterAccessor<T> {

    private final String parameterName;
    private final Class<T> mappedClass;

    protected ParameterAccessor(String parameterName, Class<T> mappedClass) {

        if (Strings.isBlank(parameterName)) {

            throw new ValueException(NOT_NULL.formatted("parameter"));
        }

        this.parameterName = parameterName.toUpperCase();
        this.mappedClass = new ClassRecord<>(mappedClass).mappedClass();
    }

    public String getParameterName() {

        return this.parameterName;
    }

    public Class<T> getMappedClass() {

        return this.mappedClass;
    }

}