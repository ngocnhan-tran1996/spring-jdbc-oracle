package io.spring.jdbc.oracle.service;

import io.spring.jdbc.oracle.dto.PersonRecord;
import io.spring.jdbc.oracle.parameter.input.ParameterInput;
import io.spring.jdbc.oracle.parameter.output.ParameterOutput;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class PersonExampleRepository {

    private static final String PERSON = "person_obj";
    private static final String PERSONS = "person_example_pack.persons";

    private final JdbcTemplate jdbcTemplate;

    Map<String, Object> callExamplePack() {

        this.jdbcTemplate.setResultsMapCaseInsensitive(true);

        // in
        var inPerson = ParameterInput.withParameterName(
                "in_person",
                PersonRecord.class
            )
            .withValue(null)
            .withStruct(PERSON);
        var inPersons = ParameterInput.withParameterName(
                "in_persons",
                PersonRecord.class
            )
            .withValues((List<PersonRecord>) null)
            .withStructArray(PERSONS, PERSON);

        // out
        var outPerson = ParameterOutput.withParameterName(
                "out_person",
                PersonRecord.class
            )
            .withStruct(PERSON);
        var outPersons = ParameterOutput.withParameterName(
                "out_persons",
                PersonRecord.class
            )
            .withStructArray(PERSONS);

        var simpleJdbcCall = new SimpleJdbcCall(this.jdbcTemplate)
            .withCatalogName("person_example_pack")
            .withProcedureName("EXAMPLE_PROC")
            .declareParameters(
                inPerson.sqlParameter(),
                inPersons.sqlParameter(),

                outPerson.sqlOutParameter(),
                outPersons.sqlOutParameter()
            );

        var sqlParameterSource = new MapSqlParameterSource()
            .addValues(inPerson.toMap())
            .addValues(inPersons.toMap());

        return simpleJdbcCall.execute(sqlParameterSource);
    }

}