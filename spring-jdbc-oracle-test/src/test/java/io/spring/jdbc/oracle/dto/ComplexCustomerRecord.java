package io.spring.jdbc.oracle.dto;

import io.spring.jdbc.oracle.annotation.OracleParameter;
import io.spring.jdbc.oracle.annotation.OracleType;
import java.sql.Timestamp;
import java.util.List;

public record ComplexCustomerRecord(

    Timestamp birthday,

    @OracleParameter(
        value = "original_address",
        input = @OracleType(structName = "complex_example_pack.address"),
        output = @OracleType(structName = "complex_example_pack.address")
    )
    Address address,

    @OracleParameter(
        value = "other_address",
        input = @OracleType(structName = "complex_example_pack.address", arrayName = "complex_example_pack.address_array"),
        output = @OracleType(structName = "complex_example_pack.address", arrayName = "complex_example_pack.address_array")
    )
    List<Address> addresses
) {

    public record Address(
        String district,
        String city,
        @OracleParameter(
            value = "ages",
            input = @OracleType(arrayName = "complex_example_pack.numbers"),
            output = @OracleType(arrayName = "complex_example_pack.numbers")
        )
        String[] values
    ) {


    }

}