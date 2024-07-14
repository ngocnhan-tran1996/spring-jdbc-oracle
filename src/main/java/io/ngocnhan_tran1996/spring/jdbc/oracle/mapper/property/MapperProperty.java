package io.ngocnhan_tran1996.spring.jdbc.oracle.mapper.property;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

public record MapperProperty(Field field, PropertyDescriptor propertyDescriptor) {

}