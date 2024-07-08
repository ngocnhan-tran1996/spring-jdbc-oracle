package io.ngocnhan_tran1996.spring.jdbc.oracle;

public interface Self<T> {

    @SuppressWarnings("unchecked")
    default T self() {

        return (T) this;
    }

}