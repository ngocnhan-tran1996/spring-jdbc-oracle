package io.ngocnhan_tran1996.spring.jdbc.oracle.mapper;

import static io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Matchers.not;

import io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Strings;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.Map;
import oracle.jdbc.OracleDatabaseMetaData;
import oracle.jdbc.OracleTypeMetaData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.LinkedCaseInsensitiveMap;

abstract class AbstractMapper implements Mapper {

    protected final Log log = LogFactory.getLog(this.getClass());

    @Override
    public <T> Struct toStruct(Connection connection, String typeName, T source) {

        try {

            var rsmd = this.getResultSetMetaData(connection, typeName);
            Object[] objects = this.toStruct(
                connection,
                rsmd.getColumnCount(),
                this.extractIndexByColumnName(rsmd),
                source
            );

            return connection.createStruct(typeName, objects);
        } catch (Exception ex) {

            String className = source == null
                ? null
                : source.getClass().getName();
            this.log.error("Could not convert class %s to STRUCT".formatted(className), ex);
            return null;
        }
    }

    @Override
    public <T> T fromStruct(Connection connection, Struct struct) {

        try {

            Object[] values = struct.getAttributes();
            var valueByName = new LinkedCaseInsensitiveMap<>(values.length);

            var rsmd = this.getResultSetMetaData(connection, struct.getSQLTypeName());
            this.extractIndexByColumnName(rsmd)
                .forEach((columnName, index) -> valueByName.put(columnName, values[index]));

            return this.constructInstance(connection, valueByName);
        } catch (Exception ex) {

            this.log.error("Can not convert struct to object", ex);
            return null;
        }
    }

    @Override
    public <T> T convert(Map<String, Object> source) {

        if (source == null) {

            return null;
        }

        var valueByName = new LinkedCaseInsensitiveMap<>();
        source.forEach((columnName, value) -> {

            valueByName.put(columnName, value);

            String propertyName = JdbcUtils.convertUnderscoreNameToPropertyName(columnName);
            if (not(Strings.equalsIgnoreCase(columnName, propertyName))) {

                valueByName.put(propertyName, value);
            }

        });

        return this.constructInstance(null, valueByName);
    }

    protected abstract <T> Object[] toStruct(
        Connection connection,
        int columns,
        Map<String, Integer> columnNameByIndex,
        T source
    );

    protected abstract <T> T constructInstance(
        Connection connection,
        Map<String, Object> valueByName
    );

    ResultSetMetaData getResultSetMetaData(Connection connection, String typeName)
        throws SQLException {

        if (connection.getMetaData() instanceof OracleDatabaseMetaData oracleDatabaseMetaData) {

            var oracleTypeMetaData = oracleDatabaseMetaData.getOracleTypeMetaData(typeName);
            return ((OracleTypeMetaData.Struct) oracleTypeMetaData).getMetaData();
        }

        var oracleTypeMetaData = connection.getMetaData()
            .unwrap(OracleDatabaseMetaData.class)
            .getOracleTypeMetaData(typeName);
        return ((OracleTypeMetaData.Struct) oracleTypeMetaData).getMetaData();
    }

    Map<String, Integer> extractIndexByColumnName(ResultSetMetaData rsmd) throws SQLException {

        Map<String, Integer> indexByColumnName = new LinkedCaseInsensitiveMap<>();

        var columns = rsmd.getColumnCount();
        for (int index = 0; index < columns; index++) {

            String columnName = JdbcUtils.lookupColumnName(rsmd, index + 1);
            indexByColumnName.put(columnName, index);

            String propertyName = JdbcUtils.convertUnderscoreNameToPropertyName(columnName);
            if (not(Strings.equalsIgnoreCase(columnName, propertyName))) {

                indexByColumnName.put(propertyName, index);
            }
        }

        return indexByColumnName;
    }

}