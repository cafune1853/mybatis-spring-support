package com.github.cafune1853.mybatis.spring.support.constant;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.github.cafune1853.mybatis.spring.support.util.StringUtil;

import lombok.Getter;

/**
 * @author doggy
 * Created on 2017-11-20.
 */
@Getter
public enum DBType {
    MYSQL("mysql", '`', '`'), POSTGRESQL("postgresql", '"', '"'), SQLSERVER("sqlserver", '[', ']'), ORACLE("oracle", '"', '"');

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
}
