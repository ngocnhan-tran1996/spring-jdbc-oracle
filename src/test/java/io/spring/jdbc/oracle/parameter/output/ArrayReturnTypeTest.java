package io.spring.jdbc.oracle.parameter.output;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ArrayReturnTypeTest {

    @Test
    void convert_struct_always_throw_exception() {

        var returnType = new ArrayReturnType();

        assertThatExceptionOfType(UnsupportedOperationException.class)
            .isThrownBy(() -> returnType.convertStruct(null, null));
    }

}