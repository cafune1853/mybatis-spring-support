package com.github.cafune1853.mybatis.spring.support.interceptor;

import java.util.Collections;
import java.util.List;

import lombok.Getter;
import org.apache.ibatis.mapping.ResultMap;

/**
 * 包含了对mapper方法的解析结果
 */
@Getter
final class MapperMethodMeta {
	private final Class<?> entityClazz;
	private final boolean appendClazzAsArg;
	private final boolean autoResultMap;
	private final List<ResultMap> resultMaps;
	private final boolean setKeyPropertiesAndColumns;
	private final String keyProperty;
	private final String keyColumn;
	
	MapperMethodMeta(Class<?> entityClazz, boolean appendClazzAsArg, boolean autoResultMap, List<ResultMap> resultMaps, boolean setKeyPropertiesAndColumns, String keyProperty, String keyColumn) {
		this.entityClazz = entityClazz;
		this.appendClazzAsArg = appendClazzAsArg;
		this.autoResultMap = autoResultMap;
		this.resultMaps = Collections.unmodifiableList(resultMaps);
		this.setKeyPropertiesAndColumns = setKeyPropertiesAndColumns;
		this.keyProperty = keyProperty;
		this.keyColumn = keyColumn;
	}
}
