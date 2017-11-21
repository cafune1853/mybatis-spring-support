package com.github.cafune1853.mybatis.spring.support.annotation;

import com.github.cafune1853.mybatis.spring.support.interceptor.CurdInterceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于注解在一个Mapper方法上，则会重写该MapperStatement的keyProperties和keyColumns
 * 必须和{@link CurdInterceptor} 一起使用
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SetKeyPropertiesAndColumns {
}