package com.github.cafune1853.mybatis.spring.support.interceptor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.github.cafune1853.mybatis.spring.support.provider.BaseProvider;
import com.github.cafune1853.mybatis.spring.support.util.PersistenceEntityMeta;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import com.github.cafune1853.mybatis.spring.support.annotation.AppendEntityClass;
import com.github.cafune1853.mybatis.spring.support.annotation.AutoResultMap;
import com.github.cafune1853.mybatis.spring.support.mapper.IBaseMapper;

import javax.persistence.Id;


/**
 * @author doggy1853
 * 支持参数
 */
@Slf4j
@Intercepts({
	@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
	@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class BaseInterceptor extends AbstractInterceptor implements Interceptor {
	/**
	 * Mapper方法的全路径到其元数据的缓存。
	 */
	private static final Map<String, MapperMethodMeta> MAPPER_METHOD_META_CACHE = new ConcurrentHashMap<>();
	/**
	 * Mapper类的全路径到其对应实体类的缓存,由于一旦MAPPER_METHOD_META_CACHE构建完成就不会再使用这个缓存，
	 * 后续可以考虑改用ConcurrentReferenceHashMap来释放内存空间。
	 */
	private static final Map<Class<?>, Class<?>> MAPPER_CLASS_ENTITY_CLASS_CACHE = new ConcurrentHashMap<>();
	private static final String DYNAMIC_GENERATE_MAPPER_ID_SUFFIX = ".GeneratedMapperIdSuffix";
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		Object target = invocation.getTarget();
		Object[] args = invocation.getArgs();
		if(target instanceof Executor){
			MappedStatement mappedStatement = (MappedStatement) args[0];
			MapperMethodMeta mapperMethodMeta = getMapperMeta(mappedStatement.getConfiguration(), mappedStatement.getId());
			if(mapperMethodMeta.isAppendClazzAsArg()){
				Object arg = args[1];
				if(arg != null){
					Map<String, Object> argMap = null;
					if(arg instanceof Map){
						argMap = (Map<String,Object>)arg;
						argMap.put(BaseProvider.CLASS_KEY, mapperMethodMeta.getEntityClazz());
					}else{
						argMap.put(BaseProvider.CLASS_KEY, mapperMethodMeta.getEntityClazz());
						argMap.put(BaseProvider.PARAM_KEY, arg);
					}
					args[1] = argMap;
				}else {
					args[1] = mapperMethodMeta.getEntityClazz();
				}
			}
			
			if(mapperMethodMeta.isAutoResultMap()){
				MetaObject metaObject = getMetaObject(mappedStatement);
				metaObject.setValue("resultMaps", mapperMethodMeta.getResultMaps());
			}
		}
		return invocation.proceed();
	}
	
	private MapperMethodMeta getMapperMeta(Configuration configuration, String fullMapperMethodName){
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
		List<ResultMap> resultMaps = new ArrayList<>();
		Method method = getMapperMethodByName(mapperClazz, mapperMethodName);
		if(method.isAnnotationPresent(AppendEntityClass.class) || method.isAnnotationPresent(AutoResultMap.class)){
			appendClazzAsArg = true;
			entityClazz = getEntityClassByMapperClass(mapperClazz);
			if(method.isAnnotationPresent(AutoResultMap.class)){
				resultMapWithJpa = true;
				String mapperId = mapperClassName + DYNAMIC_GENERATE_MAPPER_ID_SUFFIX;
				if(configuration.hasResultMap(mapperId)){
					resultMaps.add(configuration.getResultMap(mapperId));
				}else{
					List<ResultMapping> resultMappings = new ArrayList<>();
					PersistenceEntityMeta persistenceEntityMeta = PersistenceEntityMeta.getPersistenceEntityMeta(entityClazz);
					for(Map.Entry<String, Field> columnFieldEntry : persistenceEntityMeta.getColumnFieldMaps().entrySet()){
						String columnName = columnFieldEntry.getKey();
						String fieldName = columnFieldEntry.getValue().getName();
						Class<?> columnTypeClass = resolveResultJavaType(entityClazz, fieldName);
						List<ResultFlag> flags = new ArrayList<>();
						if (columnFieldEntry.getValue().isAnnotationPresent(Id.class)) {
							flags.add(ResultFlag.ID);
						}
						ResultMapping.Builder builder = new ResultMapping.Builder(configuration, fieldName, columnName, columnTypeClass);
						builder.flags(flags);
						builder.composites(new ArrayList<>());
						builder.notNullColumns(new HashSet<>());
						resultMappings.add(builder.build());
					}
					ResultMap resultMap = new ResultMap.Builder(configuration, mapperId, entityClazz, resultMappings).build();
					configuration.addResultMap(resultMap);
					resultMaps.add(resultMap);
				}
			}
		}
		return new MapperMethodMeta(entityClazz, appendClazzAsArg, resultMapWithJpa, resultMaps);
	}
	
	/**
	 * copy from mybatis sourceCode
	 *
	 * @param resultType
	 * @param property
	 * @return
	 */
	private Class<?> resolveResultJavaType(Class<?> resultType, String property) {
		if (property != null) {
			try {
				MetaClass metaResultType = MetaClass.forClass(resultType, REFLECTOR_FACTORY);
				Class result = metaResultType.getSetterType(property);
				if (result != null) {
					return result;
				}
			} catch (Exception ignored) {
				log.error("error", ignored);
			}
		}
		return Object.class;
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
					if (IBaseMapper.class.isAssignableFrom(((Class)parameterizedType.getRawType()))){
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
