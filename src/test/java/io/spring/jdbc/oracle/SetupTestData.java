package io.spring.jdbc.oracle;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

@TestInstance(Lifecycle.PER_CLASS)
public abstract class SetupTestData {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Value("classpath:script/example_pack.sql")
    Resource examplePack;

    @Value("classpath:script/complex_example_pack.sql")
    Resource complexExamplePack;

    @BeforeAll
    void init() {

        Stream.of(examplePack, complexExamplePack)
            .map(pack -> {
                try {

                    return pack.getContentAsString(StandardCharsets.UTF_8)
                        .split("/");
                } catch (IOException e) {

                    return null;
                }
            })
            .filter(Objects::nonNull)
            .flatMap(Arrays::stream)
            .forEach(this.jdbcTemplate::update);
    }

}