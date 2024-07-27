package io.spring.jdbc.oracle.accessor;

import io.spring.jdbc.oracle.utils.Validators;

public abstract class ParameterAccessor<T> {

    private final String parameterName;
    private final Class<T> mappedClass;

    protected ParameterAccessor(String parameterName, Class<T> mappedClass) {

        this.parameterName = Validators.requireNotBank(parameterName, "parameter")
            .toUpperCase();
        this.mappedClass = new ClassRecord<>(mappedClass).mappedClass();
    }

    public String getParameterName() {

        return this.parameterName;
    }

    public Class<T> getMappedClass() {

        return this.mappedClass;
    }

}