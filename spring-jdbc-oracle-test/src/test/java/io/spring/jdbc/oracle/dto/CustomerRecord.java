package io.spring.jdbc.oracle.dto;

import io.spring.jdbc.oracle.annotation.OracleParameter;
import java.math.BigDecimal;

public record CustomerRecord(
    @OracleParameter("first_name")
    String name,
    String lastName,
    BigDecimal age
) {

}