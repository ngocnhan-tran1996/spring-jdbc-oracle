package io.spring.jdbc.oracle;

import io.spring.jdbc.oracle.annotation.OracleParameter;
import io.spring.jdbc.oracle.annotation.OracleType;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class ComplexCustomer extends Customer {

    private Timestamp birthday;

    @OracleParameter(
        value = "original_address",
        input = @OracleType(structName = "complex_example_pack.address"),
        output = @OracleType(structName = "complex_example_pack.address")
    )
    private Address address;

    @OracleParameter(
        value = "other_addresses",
        input = @OracleType(structName = "complex_example_pack.address", arrayName = "complex_example_pack.addresses"),
        output = @OracleType(structName = "complex_example_pack.address", arrayName = "complex_example_pack.addresses")
    )
    private Address[] addresses;

    public ComplexCustomer() {

    }

    public ComplexCustomer(
        String name,
        String lastName,
        BigDecimal age,
        Timestamp birthday,
        Address address,
        Address[] addresses) {

        super(name, lastName, age);
        this.birthday = birthday;
        this.address = address;
        this.addresses = addresses;
    }

    public Timestamp getBirthday() {
        return birthday;
    }

    public void setBirthday(Timestamp birthday) {
        this.birthday = birthday;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Address[] getAddresses() {
        return addresses;
    }

    public void setAddresses(Address[] addresses) {
        this.addresses = addresses;
    }

    public static class Address {

        private String district;
        private String city;
        @OracleParameter(
            value = "my_values",
            input = @OracleType(arrayName = "complex_example_pack.numbers"),
            output = @OracleType(arrayName = "complex_example_pack.numbers")
        )
        private List<String> values;

        public Address() {
        }

        public Address(String district, String city, List<String> values) {
            this.district = district;
            this.city = city;
            this.values = values;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public List<String> getValues() {
            return values;
        }

        public void setValues(List<String> values) {
            this.values = values;
        }

    }

}