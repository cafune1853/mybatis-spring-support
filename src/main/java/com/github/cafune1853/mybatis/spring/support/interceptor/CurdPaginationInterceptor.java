package com.github.cafune1853.mybatis.spring.support.interceptor;

import java.lang.reflect.Field;
import java.security.InvalidParameterException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import com.github.cafune1853.mybatis.spring.support.config.DBConfig;
import com.github.cafune1853.mybatis.spring.support.constant.DBType;
import com.github.cafune1853.mybatis.spring.support.meta.MapperMethodMeta;
import com.github.cafune1853.mybatis.spring.support.meta.MapperMethodMetaFactory;
import com.github.cafune1853.mybatis.spring.support.pagination.Page;
import com.github.cafune1853.mybatis.spring.support.provider.CurdProvider;
import com.github.cafune1853.mybatis.spring.support.util.ReflectUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author doggy1853
 * 支持参数
 */
@Slf4j
@Intercepts({ @Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class }), @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }), @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class, Integer.class }) })
public class CurdPaginationInterceptor extends AbstractInterceptor implements Interceptor {
    private static final Field SQL_FIELD;
    static {
        SQL_FIELD = ReflectUtil.silentGetFieldAndSetAccessible(BoundSql.class, "sql");
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();
        Object[] args = invocation.getArgs();
        if (target instanceof Executor) {
            appendClazzAndAutoResultMap(args);
        } else if (target instanceof StatementHandler) {
            BoundSql boundSql = ((StatementHandler) target).getBoundSql();
            MetaObject statementHandler = getMetaObject(target);
            Connection connection = (Connection) args[0];
            pagination(connection, statementHandler, boundSql);
        }
        return invocation.proceed();
    }

    private void appendClazzAndAutoResultMap(Object[] args) {
        MappedStatement mappedStatement = (MappedStatement) args[0];
        MapperMethodMeta mapperMethodMeta = MapperMethodMetaFactory.getMapperMethodMeta(mappedStatement.getConfiguration(), mappedStatement.getId());
        if (mapperMethodMeta.isAppendClazzAsArg()) {
            Object arg = args[1];
            if (arg != null) {
                Map<String, Object> argMap = null;
                if (arg instanceof Map) {
                    argMap = (Map<String, Object>) arg;
                    argMap.put(CurdProvider.CLASS_KEY, mapperMethodMeta.getEntityClazz());
                } else {
                    argMap = new HashMap<>(2);
                    argMap.put(CurdProvider.CLASS_KEY, mapperMethodMeta.getEntityClazz());
                    argMap.put(CurdProvider.PARAM_KEY, arg);
                }
                args[1] = argMap;
            } else {
                args[1] = mapperMethodMeta.getEntityClazz();
            }
        }

        if (mapperMethodMeta.isAutoResultMap()) {
            MetaObject metaObject = getMetaObject(mappedStatement);
            metaObject.setValue("resultMaps", mapperMethodMeta.getResultMaps());
        }

        if (mapperMethodMeta.isSetKeyPropertiesAndColumns()) {
            MetaObject metaObject = getMetaObject(mappedStatement);
            metaObject.setValue("keyProperties", new String[] { mapperMethodMeta.getKeyProperty() });
            metaObject.setValue("keyColumns", new String[] { mapperMethodMeta.getKeyColumn() });
        }
    }

    private void pagination(Connection connection, MetaObject statementHandler, BoundSql boundSql) throws SQLException {
        Optional<Page> optionalPage = getPageParam(boundSql.getParameterObject());
        int prefixLength = 10;
        String selectPrefix = "select";
        if (optionalPage.isPresent() && boundSql.getSql().substring(0, prefixLength).toLowerCase().startsWith(selectPrefix)) {
            Page page = optionalPage.get();
            //取消内存分页
            statementHandler.setValue("delegate.rowBounds.offset", RowBounds.NO_ROW_OFFSET);
            statementHandler.setValue("delegate.rowBounds.limit", RowBounds.NO_ROW_LIMIT);
            //统计总条数
            if (optionalPage.get().isCountTotal()) {
                MappedStatement mappedStatement = (MappedStatement) statementHandler.getValue("delegate.mappedStatement");
                page.setTotalNumber(getTotalCount(connection, mappedStatement, boundSql));
            }
            //改写sql
            String newSql = DBConfig.getInstance().getDbType().appendPagination(boundSql.getSql(), page.getPageNo(), page.getPageSize());
            ReflectUtil.silentSetFieldValue(SQL_FIELD, boundSql, newSql);
        }
    }

    // TODO:暂不支持@Param("alias") Map 形式的mapper方法。
    private Optional<Page> getPageParam(Object parameterObject) {
        if (parameterObject instanceof Page) {
            return Optional.of((Page) parameterObject);
        } else if (parameterObject instanceof Map) {
            Map map = (Map) parameterObject;
            for (Object param : map.entrySet()) {
                if (param instanceof Page) {
                    return Optional.of((Page) param);
                }
            }
        }
        return Optional.empty();
    }

    private int getTotalCount(Connection conn, MappedStatement ms, BoundSql boundSql) throws SQLException {
        String sqlLower = boundSql.getSql().toLowerCase();
        int start = sqlLower.indexOf("from");
        if (start == -1) {
            throw new InvalidParameterException("statement has no 'from' keyword");
        }
        int stop = sqlLower.indexOf("order by");
        if (stop == -1) {
            stop = sqlLower.length();
        }

        String countSql = "select count(0) " + boundSql.getSql().substring(start, stop);
        BoundSql countBoundSql = new BoundSql(ms.getConfiguration(), countSql, boundSql.getParameterMappings(), boundSql.getParameterObject());
        ParameterHandler parameterHandler = new DefaultParameterHandler(ms, boundSql.getParameterObject(), countBoundSql);
        try (PreparedStatement stmt = conn.prepareStatement(countSql)) {
            // 通过parameterHandler给PreparedStatement对象设置参数
            parameterHandler.setParameters(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor || target instanceof StatementHandler || target instanceof ResultSetHandler) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties) {
        String dialect = properties.getProperty("dialect");
        DBConfig.configDbType(DBType.getByDialect(dialect));
    }
}
