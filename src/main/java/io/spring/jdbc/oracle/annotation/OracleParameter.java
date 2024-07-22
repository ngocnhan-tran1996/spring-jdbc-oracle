package io.spring.jdbc.oracle.annotation;

import io.spring.jdbc.oracle.utils.Strings;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OracleParameter {

    String value() default Strings.EMPTY;

    OracleType input() default @OracleType;

    OracleType output() default @OracleType;

}