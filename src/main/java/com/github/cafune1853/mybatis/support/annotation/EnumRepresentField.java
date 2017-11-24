package com.github.cafune1853.mybatis.support.annotation;

import com.github.cafune1853.mybatis.support.handler.FieldEnumTypeHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于与{@link FieldEnumTypeHandler}配合使用
 * 注解在一个enum类的一个域上，则该域的值会作为代表该Enum的值被用于数据库映射。
 * 被注解的域只能是数值类型/字符串类型，其他类型会直接报错
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EnumRepresentField {
}
