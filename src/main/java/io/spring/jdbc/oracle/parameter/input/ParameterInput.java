package io.spring.jdbc.oracle.parameter.input;

import io.spring.jdbc.oracle.accessor.ParameterAccessor;
import io.spring.jdbc.oracle.mapper.DelegateMapper;
import io.spring.jdbc.oracle.mapper.Mapper;
import io.spring.jdbc.oracle.parameter.output.ParameterOutput;
import io.spring.jdbc.oracle.utils.Validators;
import java.sql.Connection;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.jdbc.core.SqlInOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnType;

public final class ParameterInput<T> extends ParameterAccessor<T> {

    private final Mapper mapper;
    private Collection<T> values;

    private ParameterInput(String parameterName, Class<T> mappedClass) {

        super(parameterName, mappedClass);
        this.mapper = DelegateMapper.newInstance(mappedClass).get();
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
            .map(Arrays::stream)
            .map(Stream::toList)
            .orElse(null);
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
                mapper
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
                mapper
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
            map.put(getParameterName(), this.getTypeValue().orElse(null));
            return map;
        }

        public Object convert(Connection connection) {

            try {

                var value = this.getTypeValue();
                if (value.isPresent()) {

                    var sqlTypeValue = (AbstractTypeValue) value.get();
                    return sqlTypeValue.createTypeValue(connection, sqlTypeValue.getTypeName());
                }

                return null;
            } catch (Exception ex) {

                return null;
            }

        }

        public SqlParameter sqlParameter() {

            return new SqlParameter(
                getParameterName(),
                this.getType(),
                this.typeValue.getTypeName()
            );
        }

        public SqlInOutParameter sqlInOutParameter() {

            return new SqlInOutParameter(
                getParameterName(),
                this.getType(),
                this.typeValue.getTypeName(),
                this.returnType
            );
        }

        private Optional<Object> getTypeValue() {

            return Optional.ofNullable(values)
                .map(v -> this.typeValue);
        }

        private int getType() {

            return Validators.requireNotNull(this.type, "type");
        }

    }

}