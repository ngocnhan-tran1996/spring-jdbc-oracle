package io.spring.jdbc.oracle.accessor;

import io.spring.jdbc.oracle.utils.Validators;

public record ClassRecord<T>(Class<T> mappedClass) {

    public ClassRecord {

        Validators.requireNotNull(mappedClass, "Mapped class");
    }

    public boolean isTypeRecord() {

        return Record.class.isAssignableFrom(this.mappedClass);
    }

}