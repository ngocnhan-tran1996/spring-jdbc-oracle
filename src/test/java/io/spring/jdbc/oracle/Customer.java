package io.spring.jdbc.oracle;

import io.spring.jdbc.oracle.annotation.OracleParameter;
import java.math.BigDecimal;

public class Customer {

    @OracleParameter("first_name")
    private String name;
    private String lastName;
    private BigDecimal age;

    public Customer() {
    }

    public Customer(String name, String lastName, BigDecimal age) {
        this.name = name;
        this.lastName = lastName;
        this.age = age;
    }

    public String getName() {

        return this.name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getLastName() {

        return this.lastName;
    }

    public void setLastName(String lastName) {

        this.lastName = lastName;
    }

    public BigDecimal getAge() {

        return this.age;
    }

    public void setAge(BigDecimal age) {

        this.age = age;
    }

}