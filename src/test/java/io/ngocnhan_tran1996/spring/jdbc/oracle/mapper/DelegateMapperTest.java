package io.ngocnhan_tran1996.spring.jdbc.oracle.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.zaxxer.hikari.HikariDataSource;
import io.ngocnhan_tran1996.spring.jdbc.oracle.Customer;
import io.ngocnhan_tran1996.spring.jdbc.oracle.CustomerRecord;
import io.ngocnhan_tran1996.spring.jdbc.oracle.SetupTestData;
import io.ngocnhan_tran1996.spring.jdbc.oracle.annotation.OracleParameter;
import io.ngocnhan_tran1996.spring.jdbc.oracle.config.ExampleConfig;
import io.ngocnhan_tran1996.spring.jdbc.oracle.exception.ValueException;
import java.math.BigDecimal;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.DataSourceUtils;

@SpringBootTest
@Import(ExampleConfig.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DelegateMapperTest extends SetupTestData {

    @Autowired
    HikariDataSource dataSource;

    @Test
    void when_class_is_empty_record() {

        assertThatExceptionOfType(ValueException.class)
            .isThrownBy(() -> DelegateMapper.newInstance(EmptyRecord.class));
    }

    @Test
    void when_class_is_record() {

        assertThat(DelegateMapper.newInstance(NoDuplicateFieldRecord.class))
            .isNotNull();
    }

    @Test
    void when_class_is_MoreFieldCustomer() {

        assertThat(DelegateMapper.newInstance(MoreFieldCustomer.class))
            .isNotNull();
    }

    @Test
    void when_class_is_record_and_duplicate_field() {

        assertThatExceptionOfType(ValueException.class)
            .isThrownBy(() -> DelegateMapper.newInstance(DuplicateFieldRecord.class));

        assertThatExceptionOfType(ValueException.class)
            .isThrownBy(() -> DelegateMapper.newInstance(DuplicateAnnotationRecord.class));
    }

    @Nested
    class ConvertMethod {

        @Test
        void when_class_is_not_type_record() {

            var mapper = DelegateMapper.newInstance(Customer.class);

            assertThat(mapper.convert(null))
                .isNull();

            var emptyValue = Map.<String, Object>of();
            assertThat(mapper.convert(emptyValue))
                .usingRecursiveComparison()
                .isEqualTo(new Customer());

            var value = Map.<String, Object>of(
                "lastName", "test",
                "first_name", "Nhan",
                "age", BigDecimal.TEN
            );
            assertThat(mapper.convert(value))
                .usingRecursiveComparison()
                .isEqualTo(new Customer("Nhan", "test", BigDecimal.TEN));
        }

        @Test
        void when_class_is_type_record() {

            var mapper = DelegateMapper.newInstance(CustomerRecord.class);

            assertThat(mapper.convert(null))
                .isNull();

            var emptyValue = Map.<String, Object>of();
            assertThat(mapper.convert(emptyValue))
                .usingRecursiveComparison()
                .isEqualTo(new CustomerRecord(null, null, null));

            var value = Map.<String, Object>of(
                "lastName", "Tran",
                "first_name", "Nhan",
                "age", BigDecimal.TEN,
                "missingField", "X"
            );
            assertThat(mapper.convert(value))
                .usingRecursiveComparison()
                .isEqualTo(new CustomerRecord("Nhan", "Tran", BigDecimal.TEN));
        }

    }

    @Nested
    class StructMethod {

        @Test
        void from_struct_to_object_with_oracle_connection() throws SQLException {

            var mapper = DelegateMapper.newInstance(Customer.class);

            var connection = DriverManager.getConnection(
                dataSource.getJdbcUrl(),
                dataSource.getUsername(),
                dataSource.getPassword()
            );

            var struct = connection
                .createStruct(
                    "SYS.EXAMPLE_PACK.CUSTOMER",
                    new Object[]{"Nhan", "Tran", BigDecimal.TEN}
                );

            assertThat(mapper.fromStruct(connection, struct))
                .usingRecursiveComparison()
                .isEqualTo(new Customer("Nhan", "Tran", BigDecimal.TEN));

            DataSourceUtils.releaseConnection(connection, dataSource);
        }

        @Test
        void from_struct_to_object_with_connection() throws SQLException {

            var mapper = DelegateMapper.newInstance(Customer.class);

            var connection = dataSource.getConnection();

            var struct = connection
                .createStruct(
                    "SYS.EXAMPLE_PACK.CUSTOMER",
                    new Object[]{"Nhan", "Tran", BigDecimal.TEN}
                );

            assertThat(mapper.fromStruct(connection, struct))
                .usingRecursiveComparison()
                .isEqualTo(new Customer("Nhan", "Tran", BigDecimal.TEN));

            DataSourceUtils.releaseConnection(connection, dataSource);
        }

        @Test
        void from_struct_to_object_with_attributes_is_empty() throws SQLException {

            var mapper = DelegateMapper.newInstance(Customer.class);

            var connection = dataSource.getConnection();

            var struct = connection
                .createStruct(
                    "SYS.EXAMPLE_PACK.CUSTOMER",
                    new Object[0]
                );

            assertThat(mapper.fromStruct(connection, struct))
                .isNull();

            DataSourceUtils.releaseConnection(connection, dataSource);
        }

        @Test
        void to_struct_from_object_with_oracle_connection() throws SQLException {

            var mapper = DelegateMapper.newInstance(Customer.class);

            var connection = DriverManager.getConnection(
                dataSource.getJdbcUrl(),
                dataSource.getUsername(),
                dataSource.getPassword()
            );

            var struct = connection
                .createStruct(
                    "SYS.EXAMPLE_PACK.CUSTOMER",
                    new Object[]{"Nhan", "Tran", BigDecimal.TEN}
                );

            assertThat(
                mapper.toStruct(
                    connection,
                    "SYS.EXAMPLE_PACK.CUSTOMER",
                    new Customer("Nhan", "Tran", BigDecimal.TEN)
                )
            )
                .usingRecursiveComparison()
                .isEqualTo(struct);

            DataSourceUtils.releaseConnection(connection, dataSource);
        }

        @Test
        void to_struct_from_object_is_MoreFieldCustomer_with_connection() throws SQLException {

            var mapper = DelegateMapper.newInstance(MoreFieldCustomer.class);

            var connection = dataSource.getConnection();

            var struct = connection
                .createStruct(
                    "SYS.EXAMPLE_PACK.CUSTOMER",
                    new Object[]{"Nhan", "Tran", BigDecimal.TEN}
                );

            assertThat(
                mapper.toStruct(
                    connection,
                    "SYS.EXAMPLE_PACK.CUSTOMER",
                    new MoreFieldCustomer("Nhan", "Tran", BigDecimal.TEN)
                )
            )
                .usingRecursiveComparison()
                .isEqualTo(struct);

            DataSourceUtils.releaseConnection(connection, dataSource);
        }

        @Test
        void to_struct_from_object_with_connection_is_null() {

            var mapper = DelegateMapper.newInstance(Customer.class);

            assertThat(
                mapper.toStruct(
                    null,
                    "SYS.EXAMPLE_PACK.CUSTOMER",
                    new Customer("Nhan", "Tran", BigDecimal.TEN)
                )
            )
                .isNull();
        }

        @Test
        void to_struct_from_object_with_connection_is_null_and_object_is_null() {

            var mapper = DelegateMapper.newInstance(Customer.class);

            assertThat(
                mapper.toStruct(
                    null,
                    "SYS.EXAMPLE_PACK.CUSTOMER",
                    null
                )
            )
                .isNull();
        }

    }

}

class MoreFieldCustomer extends Customer {

    private BigDecimal missingAccessorField;
    private String noSetField;
    private String noGetField;

    public MoreFieldCustomer() {
    }

    public MoreFieldCustomer(String name, String lastName, BigDecimal age) {

        super(name, lastName, age);
    }

    public String getNoSetField() {

        return this.noSetField;
    }

    public void setNoGetField(String noGetField) {

        this.noGetField = noGetField;
    }

}

record EmptyRecord() {

}

record DuplicateFieldRecord(
    @OracleParameter("first_name")
    String name,
    String first_name,
    BigDecimal age
) {

}

record DuplicateAnnotationRecord(
    @OracleParameter("first_name")
    String name,
    @OracleParameter("first_name")
    String lastName,
    BigDecimal age
) {

}

record NoDuplicateFieldRecord(
    @OracleParameter("first_name")
    String name,
    @OracleParameter("last_name")
    String first_name,
    BigDecimal age
) {

}