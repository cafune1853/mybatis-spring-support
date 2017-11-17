package com.github.cafune1853.mybatis.spring.support.handler;

import com.github.cafune1853.mybatis.spring.support.annotation.EnumRepresentField;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 使用{@link EnumRepresentField} 来表示该enum域，
 * 如果没有被该注解标识的域，则行为与{@link org.apache.ibatis.type.EnumTypeHandler}一致
 * @author huangzhw
 */
public class FieldEnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {
	private Class<E> type;
	
	public FieldEnumTypeHandler(Class<E> type) {
		if (type == null) {
			throw new IllegalArgumentException("Type argument cannot be null");
		}
		this.type = type;
	}
	
	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
	
	}
	
	@Override
	public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return null;
	}
	
	@Override
	public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return null;
	}
	
	@Override
	public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return null;
	}
}
