package io.spring.jdbc.oracle;

import io.spring.jdbc.oracle.annotation.OracleParameter;
import io.spring.jdbc.oracle.annotation.OracleType;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public record ComplexCustomerRecord(
    @OracleParameter("first_name")
    String name,
    String lastName,
    BigDecimal age,
    Timestamp birthday,

    @OracleParameter(
        value = "original_address",
        input = @OracleType(structName = "complex_example_pack.address"),
        output = @OracleType(structName = "complex_example_pack.address")
    )
    Address address,

    @OracleParameter(
        value = "other_addresses",
        input = @OracleType(structName = "complex_example_pack.address", arrayName = "complex_example_pack.addresses"),
        output = @OracleType(structName = "complex_example_pack.address", arrayName = "complex_example_pack.addresses")
    )
    List<Address> addresses
) {


    public record Address(
        String district,
        String city,
        @OracleParameter(
            value = "my_values",
            input = @OracleType(arrayName = "complex_example_pack.numbers"),
            output = @OracleType(arrayName = "complex_example_pack.numbers")
        )
        List<String> values
    ) {

    }

}