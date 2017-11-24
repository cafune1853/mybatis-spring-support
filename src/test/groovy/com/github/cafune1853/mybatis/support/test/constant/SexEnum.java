package com.github.cafune1853.mybatis.support.test.constant;

import com.github.cafune1853.mybatis.support.annotation.EnumRepresentField;

public enum SexEnum {
	MALE(1), FEMALE(2);
	
	@EnumRepresentField
	private int type;
	
	SexEnum(int type) {
		this.type = type;
	}
}
