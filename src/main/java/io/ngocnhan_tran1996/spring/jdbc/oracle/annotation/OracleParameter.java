package io.ngocnhan_tran1996.spring.jdbc.oracle.annotation;

import io.ngocnhan_tran1996.spring.jdbc.oracle.converter.OracleConverter;
import io.ngocnhan_tran1996.spring.jdbc.oracle.converter.support.NoneConverter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OracleParameter {

    String value();

    Class<? extends OracleConverter> converter() default NoneConverter.class;

}