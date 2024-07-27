package io.spring.jdbc.oracle.dto;

import io.spring.jdbc.oracle.annotation.OracleParameter;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerPojo {

    @OracleParameter("first_name")
    private String name;
    private String lastName;
    private BigDecimal age;

}