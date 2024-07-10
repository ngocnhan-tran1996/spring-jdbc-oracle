package io.ngocnhan_tran1996.spring.jdbc.oracle.mapper;

import java.util.Objects;

public abstract class ClassMapper<T> extends AbstractMapper<T> {

    private Class<T> mappedClass;

    protected Class<T> getMappedClass() {

        return this.mappedClass;
    }

    protected void setMappedClass(Class<T> mappedClass) {

        this.mappedClass = Objects.requireNonNull(mappedClass, "mapped class");
    }

}