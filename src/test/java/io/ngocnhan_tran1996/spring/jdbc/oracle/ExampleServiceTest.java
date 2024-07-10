package io.ngocnhan_tran1996.spring.jdbc.oracle;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("local")
@SpringBootTest
class ExampleServiceTest extends SetupTestData {

    @Autowired
    ExampleService exampleService;

    @Test
    void testCallExamplePack() {

        // arrange
        var outNumbers = new BigDecimal[]{BigDecimal.ONE, BigDecimal.TWO};
        var outCustomer = new Customer("Nhan", "Tran", BigDecimal.ONE);
        var outCustomers = new Customer[]{
            new Customer("Tran", "Nhan", BigDecimal.TWO),
            new Customer("Nhan", "Tran", BigDecimal.TEN)
        };

        var expectOutput = new HashMap<String, Object>();
        expectOutput.put("IN_OUT_NUMBERS", outNumbers);
        expectOutput.put("OUT_NUMBERS", outNumbers);

        expectOutput.put("IN_OUT_CUSTOMER", outCustomer);
        expectOutput.put("OUT_CUSTOMER", outCustomer);

        expectOutput.put("IN_OUT_CUSTOMERS", outCustomers);
        expectOutput.put("OUT_CUSTOMERS", outCustomers);

        // assert
        assertThat(exampleService.callExamplePack())
            .usingRecursiveComparison()
            .isEqualTo(expectOutput);
    }

}