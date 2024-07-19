package io.ngocnhan_tran1996.spring.jdbc.oracle.annotation;

import io.ngocnhan_tran1996.spring.jdbc.oracle.converter.OracleConverter;
import io.ngocnhan_tran1996.spring.jdbc.oracle.converter.support.NoneConverter;
import io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Strings;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OracleType {

    String structName() default Strings.EMPTY;

    String arrayName() default Strings.EMPTY;

    Class<? extends OracleConverter> converter() default NoneConverter.class;

}