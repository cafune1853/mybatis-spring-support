package com.github.cafune1853.mybatis.support.config;

import com.github.cafune1853.mybatis.support.constant.DBType;
import com.github.cafune1853.mybatis.support.interceptor.CurdPaginationInterceptor;

import lombok.Data;

/**
 * @author doggy
 * Created on 2017-11-20.
 * 这个类必须配合{@link CurdPaginationInterceptor} 一起使用
 */
public class DBConfig {
    private static final DBConfig INSTANCE = new DBConfig();
    private volatile DBType dbType = DBType.MYSQL;
    private volatile boolean manualUpdateGmtModified = false;

    private DBConfig() {
    }

    public static DBConfig getInstance() {
        return INSTANCE;
    }

    public static DBConfig configDbType(DBType dbType) {
        INSTANCE.dbType = dbType;
        return INSTANCE;
    }
    
    public static DBConfig configManualUpdateGmtModified(boolean manualUpdateGmtModified){
        INSTANCE.manualUpdateGmtModified = manualUpdateGmtModified;
        return INSTANCE;
    }
    
    public DBType getDbType() {
        return dbType;
    }
    
    public boolean isManualUpdateGmtModified() {
        return manualUpdateGmtModified;
    }
}
