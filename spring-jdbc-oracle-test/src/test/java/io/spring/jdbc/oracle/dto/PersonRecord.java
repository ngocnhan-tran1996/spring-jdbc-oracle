package io.spring.jdbc.oracle.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PersonRecord(
    String firstName,
    String lastName,
    BigDecimal age,
    LocalDateTime birthDate
) {

}