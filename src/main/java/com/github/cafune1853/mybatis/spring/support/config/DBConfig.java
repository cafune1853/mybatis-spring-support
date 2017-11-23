package com.github.cafune1853.mybatis.spring.support.config;

import com.github.cafune1853.mybatis.spring.support.constant.DBType;

import com.github.cafune1853.mybatis.spring.support.interceptor.CurdPaginationInterceptor;
import lombok.Data;

/**
 * @author doggy
 * Created on 2017-11-20.
 * 这个类必须配合{@link CurdPaginationInterceptor} 一起使用
 */
@Data
public class DBConfig {
    private static final DBConfig INSTANCE = new DBConfig();
    private volatile DBType dbType;

    private DBConfig() {
    }

    public static DBConfig getInstance() {
        return INSTANCE;
    }

    public static DBConfig configDbType(DBType dbType) {
        INSTANCE.setDbType(dbType);
        return INSTANCE;
    }
}
