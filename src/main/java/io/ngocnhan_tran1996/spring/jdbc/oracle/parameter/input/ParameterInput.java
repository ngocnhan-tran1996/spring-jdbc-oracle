package io.ngocnhan_tran1996.spring.jdbc.oracle.parameter.input;

import static io.ngocnhan_tran1996.spring.jdbc.oracle.utils.Strings.NOT_NULL;

import io.ngocnhan_tran1996.spring.jdbc.oracle.accessor.ParameterAccessor;
import io.ngocnhan_tran1996.spring.jdbc.oracle.exception.ValueException;
import io.ngocnhan_tran1996.spring.jdbc.oracle.mapper.DelegateMapper;
import io.ngocnhan_tran1996.spring.jdbc.oracle.parameter.output.ParameterOutput;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.jdbc.core.SqlInOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnType;
import org.springframework.jdbc.core.SqlTypeValue;

public final class ParameterInput<T> extends ParameterAccessor<T> {

    private final DelegateMapper<T> mapper;
    private Collection<T> values;

    private ParameterInput(String parameterName, Class<T> mappedClass) {

        super(parameterName, mappedClass);
        this.mapper = DelegateMapper.newInstance(mappedClass);
    }

    public static ParameterInput<Object> withParameterName(String parameterName) {

        return withParameterName(parameterName, Object.class);
    }

    public static <T> ParameterInput<T> withParameterName(
        String parameterName,
        Class<T> mappedClass) {

        return new ParameterInput<>(parameterName, mappedClass);
    }

    @SafeVarargs
    public final ParameterTypeValue withValues(T... values) {

        this.values = Optional.ofNullable(values)
            .stream()
            .flatMap(Arrays::stream)
            .toList();
        return new ParameterTypeValue();
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
        private SqlReturnType returnType;

        private ParameterTypeValue() {
        }

        public ParameterTypeValue withArray(String arrayTypeName) {

            this.typeValue = new ArrayTypeValue<>(arrayTypeName, values);
            this.type = Types.ARRAY;
            this.returnType = ParameterOutput.withParameterName(
                    getParameterName(),
                    getMappedClass()
                )
                .withArray(arrayTypeName)
                .sqlReturnType();
            return this;
        }

        public ParameterTypeValue withStruct(String structTypeName) {

            var value = values == null || values.isEmpty()
                ? null
                : new ArrayList<>(values).getFirst();
            this.typeValue = new StructTypeValue<>(
                structTypeName,
                value,
                mapper.get()
            );
            this.type = Types.STRUCT;
            this.returnType = ParameterOutput.withParameterName(
                    getParameterName(),
                    getMappedClass()
                )
                .withStruct(structTypeName)
                .sqlReturnType();
            return this;
        }

        public ParameterTypeValue withStructArray(String arrayTypeName, String structTypeName) {

            this.typeValue = new StructArrayTypeValue<>(
                arrayTypeName,
                values,
                structTypeName,
                mapper.get()
            );
            this.type = Types.ARRAY;
            this.returnType = ParameterOutput.withParameterName(
                    getParameterName(),
                    getMappedClass()
                )
                .withStructArray(arrayTypeName)
                .sqlReturnType();
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
                this.returnType
            );
        }

        private void validateTypeValue() {

            if (this.type == null || this.typeValue == null) {

                throw new ValueException(NOT_NULL.formatted("typeValue"));
            }

        }

    }

}