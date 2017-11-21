package com.github.cafune1853.mybatis.spring.support.interceptor;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import com.github.cafune1853.mybatis.spring.support.config.DBConfig;
import com.github.cafune1853.mybatis.spring.support.constant.DBType;
import com.github.cafune1853.mybatis.spring.support.provider.BaseProvider;
import com.github.cafune1853.mybatis.spring.support.util.MapperMethodMeta;

import lombok.extern.slf4j.Slf4j;

/**
 * @author doggy1853
 * 支持参数
 */
@Slf4j
@Intercepts({ @Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class }), @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }) })
public class CurdInterceptor extends AbstractInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();
        Object[] args = invocation.getArgs();
        if (target instanceof Executor) {
            MappedStatement mappedStatement = (MappedStatement) args[0];
            MapperMethodMeta mapperMethodMeta = MapperMethodMeta.getMapperMethodMeta(mappedStatement.getConfiguration(), mappedStatement.getId());
            if (mapperMethodMeta.isAppendClazzAsArg()) {
                Object arg = args[1];
                if (arg != null) {
                    Map<String, Object> argMap = null;
                    if (arg instanceof Map) {
                        argMap = (Map<String, Object>) arg;
                        argMap.put(BaseProvider.CLASS_KEY, mapperMethodMeta.getEntityClazz());
                    } else {
                        argMap = new HashMap<>(2);
                        argMap.put(BaseProvider.CLASS_KEY, mapperMethodMeta.getEntityClazz());
                        argMap.put(BaseProvider.PARAM_KEY, arg);
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
        return invocation.proceed();
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
