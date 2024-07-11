package io.ngocnhan_tran1996.spring.jdbc.oracle.service;

import io.ngocnhan_tran1996.spring.jdbc.oracle.annotation.OracleParameter;
import java.math.BigDecimal;

record CustomerRecord(
    @OracleParameter("first_name")
    String name,
    String lastName,
    BigDecimal age
) {

}