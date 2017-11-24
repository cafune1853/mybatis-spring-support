package com.github.cafune1853.mybatis.support.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 被注解的方法，会从Mapper传递实体类到Provider中（与AppendEntityClass一致），并利用该实体类生成相应的ResultMap,作为该方法的ResultMap.
 * 该类使用JPA注解进行简单结果映射，只支持简单pojo映射，不支持内嵌对象，内嵌数组等。
 * 在进行类映射时，优先使用[@Table]{@link javax.persistence.Table}进行数据库表名的匹配，否则使用lower_underscore来替换大驼峰，
 * 在寻找id元素时，优先使用[@Id]{@link javax.persistence.Id}标记的域，否则转而寻找名为id的域。如果不存在这样的数据则直接报错。
 * 在进行域映射时，使用underScoreToCaseCamel方式进行映射，与mybatis保持一致，避免不一致的映射情况出现。
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AutoResultMap {
}
