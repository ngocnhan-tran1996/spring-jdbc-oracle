package io.ngocnhan_tran1996.spring.jdbc.oracle.mapper;

import static io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Matchers.not;

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

public abstract class AbstractMapper<T> implements Mapper<T> {

    protected final Log log = LogFactory.getLog(this.getClass());

    @Override
    public Struct toStruct(Connection connection, String typeName, T source) {

        try {

            var oracleTypeMetaData = connection.getMetaData()
                .unwrap(OracleDatabaseMetaData.class)
                .getOracleTypeMetaData(typeName);

            if (oracleTypeMetaData.getKind() != OracleTypeMetaData.Kind.STRUCT) {

                throw new ValueException("%s is not struct".formatted(typeName));
            }

            var rsmd = this.getResultSetMetaData(oracleTypeMetaData);
            Object[] objects = this.createStruct(
                rsmd.getColumnCount(),
                this.extractIndexByColumnName(rsmd)
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

        // TODO add logic
        return null;
    }

    protected abstract Object[] createStruct(int columns, Map<String, Integer> columnNameByIndex);

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