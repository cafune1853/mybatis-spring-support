package com.github.cafune1853.mybatis.spring.support.handler;

import com.github.cafune1853.mybatis.spring.support.annotation.EnumRepresentField;
import com.github.cafune1853.mybatis.spring.support.util.EnumResolveUtil;
import com.github.cafune1853.mybatis.spring.support.util.RepresentFieldTypeValue;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

import java.sql.*;

/**
 * 使用{@link EnumRepresentField} 来表示该enum域，
 * 如果没有被该注解标识的域，则行为与{@link org.apache.ibatis.type.EnumTypeHandler}一致
 * @author huangzhw
 */
@Slf4j
@MappedJdbcTypes(value = {JdbcType.SMALLINT, JdbcType.INTEGER, JdbcType.BIGINT, JdbcType.VARCHAR},includeNullJdbcType = true)
public class FieldEnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {
	private Class<E> enumType;
	
	public FieldEnumTypeHandler(Class<E> enumType) {
		if (enumType == null) {
			throw new IllegalArgumentException("Type argument cannot be null");
		}
		this.enumType = enumType;
	}
	
	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
		if(!EnumResolveUtil.hasRepresentField(enumType)){
			if (jdbcType == null) {
				ps.setString(i, parameter.name());
			} else {
				ps.setObject(i, parameter.name(), jdbcType.TYPE_CODE);
			}
		}else{
			RepresentFieldTypeValue representFieldTypeValue = EnumResolveUtil.getRepresentFieldTypeValue(enumType, parameter);
			Class<?> fieldType = representFieldTypeValue.getType();
			if(Short.class == fieldType|| short.class == representFieldTypeValue.getType()){
				ps.setShort(i, (Short)representFieldTypeValue.getValue());
			}else if(Integer.class == fieldType || int.class == fieldType){
				ps.setInt(i, (Integer)representFieldTypeValue.getValue());
			}else if(Long.class == fieldType || long.class == fieldType){
				ps.setLong(i, (Long) representFieldTypeValue.getValue());
			}else if(String.class == representFieldTypeValue.getType()){
				ps.setString(i, (String) representFieldTypeValue.getValue());
			}
		}
	}
	
	@Override
	public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
		if(!EnumResolveUtil.hasRepresentField(enumType)){
			String s = rs.getString(columnName);
			return s == null ? null : Enum.valueOf(enumType, s);
		}else{
			return EnumResolveUtil.getEnumByRepresentFieldValue(enumType, rs.getObject(columnName));
		}
	}
	
	@Override
	public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		if(!EnumResolveUtil.hasRepresentField(enumType)){
			String s = rs.getString(columnIndex);
			return s == null ? null : Enum.valueOf(enumType, s);
		}else{
			return EnumResolveUtil.getEnumByRepresentFieldValue(enumType, rs.getObject(columnIndex));
		}
	}
	
	@Override
	public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		if(!EnumResolveUtil.hasRepresentField(enumType)){
			String s = cs.getString(columnIndex);
			return s == null ? null : Enum.valueOf(enumType, s);
		}else{
			return EnumResolveUtil.getEnumByRepresentFieldValue(enumType, cs.getObject(columnIndex));
		}
	}
}
