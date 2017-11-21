package com.github.cafune1853.mybatis.spring.support.constant;

import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;

/**
 * @author huangzhw
 */
public class MetaObjectShared {
    public static final ReflectorFactory REFLECTOR_FACTORY = new DefaultReflectorFactory();
    public static final DefaultObjectFactory OBJECT_FACTORY = new DefaultObjectFactory();
    public static final DefaultObjectWrapperFactory OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
}
