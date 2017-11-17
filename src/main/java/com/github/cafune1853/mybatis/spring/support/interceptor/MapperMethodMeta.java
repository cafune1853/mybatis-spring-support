package com.github.cafune1853.mybatis.spring.support.interceptor;

import java.util.Collections;
import java.util.List;

import org.apache.ibatis.mapping.ResultMap;

/**
 * 包含了对mapper方法的解析结果
 */

final class MapperMethodMeta {
	private final Class<?> entityClazz;
	private final boolean appendClazzAsArg;
	private final boolean autoResultMap;
	private final List<ResultMap> resultMaps;
	
	public MapperMethodMeta(Class<?> entityClazz, boolean appendClazzAsArg, boolean autoResultMap, List<ResultMap> resultMaps) {
		this.entityClazz = entityClazz;
		this.appendClazzAsArg = appendClazzAsArg;
		this.autoResultMap = autoResultMap;
		this.resultMaps = Collections.unmodifiableList(resultMaps);
	}
	
	public Class<?> getEntityClazz() {
		return entityClazz;
	}
	
	public boolean isAppendClazzAsArg() {
		return appendClazzAsArg;
	}
	
	public boolean isAutoResultMap() {
		return autoResultMap;
	}
	
	public List<ResultMap> getResultMaps() {
		return resultMaps;
	}
}
