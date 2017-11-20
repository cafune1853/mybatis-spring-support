package com.github.cafune1853.mybatis.spring.support.config;

import com.github.cafune1853.mybatis.spring.support.constant.DBType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author doggy
 * Created on 2017-11-20.
 */
@Data
public class DBConfig {
	private volatile DBType dbType;
	private static final DBConfig INSTANCE = new DBConfig();
	private DBConfig(){
	}
	
	public static DBConfig getInstance(){
		return INSTANCE;
	}
	
	public static DBConfig configDbType(DBType dbType){
		INSTANCE.setDbType(dbType);
		return INSTANCE;
	}
}
