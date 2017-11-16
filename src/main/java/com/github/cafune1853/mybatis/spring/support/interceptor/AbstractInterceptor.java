package com.github.cafune1853.mybatis.spring.support.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;

/**
 * @author doggy
 * Created on 2017-11-16.
 */

@Slf4j
class AbstractInterceptor {
	static final ReflectorFactory REFLECTOR_FACTORY = new DefaultReflectorFactory();
	static final DefaultObjectFactory OBJECT_FACTORY = new DefaultObjectFactory();
	static final DefaultObjectWrapperFactory OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
	
	/**
	 * 获取被拦截的对象(MetaObject包装)
	 * 通过JDK动态代理生成的动态代理类除了实现目标接口外还会继承{@link java.lang.reflect.Proxy},顺便从proxy中继承相应的字段
	 * proxy中有一个重要的字段h指向{@link java.lang.reflect.InvocationHandler},这也是为什么会用getValue("h")
	 * 又由于在mybatis中,动态代理使用了{@link org.apache.ibatis.plugin.Plugin} 实现的,因此h实例都有一个target指向被代理的实际对象
	 * 整体代理对象结构如下:
	 * realObj
	 * proxy1 = {h: plugin1: {target: realObject}}
	 * proxy2 = {h: plugin2: {target: proxy1}}
	 * @param obj bean对象
	 * @return
	 */
	protected MetaObject getMetaObject(Object obj) {
		ObjectFactory objectFactory = OBJECT_FACTORY;
		ObjectWrapperFactory objectWrapperFactory = OBJECT_WRAPPER_FACTORY;
		MetaObject metaObject = MetaObject.forObject(obj, objectFactory, objectWrapperFactory, REFLECTOR_FACTORY);
		// 由于目标类可能被多个拦截器拦截，从而形成多次代理，通过以下循环找出原始代理
		while (metaObject.hasGetter("h")) {
			Object object = metaObject.getValue("h");
			metaObject = MetaObject.forObject(object, objectFactory, objectWrapperFactory, REFLECTOR_FACTORY);
		}
		// 得到原始代理对象的目标类，即StatementHandler实现类
		if (metaObject.hasGetter("target")) {
			Object target = metaObject.getValue("target");
			metaObject = MetaObject.forObject(target, objectFactory, objectWrapperFactory, REFLECTOR_FACTORY);
			if (metaObject.hasGetter("h")) {
				return getMetaObject(target);
			}
		}
		return metaObject;
	}
}
