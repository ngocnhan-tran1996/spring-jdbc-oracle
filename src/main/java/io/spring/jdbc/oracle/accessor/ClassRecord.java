package io.spring.jdbc.oracle.accessor;

import java.util.Objects;

public record ClassRecord<T>(Class<T> mappedClass) {

    public ClassRecord {

        Objects.requireNonNull(mappedClass, "mapped class");
    }

    public boolean isTypeRecord() {

        return Record.class.isAssignableFrom(this.mappedClass);
    }

}