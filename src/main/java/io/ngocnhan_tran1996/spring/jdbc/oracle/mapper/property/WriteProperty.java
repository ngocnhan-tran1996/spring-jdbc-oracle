package io.ngocnhan_tran1996.spring.jdbc.oracle.mapper.property;

import java.lang.reflect.Method;

public record WriteProperty(String propertyName, String fieldName, Method convertMethod) {

}