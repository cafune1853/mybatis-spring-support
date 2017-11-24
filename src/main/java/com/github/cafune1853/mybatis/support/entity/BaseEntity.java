package com.github.cafune1853.mybatis.support.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * @author huangzhw
 */
public abstract class BaseEntity<T extends Serializable> {
	@Id
	@Column(name = "id")
	private T id;
	
	public T getId() {
		return id;
	}
	
	public BaseEntity setId(T id) {
		this.id = id;
		return this;
	}
}
