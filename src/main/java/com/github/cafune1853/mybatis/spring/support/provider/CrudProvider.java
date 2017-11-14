package com.github.cafune1853.mybatis.spring.support.provider;

public class CrudProvider {
	public String test(String arg){
		return "SELECT '" + arg + "'";
	}
}
