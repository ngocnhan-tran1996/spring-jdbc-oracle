package io.ngocnhan_tran1996.spring.jdbc.oracle.accessor;

public abstract class ParameterAccessor<T> {

    private final String parameterName;
    private final Class<T> mappedClass;

    protected ParameterAccessor(String parameterName, Class<T> mappedClass) {

        this.parameterName = parameterName == null
            ? null
            : parameterName.toUpperCase();
        this.mappedClass = new ClassRecord<>(mappedClass).mappedClass();
    }

    public String getParameterName() {

        return this.parameterName;
    }

    public Class<T> getMappedClass() {

        return this.mappedClass;
    }

}