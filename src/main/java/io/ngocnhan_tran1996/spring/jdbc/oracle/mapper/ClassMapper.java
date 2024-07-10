package io.ngocnhan_tran1996.spring.jdbc.oracle.mapper;

import java.util.Objects;

public abstract class ClassMapper<T> extends AbstractMapper<T> {

    private Class<T> mappedClass;

    public Class<T> getMappedClass() {

        return this.mappedClass;
    }

    public void setMappedClass(Class<T> mappedClass) {

        this.mappedClass = Objects.requireNonNull(mappedClass, "mapped class");
    }

}