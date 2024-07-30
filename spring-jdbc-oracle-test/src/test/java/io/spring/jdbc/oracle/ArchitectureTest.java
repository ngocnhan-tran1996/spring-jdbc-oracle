package io.spring.jdbc.oracle;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.lang.ArchRule;
import io.spring.jdbc.oracle.converter.GenericOracleConverter;
import io.spring.jdbc.oracle.converter.OracleConverter;
import io.spring.jdbc.oracle.mapper.Mapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.SqlReturnType;
import org.springframework.jdbc.core.SqlTypeValue;

class ArchitectureTest {

    private static final String BASE_PACKAGE = "io.spring.jdbc.oracle";

    @Test
    void reside_in_a_package_rule() {

        var importedClasses = new ClassFileImporter()
            .withImportOption(new DoNotIncludeTests())
            .importPackages(BASE_PACKAGE);

        ArchRule genericOracleConverterRule = classes()
            .that()
            .implement(GenericOracleConverter.class)
            .should()
            .haveSimpleNameEndingWith(GenericOracleConverter.class.getSimpleName())
            .andShould()
            .resideInAPackage(BASE_PACKAGE + ".converter.support");

        ArchRule oracleConverterRule = classes()
            .that()
            .implement(OracleConverter.class)
            .should()
            .haveSimpleNameEndingWith(OracleConverter.class.getSimpleName())
            .andShould()
            .resideInAPackage(BASE_PACKAGE + ".converter.support");

        ArchRule mapperRule = classes()
            .that()
            .implement(Mapper.class)
            .should()
            .haveSimpleNameEndingWith(Mapper.class.getSimpleName())
            .andShould()
            .resideInAPackage(BASE_PACKAGE + ".mapper");

        ArchRule typeValueRule = classes()
            .that()
            .implement(SqlTypeValue.class)
            .should()
            .haveSimpleNameEndingWith("TypeValue")
            .andShould()
            .resideInAPackage(BASE_PACKAGE + ".parameter.input");

        ArchRule returnTypeRule = classes()
            .that()
            .implement(SqlReturnType.class)
            .should()
            .haveSimpleNameEndingWith("ReturnType")
            .andShould()
            .resideInAPackage(BASE_PACKAGE + ".parameter.output");

        List.of(
                genericOracleConverterRule,
                oracleConverterRule,
                mapperRule,
                typeValueRule,
                returnTypeRule
            )
            .forEach(rule -> rule.check(importedClasses));
    }

}
