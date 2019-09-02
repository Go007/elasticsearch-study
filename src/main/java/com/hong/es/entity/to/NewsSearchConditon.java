package com.hong.es.entity.to;

/**
 *  新闻舆情查询条件

 * @ClassName: NewsSearchConditon

 * @Description: TODO

 * @author: liunn

 * @date: 2018年9月13日 上午11:33:31
 */
public class NewsSearchConditon {
	//搜索框关键字
    private String keyword;
    //当前页
    private Integer curPage;
    //每页显示的条数
    private Integer rowNum;
	//媒体类型
	private String mediaType;
    //发布时间
    private String publishTime;
    //风险类型
    private String riskType;
    //关联企业类型
	private String relateCompanyType;
	//风险一级标签
	private String labelL1;
	//风险二级标签
	private String labelL2;
	//正负面
	private String sentimental;
	//关联度
	private String relevance;
	//新闻来源
	private String sourceType;

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getRelevance() {
		return relevance;
	}

	public void setRelevance(String relevance) {
		this.relevance = relevance;
	}

	public String getRelateCompanyType() {
		return relateCompanyType;
	}

	public void setRelateCompanyType(String relateCompanyType) {
		this.relateCompanyType = relateCompanyType;
	}

	public String getLabelL1() {
		return labelL1;
	}

	public void setLabelL1(String labelL1) {
		this.labelL1 = labelL1;
	}

	public String getLabelL2() {
		return labelL2;
	}

	public void setLabelL2(String labelL2) {
		this.labelL2 = labelL2;
	}

	public String getSentimental() {
		return sentimental;
	}

	public void setSentimental(String sentimental) {
		this.sentimental = sentimental;
	}

	public String getMediaType() {
		return mediaType;
	}
	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public Integer getCurPage() {
		return curPage;
	}
	public void setCurPage(Integer curPage) {
		this.curPage = curPage;
	}
	public Integer getRowNum() {
		return rowNum;
	}
	public void setRowNum(Integer rowNum) {
		this.rowNum = rowNum;
	}
	public String getPublishTime() {
		return publishTime;
	}
	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}
	public String getRiskType() {
		return riskType;
	}
	public void setRiskType(String riskType) {
		this.riskType = riskType;
	}
	
}
