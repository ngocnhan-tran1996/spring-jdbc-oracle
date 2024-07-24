package io.spring.jdbc.oracle;

import io.spring.jdbc.oracle.annotation.OracleParameter;
import io.spring.jdbc.oracle.annotation.OracleType;
import io.spring.jdbc.oracle.converter.OracleConverter;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public record ComplexCustomerRecord(
    @OracleParameter("first_name")
    String name,
    String lastName,
    BigDecimal age,
    @OracleParameter(
        input = @OracleType(converter = LocalDatetimeToTimestampConverter.class),
        output = @OracleType(converter = TimestampToLocalDatetimeConverter.class)
    )
    LocalDateTime birthday,

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

    public record LocalDatetimeToTimestampConverter() implements
        OracleConverter<LocalDateTime, Timestamp> {

        @Override
        public Timestamp convert(LocalDateTime source) {

            return Optional.ofNullable(source)
                .map(Timestamp::valueOf)
                .orElse(null);
        }

    }

    public record TimestampToLocalDatetimeConverter() implements
        OracleConverter<Timestamp, LocalDateTime> {

        @Override
        public LocalDateTime convert(Timestamp source) {

            return Optional.ofNullable(source)
                .map(Timestamp::toLocalDateTime)
                .orElse(null);
        }

    }

}