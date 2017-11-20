package com.github.cafune1853.mybatis.spring.support.provider;

import java.lang.reflect.Modifier;
import java.util.Map;

import org.apache.ibatis.jdbc.SQL;

import lombok.extern.slf4j.Slf4j;

/**
 * 批量
 */
@Slf4j
public class BatchProvider {
    public static final String KEY = "value";
    public static final String COLUMN = "column";
    private static final String normalSql = new SQL().toString();
    private static final int ignoreModifier = Modifier.FINAL | Modifier.STATIC | Modifier.VOLATILE | Modifier.TRANSIENT;

    public String batchUpdate(final Map<String, Object> map) throws Exception {
        return null;
    }

    @SuppressWarnings("unchecked")
    public String batchInsert(final Map<String, Object> map) {
        return null;
    }

    public String findBatchIds(final Map<String, Object> parameter) {
        return null;
    }
}
