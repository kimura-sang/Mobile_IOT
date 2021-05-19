/**
 * 
 */
package com.chug.north_outlet.bean;

import org.xutils.db.annotation.Column;

import java.io.Serializable;


/**
 * EntityBase
 * 
 * @author Sean.guo
 * @data 2015.7.1
 * @doc 需要继承IEntity的实体都实现Comparable接口，方便排序 继承了id属性，实体的id
 */
public abstract class EntityBase implements Serializable,Comparable<Object> {

	/** 
	 * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么) 
	 */ 
	private static final long serialVersionUID = 2858234816204724092L;
	
	@Column(name = "id", isId = true)
	protected int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
