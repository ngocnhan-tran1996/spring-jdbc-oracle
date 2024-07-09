package io.ngocnhan_tran1996.spring.jdbc.oracle.parameter.input;

import static io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Strings.NOT_NULL;

import io.ngocnhan_tran1996.spring.jdbc.oracle.Parameter;
import io.ngocnhan_tran1996.spring.jdbc.oracle.exception.ValueException;
import io.ngocnhan_tran1996.spring.jdbc.oracle.mapper.BeanPropertyMapper;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.SqlInOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlTypeValue;

public final class ParameterInput<T> extends Parameter<T> {

    private Collection<T> values;

    private ParameterInput(String parameterName, Class<T> mappedClass) {

        super.setParameterName(parameterName);
        super.setMappedClass(mappedClass);
    }

    public static <T> ParameterInput<T> withParameterName(String parameterName) {

        return withParameterName(parameterName, null);
    }

    public static <T> ParameterInput<T> withParameterName(
        String parameterName,
        Class<T> mappedClass) {

        return new ParameterInput<>(parameterName, mappedClass);
    }

    public ParameterTypeValue withValues(Collection<T> values) {

        this.values = values;
        return new ParameterTypeValue();
    }

    public ParameterTypeValue withValue(T value) {

        this.values = value == null
            ? null
            : List.of(value);
        return new ParameterTypeValue();
    }

    public final class ParameterTypeValue {

        private AbstractTypeValue typeValue;
        private Integer type;

        private ParameterTypeValue() {
        }

        public ParameterTypeValue withArray(String arrayTypeName) {

            this.typeValue = new ArrayTypeValue<>(arrayTypeName, values);
            this.type = Types.ARRAY;
            return this;
        }

        public ParameterTypeValue withStruct(String structTypeName) {

            var value = values == null || values.isEmpty()
                ? null
                : new ArrayList<>(values).getFirst();
            this.typeValue = new StructTypeValue<>(
                structTypeName,
                value,
                BeanPropertyMapper.newInstance(getMappedClass())
            );
            this.type = Types.STRUCT;
            return this;
        }

        public ParameterTypeValue withStructArray(String arrayTypeName, String structTypeName) {

            this.typeValue = new StructArrayTypeValue<>(
                arrayTypeName,
                values,
                structTypeName,
                BeanPropertyMapper.newInstance(getMappedClass())
            );
            this.type = Types.ARRAY;
            return this;
        }

        public Map<String, Object> toMap() {

            var map = new HashMap<String, Object>();
            map.put(getParameterName(), this.sqlTypeValue());
            return map;
        }

        public SqlTypeValue sqlTypeValue() {

            return this.typeValue;
        }

        public SqlParameter sqlParameter() {

            this.validateTypeValue();
            return new SqlParameter(
                getParameterName(),
                this.type,
                this.typeValue.getTypeName()
            );
        }

        public SqlInOutParameter sqlInOutParameter() {

            this.validateTypeValue();
            return new SqlInOutParameter(
                getParameterName(),
                this.type,
                this.typeValue.getTypeName(),
                // FIXME add logic
                null
            );
        }

        private void validateTypeValue() {

            if (this.type == null || this.typeValue == null) {

                throw new ValueException(NOT_NULL.formatted("Type"));
            }

        }

    }

}