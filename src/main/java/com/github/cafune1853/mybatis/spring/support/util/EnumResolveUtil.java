package com.github.cafune1853.mybatis.spring.support.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.github.cafune1853.mybatis.spring.support.annotation.EnumRepresentField;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EnumResolveUtil {
    private static final Map<Class<? extends Enum>, EnumResolveData> CACHED;
    private static final Set<Class<?>> LEGAL_REPRESENT_FIELD_TYPE;
    static {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(String.class);
        classes.add(Long.class);
        classes.add(long.class);
        classes.add(Integer.class);
        classes.add(int.class);
        classes.add(Short.class);
        classes.add(short.class);
        CACHED = new ConcurrentHashMap<>();
        LEGAL_REPRESENT_FIELD_TYPE = Collections.unmodifiableSet(classes);
    }

    public static boolean hasRepresentField(Class<? extends Enum> enumClazz) {
        boolean result = getEnumResolveData(enumClazz).isHasRepresentField();
        if (!result) {
            log.warn("You use FieldEnumTypeHandler as EnumHandler but don't has a field annotated with EnumRepresentField in {}, so fallback to EnumTypeHandler", enumClazz);
        }
        return result;
    }

    public static <E extends Enum<E>> E getEnumByRepresentFieldValue(Class<E> enumClazz, Object representFieldValue) {
        EnumResolveData enumResolveData = getEnumResolveData(enumClazz);
        if (!enumResolveData.isHasRepresentField()) {
            throw new IllegalStateException(String.format("Enum class:%s should have a field annotated with EnumRepresentField.", enumClazz));
        }
        for (Object enumValue : enumResolveData.getEnumValues()) {
            try {
                if (enumResolveData.getRepresentField().get(enumValue).equals(representFieldValue)) {
                    @SuppressWarnings("unchecked")
                    E retValue = (E) enumValue;
                    return retValue;
                }
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        return null;
    }

    public static <E extends Enum<E>> RepresentFieldTypeValue getRepresentFieldTypeValue(Class<E> enumClazz, E enumValue) {
        EnumResolveData enumResolveData = getEnumResolveData(enumClazz);
        if (!enumResolveData.isHasRepresentField()) {
            throw new IllegalStateException(String.format("Enum class:%s should have a field annotated with EnumRepresentField.", enumClazz));
        }
        try {
            Field field = enumResolveData.getRepresentField();
            return new RepresentFieldTypeValue(field.get(enumValue), enumResolveData.getRepresentFieldClass());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static EnumResolveData getEnumResolveData(Class<? extends Enum> clazz) {
        return CACHED.computeIfAbsent(clazz, clz -> {
            Optional<Field> optionalField = getEnumRepresentField(clz);
            if (optionalField.isPresent()) {
                Field representField = optionalField.get();
                if ((representField.getModifiers() & Modifier.STATIC) != 0) {
                    throw new IllegalStateException(String.format("Enum class:%s, EnumRepresentField should annotated at non static field.", clazz));
                }
                if (!LEGAL_REPRESENT_FIELD_TYPE.contains(representField.getType())) {
                    throw new IllegalStateException(String.format("Enum class:%s, EnumRepresentField should annotated at prop(short/int/long/String) type field.", clazz));
                }
                return new EnumResolveData(true, representField, representField.getType(), getEnumObjects(clazz));
            } else {
                return new EnumResolveData(false, null, null, null);
            }
        });
    }

    private static Object[] getEnumObjects(Class<? extends Enum> clazz) {
        try {
            Method method = clazz.getMethod("values");
            return (Object[]) method.invoke(null);
        } catch (Exception e) {
            throw new IllegalStateException(String.format("Enum class:%s should have static method values.", clazz));
        }
    }

    private static Optional<Field> getEnumRepresentField(Class<? extends Enum> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(EnumRepresentField.class)) {
                field.setAccessible(true);
                return Optional.of(field);
            }
        }
        return Optional.empty();
    }

    private static class EnumResolveData {
        private final boolean hasRepresentField;
        private final Field representField;
        private final Class<?> representFieldClass;
        private final Object[] enumValues;

        EnumResolveData(boolean hasRepresentField, Field representField, Class<?> representFieldClass, Object[] enumValues) {
            this.hasRepresentField = hasRepresentField;
            this.representField = representField;
            this.enumValues = enumValues;
            this.representFieldClass = representFieldClass;
        }

        private boolean isHasRepresentField() {
            return hasRepresentField;
        }

        private Field getRepresentField() {
            return representField;
        }

        private Object[] getEnumValues() {
            return enumValues;
        }

        private Class<?> getRepresentFieldClass() {
            return representFieldClass;
        }
    }
}
