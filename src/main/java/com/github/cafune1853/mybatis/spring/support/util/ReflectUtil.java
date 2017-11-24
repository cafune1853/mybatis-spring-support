package com.github.cafune1853.mybatis.spring.support.util;

import java.lang.reflect.Field;

import com.github.cafune1853.mybatis.spring.support.exception.ReflectionException;

public class ReflectUtil {
    private ReflectUtil() {
    }

    public static void silentSetFieldValue(Field field, Object target, Object value) {
        try {
            field.set(target, value);
        } catch (IllegalAccessException iae) {
            throw new ReflectionException(iae);
        }
    }

    public static Field silentGetField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException nfe) {
            throw new ReflectionException(nfe);
        }
    }

    public static Field silentGetFieldAndSetAccessible(Class<?> clazz, String fieldName) {
        Field field = silentGetField(clazz, fieldName);
        field.setAccessible(true);
        return field;
    }
}
