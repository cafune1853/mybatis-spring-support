package com.github.cafune1853.mybatis.support.constant;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.github.cafune1853.mybatis.support.util.StringUtil;

import lombok.Getter;

/**
 * @author doggy
 * Created on 2017-11-20.
 */
@Getter
public enum DBType {
    MYSQL("mysql", '`', '`') {
        @Override
        public String appendPagination(String oldSql, int pageNo, int pageSize) {
            return oldSql + " limit " + (pageNo - 1) * pageSize + ", " + pageSize;
        }
    },
    POSTGRESQL("postgresql", '"', '"') {
        /**
         * 数据量大的话，有性能隐患
         */
        @Override
        public String appendPagination(String oldSql, int pageNo, int pageSize) {
            return oldSql + " limit " + pageSize + " offset " + (pageNo - 1) * pageSize;
        }
    },
    SQLSERVER("sqlserver", '[', ']') {
        @Override
        public String appendPagination(String oldSql, int pageNo, int pageSize) {
            return oldSql;
        }
    },
    ORACLE("oracle", '"', '"') {
        @Override
        public String appendPagination(String oldSql, int pageNo, int pageSize) {
            int offset = (pageNo - 1) * pageSize;
            return "select * from (select tmp_tb.*,ROWNUM row_id from (" + oldSql + ")  tmp_tb where ROWNUM<=" + (offset + pageSize) + ") where row_id>" + offset;
        }
    };

    /**
     * 数据库方言名称
     */
    private String dialect;

    /**
     * 关键字的左引用字符
     */
    private char leftIdentifierQuote;

    /**
     * 关键字的右引用字符
     */
    private char rightIdentifierQuote;

    DBType(String dialect, char leftIdentifierQuote, char rightIdentifierQuote) {
        this.dialect = dialect;
        this.leftIdentifierQuote = leftIdentifierQuote;
        this.rightIdentifierQuote = rightIdentifierQuote;
    }

    public static DBType getByDialect(String dialect) {
        if (StringUtil.isNullOrEmpty(dialect)) {
            return MYSQL;
        }
        for (DBType dbType : DBType.values()) {
            if (dbType.getDialect().equalsIgnoreCase(dialect)) {
                return dbType;
            }
        }
        throw new IllegalArgumentException(String.format("Not support sql dialect, all the support dialect are %s", listAllDialect()));
    }

    private static List<String> listAllDialect() {
        return Arrays.stream(DBType.values()).map(DBType::getDialect).collect(Collectors.toList());
    }

    /**
     * 定义在分页时的行为
     * @param oldSql: 原来的sql
     * @param pageNo: 页号。从1开始
     * @param pageSize: 分页大小
     * @return newSql
     */
    public abstract String appendPagination(String oldSql, int pageNo, int pageSize);
}
