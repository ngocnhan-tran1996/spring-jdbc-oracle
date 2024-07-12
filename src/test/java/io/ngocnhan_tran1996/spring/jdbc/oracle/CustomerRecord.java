package io.ngocnhan_tran1996.spring.jdbc.oracle;

import io.ngocnhan_tran1996.spring.jdbc.oracle.annotation.OracleParameter;
import java.math.BigDecimal;

public record CustomerRecord(
    @OracleParameter("first_name")
    String name,
    String lastName,
    BigDecimal age
) {

}