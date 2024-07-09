package io.ngocnhan_tran1996.spring.jdbc.oracle.mapper;

import static io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Matchers.not;

import io.ngocnhan_tran1996.spring.jdbc.oracle.Parameter;
import io.ngocnhan_tran1996.spring.jdbc.oracle.exception.ValueException;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.Map;
import java.util.Objects;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleDatabaseMetaData;
import oracle.jdbc.OracleTypeMetaData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.LinkedCaseInsensitiveMap;

public abstract class AbstractMapper<T> extends Parameter<T> implements Mapper<T> {

    private static final String MESSAGE = "%s is not struct";

    protected final Log log = LogFactory.getLog(this.getClass());

    @Override
    public Struct toStruct(Connection connection, String typeName, T source) {

        try {

            var oracleTypeMetaData = connection.getMetaData()
                .unwrap(OracleDatabaseMetaData.class)
                .getOracleTypeMetaData(typeName);

            if (oracleTypeMetaData.getKind() != OracleTypeMetaData.Kind.STRUCT) {

                throw new ValueException(MESSAGE.formatted(typeName));
            }

            var rsmd = this.getResultSetMetaData(oracleTypeMetaData);
            Object[] objects = this.createStruct(
                rsmd.getColumnCount(),
                this.extractIndexByColumnName(rsmd),
                source
            );

            return connection.unwrap(OracleConnection.class)
                .createStruct(typeName, objects);
        } catch (Exception ex) {

            String message = source == null
                ? null
                : source.getClass().getName();
            this.log.debug("Expected STRUCT but got '%s'".formatted(message), ex);
            return null;
        }
    }

    @Override
    public T fromStruct(Connection connection, Struct struct) {

        try {

            var typeName = struct.getSQLTypeName();
            var oracleTypeMetaData = connection.getMetaData()
                .unwrap(OracleDatabaseMetaData.class)
                .getOracleTypeMetaData(typeName);

            if (oracleTypeMetaData.getKind() != OracleTypeMetaData.Kind.STRUCT) {

                throw new ValueException(MESSAGE.formatted(typeName));
            }

            Object[] values = struct.getAttributes();
            var valueByName = new LinkedCaseInsensitiveMap<>(values.length);

            var rsmd = this.getResultSetMetaData(oracleTypeMetaData);
            this.extractIndexByColumnName(rsmd)
                .forEach((columnName, index) -> valueByName.put(columnName, values[index]));

            return this.constructInstance(valueByName);
        } catch (SQLException ex) {

            this.log.debug("Can not convert struct to object", ex);
            return null;
        }
    }

    @Override
    public T convert(Map<String, Object> source) {

        if (source == null) {

            return null;
        }

        var valueByName = new LinkedCaseInsensitiveMap<>();
        source.forEach((columnName, value) -> {

            valueByName.put(columnName, value);

            String propertyName = JdbcUtils.convertUnderscoreNameToPropertyName(columnName);
            if (not(Objects.equals(columnName, propertyName))) {

                valueByName.put(propertyName, value);
            }

        });

        return this.constructInstance(valueByName);
    }


    protected abstract Object[] createStruct(
        int columns,
        Map<String, Integer> columnNameByIndex,
        T source
    );

    protected abstract T constructInstance(Map<String, Object> valueByName);

    ResultSetMetaData getResultSetMetaData(OracleTypeMetaData struct) throws SQLException {

        return ((OracleTypeMetaData.Struct) struct).getMetaData();
    }

    Map<String, Integer> extractIndexByColumnName(ResultSetMetaData rsmd) throws SQLException {

        Map<String, Integer> indexByColumnName = new LinkedCaseInsensitiveMap<>();

        var columns = rsmd.getColumnCount();
        for (int index = 0; index < columns; index++) {

            String columnName = JdbcUtils.lookupColumnName(rsmd, index + 1);
            indexByColumnName.put(columnName, index);

            String propertyName = JdbcUtils.convertUnderscoreNameToPropertyName(columnName);
            if (not(Objects.equals(columnName, propertyName))) {

                indexByColumnName.put(propertyName, index);
            }
        }

        return indexByColumnName;
    }

}