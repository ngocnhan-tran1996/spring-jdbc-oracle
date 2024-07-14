package io.ngocnhan_tran1996.spring.jdbc.oracle.converter;

@FunctionalInterface
public interface OracleConverter<S, D> {

    D convert(S source);

}