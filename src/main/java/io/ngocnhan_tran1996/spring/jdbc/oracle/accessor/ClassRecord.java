package io.ngocnhan_tran1996.spring.jdbc.oracle.accessor;

import java.util.Objects;

public record ClassRecord<T>(Class<T> mappedClass) {

    public ClassRecord {

        Objects.requireNonNull(mappedClass, "mapped class");
    }

}