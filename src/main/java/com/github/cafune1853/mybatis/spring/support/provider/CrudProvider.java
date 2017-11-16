package com.github.cafune1853.mybatis.spring.support.provider;

public class CrudProvider {
	public static final String PARAM_KEY = "PARAM";
	public static final String CLASS_KEY = "CLASS";
	public String test(String arg){
		return "SELECT '" + arg + "'";
	}
}
