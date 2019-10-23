package com.hong.es.entity;

import java.math.BigDecimal;

public class CommonCountVO {
	
	private String type;

	private String key;
	
	private BigDecimal value;


	public CommonCountVO() {
		super();
	}

	public CommonCountVO(String type, String key, BigDecimal value) {
		super();
		this.type = type;
		this.key = key;
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "CommonCountVO{" +
				"type='" + type + '\'' +
				", key='" + key + '\'' +
				", value=" + value +
				'}';
	}
}
