package com.github.cafune1853.mybatis.spring.support.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 是否将实体类从Mapper方法传递到相应的Provider中，具体的实体解析规则见{@link AutoResultMap}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AppendEntityClass {
}
