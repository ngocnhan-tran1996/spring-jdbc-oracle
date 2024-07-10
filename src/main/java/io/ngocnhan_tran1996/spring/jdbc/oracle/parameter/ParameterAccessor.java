package io.ngocnhan_tran1996.spring.jdbc.oracle.parameter;

import java.util.Objects;

public abstract class ParameterAccessor<T> {

    private String parameterName;
    private Class<T> mappedClass;

    public String getParameterName() {

        return this.parameterName;
    }

    public void setParameterName(String parameterName) {

        this.parameterName = parameterName == null
            ? null
            : parameterName.toUpperCase();
    }

    public Class<T> getMappedClass() {

        return this.mappedClass;
    }

    public void setMappedClass(Class<T> mappedClass) {

        this.mappedClass = Objects.requireNonNull(mappedClass, "mapped class");
    }

}