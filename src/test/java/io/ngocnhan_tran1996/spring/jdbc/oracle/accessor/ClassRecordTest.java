package io.ngocnhan_tran1996.spring.jdbc.oracle.accessor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ClassRecordTest {

    @Test
    void when_class_is_null() {

        assertThatNullPointerException()
            .isThrownBy(() -> new ClassRecord<>(null));
    }

    @Test
    void when_class_is_not_record_type() {

        assertThat(new ClassRecord<>(Object.class).isTypeRecord())
            .isFalse();

        assertThat(new ClassRecord<>(BigDecimal.class).isTypeRecord())
            .isFalse();
    }

    @Test
    void when_class_is_record_type() {

        assertThat(new ClassRecord<>(Record.class).isTypeRecord())
            .isTrue();
    }

}