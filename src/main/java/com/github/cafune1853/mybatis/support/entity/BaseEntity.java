package com.github.cafune1853.mybatis.support.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * 作为实体基类,按照阿里编码规范,给出必备三字段,分别是id gmtCreate gmtModified
 * 对于insert操作,在主流数据库中都可以通过DEFAULT CURRENT_TIMESTAMP进行默认值生成,所以不支持将这两个非空域落库,由数据库自动生成即可
 * 对于update操作,gmtCreate是不允许更新的,因此直接忽略该域,主流数据库可以通过DDL/Trigger自动更新gmtModified,所以默认通过非空域无法更新该值
 * 但还是将更新gmtModified的功能开放给用户,在CurdPaginationInterceptor中配置manualUpdateGmtModified即可通过非空的gmtModified更新该值.
 * 对于search操作,由于gmtCreate/gmtModified都不具有业务意义,因此从搜索实体中直接排除这两个字段
 * @author huangzhw
 */
public abstract class BaseEntity<T extends Serializable> {
	@Id
	@Column(name = "id")
	private T id;
	
	@Column(name = "gmt_create")
	private Date gmtCreate;
	
	@Column(name = "gmt_modified")
	private Date gmtModified;
	
	public T getId() {
		return id;
	}
	
	public BaseEntity setId(T id) {
		this.id = id;
		return this;
	}
	
	public Date getGmtCreate() {
		return gmtCreate;
	}
	
	public Date getGmtModified() {
		return gmtModified;
	}
	
	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("BaseEntity{");
		sb.append("id=").append(id);
		sb.append(", gmtCreate=").append(gmtCreate);
		sb.append(", gmtModified=").append(gmtModified);
		sb.append('}');
		return sb.toString();
	}
}
