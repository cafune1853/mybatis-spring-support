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
	
	@Column(name = "gmt_create")
	private Date GmtCreate;
	
	@Column(name = "gmt_modified")
	private Date GmtModified;
	
	public T getId() {
		return id;
	}
	
	public BaseEntity setId(T id) {
		this.id = id;
		return this;
	}
	
	public Date getGmtCreate() {
		return GmtCreate;
	}
	
	public BaseEntity setGmtCreate(Date gmtCreate) {
		GmtCreate = gmtCreate;
		return this;
	}
	
	public Date getGmtModified() {
		return GmtModified;
	}
	
	public BaseEntity setGmtModified(Date gmtModified) {
		GmtModified = gmtModified;
		return this;
	}
}
