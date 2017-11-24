package com.github.cafune1853.mybatis.spring.support.meta;

import com.github.cafune1853.mybatis.spring.support.annotation.AppendEntityClass;
import com.github.cafune1853.mybatis.spring.support.annotation.AutoResultMap;
import com.github.cafune1853.mybatis.spring.support.annotation.SetKeyPropertiesAndColumns;
import com.github.cafune1853.mybatis.spring.support.constant.MetaObjectShared;
import com.github.cafune1853.mybatis.spring.support.mapper.ICurdMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.session.Configuration;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author huangzhw
 */
@Slf4j
public class MapperMethodMetaFactory {
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
	/**
	 * MapperMethod即MapperStatement,通过该方法可以获得一个Mapper方法的元数据信息，在这里解析
	 * {@link AppendEntityClass} {@link AutoResultMap} {@link SetKeyPropertiesAndColumns}
	 * 三个注解。
	 * @param configuration: mybatis上下文配置，目前仅用于缓存ResultMap
	 * @param mapperMethodId: 即mapper方法的全路径.
	 * @return {@link MapperMethodMeta}
	 */
	public static MapperMethodMeta getMapperMethodMeta(Configuration configuration, String mapperMethodId) {
		return MAPPER_METHOD_META_CACHE.computeIfAbsent(mapperMethodId, key -> {
			int lastIndex = key.lastIndexOf('.');
			String mapperClassName = key.substring(0, lastIndex);
			String mapperMethodName = key.substring(lastIndex + 1);
			try {
				return buildMapperMeta(configuration, mapperClassName, mapperMethodName);
			} catch (ClassNotFoundException e) {
				throw new IllegalStateException(String.format("MapperClass(nameSpace):%s can not be resolved as Class.", mapperClassName));
			}
		});
	}
	
	private static MapperMethodMeta buildMapperMeta(Configuration configuration, String mapperClassName, String mapperMethodName) throws ClassNotFoundException {
		Class<?> mapperClazz = Class.forName(mapperClassName);
		boolean appendClazzAsArg = false;
		boolean autoResultMap = false;
		boolean setKeyPropertiesAndColumns = false;
		String keyProperty = null;
		String keyColumn = null;
		Class<?> entityClazz = null;
		List<ResultMap> resultMaps = new ArrayList<>();
		Method method = getMapperMethodByName(mapperClazz, mapperMethodName);
		if (method.isAnnotationPresent(AppendEntityClass.class) || method.isAnnotationPresent(AutoResultMap.class)) {
			appendClazzAsArg = true;
			entityClazz = getEntityClassByMapperClass(mapperClazz);
			if (method.isAnnotationPresent(AutoResultMap.class)) {
				autoResultMap = true;
				//以整个mapper作为key，进行MapperResult的缓存，可以考虑优化成使用实体类作为key进行缓存。
				String mapperResultId = mapperClassName + DYNAMIC_GENERATE_MAPPER_ID_SUFFIX;
				ResultMap resultMap = getResultMap(configuration, entityClazz, mapperResultId);
				resultMaps.add(resultMap);
			}
		}
		if (method.isAnnotationPresent(SetKeyPropertiesAndColumns.class)) {
			setKeyPropertiesAndColumns = true;
			EntityMeta entityMeta = EntityMetaFactory.getEntityMeta(getEntityClassByMapperClass(mapperClazz));
			keyColumn = entityMeta.getIdColumnName();
			keyProperty = entityMeta.columnNameToFieldName(keyColumn);
		}
		return new MapperMethodMeta(entityClazz, appendClazzAsArg, autoResultMap, resultMaps, setKeyPropertiesAndColumns, keyProperty, keyColumn);
	}
	
	private static ResultMap getResultMap(Configuration configuration, Class<?> entityClazz, String mapperResultId) {
		if (configuration.hasResultMap(mapperResultId)) {
			return configuration.getResultMap(mapperResultId);
		}
		List<ResultMapping> resultMappings = new ArrayList<>();
		EntityMeta entityMeta = EntityMetaFactory.getEntityMeta(entityClazz);
		for (Map.Entry<String, Field> columnFieldEntry : entityMeta.getColumnFieldMaps().entrySet()) {
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
		ResultMap resultMap = new ResultMap.Builder(configuration, mapperResultId, entityClazz, resultMappings).build();
		configuration.addResultMap(resultMap);
		return resultMap;
	}
	
	private static Class<?> resolveResultJavaType(Class<?> resultType, String property) {
		if (property != null) {
			try {
				MetaClass metaResultType = MetaClass.forClass(resultType, MetaObjectShared.REFLECTOR_FACTORY);
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
	
	private static Method getMapperMethodByName(Class<?> mapperClazz, String methodName) {
		Method[] allPublicMethods = mapperClazz.getMethods();
		Method method = null;
		for (Method publicMethod : allPublicMethods) {
			if (publicMethod.getName().equals(methodName)) {
				method = publicMethod;
				break;
			}
		}
		if (method == null) {
			throw new IllegalStateException(String.format("Method %s#%s not found.", mapperClazz.getName(), methodName));
		}
		return method;
	}
	
	private static Class<?> getEntityClassByMapperClass(Class<?> mapperClazz) {
		return MAPPER_CLASS_ENTITY_CLASS_CACHE.computeIfAbsent(mapperClazz, clz -> {
			Class<?> entityClazz = null;
			Type[] genericInterfaces = clz.getGenericInterfaces();
			for (Type genericInterface : genericInterfaces) {
				if (genericInterface instanceof ParameterizedType) {
					ParameterizedType parameterizedType = (ParameterizedType) genericInterface;
					if (ICurdMapper.class.isAssignableFrom(((Class) parameterizedType.getRawType()))) {
						Type[] genericTypes = parameterizedType.getActualTypeArguments();
						if (genericTypes.length == 1) {
							entityClazz = (Class<?>) genericTypes[0];
						}
					}
				}
			}
			if (entityClazz == null) {
				throw new IllegalStateException(String.format("MapperClass(nameSpace):%s must implements/extends ICurdMapper and contains one generic param.", mapperClazz.getName()));
			}
			return entityClazz;
		});
	}
}
