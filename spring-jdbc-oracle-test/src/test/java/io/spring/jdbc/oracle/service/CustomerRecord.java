package io.spring.jdbc.oracle.service;

import io.spring.jdbc.oracle.annotation.OracleParameter;
import java.math.BigDecimal;

public record CustomerRecord(
    @OracleParameter("first_name")
    String name,
    String lastName,
    BigDecimal age
) {

}