package com.github.cafune1853.test;

import com.github.cafune1853.mybatis.spring.support.annotation.EnumRepresentField;

/**
 * @author doggy
 * Created on 2017-11-19.
 */

public enum SexEnum {
	MALE(1), FEMALE(2);
	
	@EnumRepresentField
	private int type;
	
	SexEnum(int type) {
		this.type = type;
	}
}
