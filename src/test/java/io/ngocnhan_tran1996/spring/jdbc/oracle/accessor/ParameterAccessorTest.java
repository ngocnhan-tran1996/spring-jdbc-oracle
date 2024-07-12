package io.ngocnhan_tran1996.spring.jdbc.oracle.accessor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import io.ngocnhan_tran1996.spring.jdbc.oracle.exception.ValueException;
import org.junit.jupiter.api.Test;

class ParameterAccessorTest {

    @Test
    void when_parameterName_is_blank() {

        assertThatExceptionOfType(ValueException.class)
            .isThrownBy(() -> new ParameterAccessorImpl<>(null, Record.class));

        assertThatExceptionOfType(ValueException.class)
            .isThrownBy(() -> new ParameterAccessorImpl<>("", Record.class));

        assertThatExceptionOfType(ValueException.class)
            .isThrownBy(() -> new ParameterAccessorImpl<>("       ", Record.class));
    }

    @Test
    void when_class_is_not_null() {

        // arrange
        var x = "X";
        var parameterName = "X";
        var clazz = Object.class;

        // assert
        var output = new ParameterAccessorImpl<>(x, Object.class);
        assertThat(output.getParameterName())
            .isEqualTo(parameterName);
        assertThat(output.getMappedClass())
            .isEqualTo(clazz);
    }

    @Test
    void when_class_is_null() {

        assertThatNullPointerException()
            .isThrownBy(() -> new ParameterAccessorImpl<>("x", null));
    }

    static class ParameterAccessorImpl<T> extends ParameterAccessor<T> {

        public ParameterAccessorImpl(String parameterName, Class<T> mappedClass) {

            super(parameterName, mappedClass);
        }
    }

}