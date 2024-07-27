package io.spring.jdbc.oracle.dto;

import io.spring.jdbc.oracle.annotation.OracleParameter;
import io.spring.jdbc.oracle.annotation.OracleType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComplexCustomerPojo {

    private LocalDateTime birthday;

    @OracleParameter(
        value = "original_address",
        input = @OracleType(structName = "complex_example_pack.address"),
        output = @OracleType(structName = "complex_example_pack.address")
    )
    private Address address;

    @OracleParameter(
        value = "other_address",
        input = @OracleType(structName = "complex_example_pack.address", arrayName = "complex_example_pack.address_array"),
        output = @OracleType(structName = "complex_example_pack.address", arrayName = "complex_example_pack.address_array")
    )
    private Address[] addresses;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address {

        private String district;
        private String city;
        @OracleParameter(
            value = "ages",
            input = @OracleType(arrayName = "complex_example_pack.numbers"),
            output = @OracleType(arrayName = "complex_example_pack.numbers")
        )
        private List<Integer> values;
    }

}