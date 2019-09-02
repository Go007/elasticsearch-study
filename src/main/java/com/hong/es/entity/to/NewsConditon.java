package com.hong.es.entity.to;

import java.util.List;

public class NewsConditon extends BaseInData {
	//类型
    private int type;
	//公司类型
	private String companyType;

	//新闻来源
	private List<String> sourceTypeList;

	//情感
	private List<String> sentimentalList;

	//风险事项
	private String riskItem;

	//公告时间
	private String timeStr;

	public List<String> getSourceTypeList() {
		return sourceTypeList;
	}

	public void setSourceTypeList(List<String> sourceTypeList) {
		this.sourceTypeList = sourceTypeList;
	}

	public List<String> getSentimentalList() {
		return sentimentalList;
	}

	public void setSentimentalList(List<String> sentimentalList) {
		this.sentimentalList = sentimentalList;
	}

	public String getRiskItem() {
		return riskItem;
	}

	public void setRiskItem(String riskItem) {
		this.riskItem = riskItem;
	}

	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}

	public String getCompanyType() {
		return companyType;
	}

	public void setCompanyType(String companyType) {
		this.companyType = companyType;
	}

	public String getTimeStr() {
		return timeStr;
	}

	public void setTimeStr(String timeStr) {
		this.timeStr = timeStr;
	}
}
