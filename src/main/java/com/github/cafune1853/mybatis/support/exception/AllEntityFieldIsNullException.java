package com.github.cafune1853.mybatis.support.exception;

/**
 * @author huangzhw
 */
public class AllEntityFieldIsNullException extends RuntimeException {
	public AllEntityFieldIsNullException(){}
	
	public AllEntityFieldIsNullException(String msg){
		super(msg);
	}
	
	public AllEntityFieldIsNullException(String msg, Throwable cause){
		super(msg, cause);
	}
	
	public AllEntityFieldIsNullException(Throwable cause){
		super(cause);
	}
}
