package com.github.cafune1853.mybatis.spring.support.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.cafune1853.mybatis.spring.support.interceptor.CurdPaginationInterceptor;

/**
 * 用于注解在一个Mapper方法上，则会重写该MappedStatement的keyProperties和keyColumns
 * 必须和{@link CurdPaginationInterceptor} 一起使用
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SetKeyPropertiesAndColumns {
}
