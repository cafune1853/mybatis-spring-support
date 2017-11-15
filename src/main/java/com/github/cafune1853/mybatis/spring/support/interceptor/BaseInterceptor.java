package com.github.cafune1853.mybatis.spring.support.interceptor;

import com.github.cafune1853.mybatis.spring.support.annotation.AppendEntityClass;
import com.github.cafune1853.mybatis.spring.support.annotation.ResultMapWithJpa;
import com.github.cafune1853.mybatis.spring.support.mapper.IBaseMapper;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import javax.security.auth.login.Configuration;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author doggy1853
 */
@Intercepts({
	@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
	@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class BaseInterceptor implements Interceptor {
	/**
	 * Mapper方法的全路径到其元数据的缓存。
	 */
	private final Map<String, MapperMethodMeta> MAPPER_METHOD_META_CACHE = new ConcurrentHashMap<>();
	/**
	 * Mapper类的全路径到其对应实体类的缓存。
	 */
	private final Map<Class<?>, Class<?>> MAPPER_CLASS_ENTITY_CLASS_CACHE = new ConcurrentHashMap<>();
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		Object target = invocation.getTarget();
		Object[] args = invocation.getArgs();
		if(target instanceof Executor){
			MappedStatement mappedStatement = (MappedStatement) args[0];
			//fullPath即为方法路径
			String fullPath = mappedStatement.getId();
			
			Object arg = args[1];
			args[1] = "test";
		}
		return invocation.proceed();
	}
	
	private MapperMethodMeta getMapperMeate(Configuration configuration, String fullMapperMethodName){
		return MAPPER_METHOD_META_CACHE.computeIfAbsent(fullMapperMethodName, key -> {
			int lastIndex = key.lastIndexOf(".");
			String mapperClassName = key.substring(0, lastIndex);
			String mapperMethodName = key.substring(lastIndex + 1);
			try {
				return buildMapperMeta(configuration, mapperClassName, mapperMethodName);
			}catch (ClassNotFoundException e){
				throw new IllegalStateException(String.format("MapperClass(nameSpace):%s can not be resolved as Class.", mapperClassName));
			}
		});
	}
	
	private MapperMethodMeta buildMapperMeta(Configuration configuration, String mapperClassName, String mapperMethodName) throws ClassNotFoundException{
		Class<?> mapperClazz = Class.forName(mapperClassName);
		boolean appendClazzAsArg = false;
		boolean resultMapWithJpa = false;
		Class<?> entityClazz = null;
		List<ResultMap> resultMaps = null;
		Method method = getMapperMethodByName(mapperClazz, mapperMethodName);
		if(method.isAnnotationPresent(AppendEntityClass.class) || method.isAnnotationPresent(ResultMapWithJpa.class)){
			appendClazzAsArg = true;
			entityClazz = getEntityClassByMapperClass(mapperClazz);
			if(method.isAnnotationPresent(ResultMapWithJpa.class)){
				resultMapWithJpa = true;
				//TODO:从clazz中构建resultMap
			}
		}
		return new MapperMethodMeta(entityClazz, appendClazzAsArg, resultMapWithJpa, resultMaps);
	}
	
	private Method getMapperMethodByName(Class<?> mapperClazz, String methodName){
		Method[] allPublicMethods = mapperClazz.getMethods();
		Method method = null;
		for (Method publicMethod : allPublicMethods) {
			if (publicMethod.getName().equals(methodName)){
				method = publicMethod;
				break;
			}
		}
		if(method == null){
			throw new IllegalStateException(String.format("Method %s#%s not found.", mapperClazz.getName(), methodName));
		}
		return method;
	}
	
	private Class<?> getEntityClassByMapperClass(Class<?> mapperClazz) {
		return MAPPER_CLASS_ENTITY_CLASS_CACHE.computeIfAbsent(mapperClazz, clz -> {
			Class<?> entityClazz = null;
			Type[] genericInterfaces = clz.getGenericInterfaces();
			for (Type genericInterface : genericInterfaces) {
				if(genericInterface instanceof ParameterizedType){
					ParameterizedType parameterizedType = (ParameterizedType)genericInterface;
					if (IBaseMapper.class.isAssignableFrom(((Class)parameterizedType.getOwnerType()))){
						Type[] genericTypes = parameterizedType.getActualTypeArguments();
						if(genericTypes.length == 1){
							entityClazz = (Class<?>) genericTypes[0];
						}
					}
				}
			}
			if(entityClazz == null){
				throw new IllegalStateException(String.format("MapperClass(nameSpace):%s must implements/extends IBaseMapper and contains one generic param.", mapperClazz.getName()));
			}
			return entityClazz;
		});
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
	
	}
}
