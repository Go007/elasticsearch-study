package com.hong.es;

import java.io.Serializable;
import java.util.List;

public class EsWarningParameter implements Serializable{
	private static final long serialVersionUID = 3071804268463388190L;
	
	private String companyName;
	
	 /**是否是默认组合*/
    private int isDefault = 0;
    private List<Long> companyId;
    private Integer page;
    private Integer size = 10;

    /**组合*/
    private List<Long> portfolioId;

    /** 内部评级*/
    private List<String> rating;

    /** 外部评级*/
    private List<String> outRating;

    /**内评类型 code*/
    private String ratingType;

    /**所属敞口id*/
    private List<Long> exposure;

    /**严重程度 code*/
    private List<Long> importance;

    /**信号分类Code A B C*/
    private List<String> typeCode;

    /**开始时间*/
    private String startNoticeDate = null;

    /**结束时间*/
    private String endNoticeDate = null;

    /**搜索关键词： 企业名称、标题名称；支持模糊搜索*/
    private String keyword;

    /**处理状态*/
    private Long statusFlag;

    /**企业性质*/
    private List<String> orgFormNames;

    /**企业性质id*/
    private List<Long> orgFormIds;

    /**通过选择的组合转换成sql，用于最后拼接查询sql*/
    private String joinSql;

    /**排序字段*/
    private String sortField;

    /**排序方向 -1:逆序  1：正序*/
    private Integer sortDirection;

    /**预警类型名称*/
    private List<String> typeName;

    /**预警信号时段类型 1:最新  2：近期*/
    private Integer warningPeriodType = 1;

    /**预警规则*/
    private List<Long> warningRegulationSid;

    /**申万一级id*/
    private List<Long> swIndustryId;

    /**证监会大类id*/
    private List<Long> csrcIndustryId;

    /**企业类型id*/
    private List<Long> companyTypeId;

    /**数据源 0 新闻舆情 1 公告 2 company_warnings */
    private List<Integer> dataType;

    /**新闻来源 */
    private List<String> publishSite;

    /** 相关度代码 */
    private List<Integer> relevance;

    /** 相关度名称 */
    private List<String> relevanceName;

    /**情感倾向 */
    private List<Long> sentimental;

    private String label;

    /** 风险分层一级标签 */
    private List<String> level1;

    /** 风险分层二级标签 */
    private List<String> level2;

    /** 风险分层三级标签 */
    private List<String> level3;

    private Boolean needDaily;

    /**发债情况 0:否，1:是 */
    private Long issueBonds;

    /**上市情况 */
    private List<String> marketNames;

    /**是否新三板上市  0:否，1:是 */
    private Long newOTCMarket;

    /**是否私募  0:否，1:是 */
    private Long ppType;
    /**是否上市  0:否，1:是 */
    private Long shareType;

    /**新闻舆情、公告、company_warnings对应表的id*/
    private Long id;

    /**关联方类型  1:股东 2:对外投资 3: 对外担保 4.主要客户 5:主要供应商*/
    private Long relevanceType;

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public int getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(int isDefault) {
		this.isDefault = isDefault;
	}

	public List<Long> getCompanyId() {
		return companyId;
	}

	public void setCompanyId(List<Long> companyId) {
		this.companyId = companyId;
	}

	 

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public List<Long> getPortfolioId() {
		return portfolioId;
	}

	public void setPortfolioId(List<Long> portfolioId) {
		this.portfolioId = portfolioId;
	}

	public List<String> getRating() {
		return rating;
	}

	public void setRating(List<String> rating) {
		this.rating = rating;
	}

	public List<String> getOutRating() {
		return outRating;
	}

	public void setOutRating(List<String> outRating) {
		this.outRating = outRating;
	}

	public String getRatingType() {
		return ratingType;
	}

	public void setRatingType(String ratingType) {
		this.ratingType = ratingType;
	}

	public List<Long> getExposure() {
		return exposure;
	}

	public void setExposure(List<Long> exposure) {
		this.exposure = exposure;
	}

	public List<Long> getImportance() {
		return importance;
	}

	public void setImportance(List<Long> importance) {
		this.importance = importance;
	}

	public List<String> getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(List<String> typeCode) {
		this.typeCode = typeCode;
	}

	public String getStartNoticeDate() {
		return startNoticeDate;
	}

	public void setStartNoticeDate(String startNoticeDate) {
		this.startNoticeDate = startNoticeDate;
	}

	public String getEndNoticeDate() {
		return endNoticeDate;
	}

	public void setEndNoticeDate(String endNoticeDate) {
		this.endNoticeDate = endNoticeDate;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public Long getStatusFlag() {
		return statusFlag;
	}

	public void setStatusFlag(Long statusFlag) {
		this.statusFlag = statusFlag;
	}

	public List<String> getOrgFormNames() {
		return orgFormNames;
	}

	public void setOrgFormNames(List<String> orgFormNames) {
		this.orgFormNames = orgFormNames;
	}

	public List<Long> getOrgFormIds() {
		return orgFormIds;
	}

	public void setOrgFormIds(List<Long> orgFormIds) {
		this.orgFormIds = orgFormIds;
	}

	public String getJoinSql() {
		return joinSql;
	}

	public void setJoinSql(String joinSql) {
		this.joinSql = joinSql;
	}

	public String getSortField() {
		return sortField;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	public Integer getSortDirection() {
		return sortDirection;
	}

	public void setSortDirection(Integer sortDirection) {
		this.sortDirection = sortDirection;
	}

	public List<String> getTypeName() {
		return typeName;
	}

	public void setTypeName(List<String> typeName) {
		this.typeName = typeName;
	}

	public Integer getWarningPeriodType() {
		return warningPeriodType;
	}

	public void setWarningPeriodType(Integer warningPeriodType) {
		this.warningPeriodType = warningPeriodType;
	}

	public List<Long> getWarningRegulationSid() {
		return warningRegulationSid;
	}

	public void setWarningRegulationSid(List<Long> warningRegulationSid) {
		this.warningRegulationSid = warningRegulationSid;
	}

	public List<Long> getSwIndustryId() {
		return swIndustryId;
	}

	public void setSwIndustryId(List<Long> swIndustryId) {
		this.swIndustryId = swIndustryId;
	}

	public List<Long> getCsrcIndustryId() {
		return csrcIndustryId;
	}

	public void setCsrcIndustryId(List<Long> csrcIndustryId) {
		this.csrcIndustryId = csrcIndustryId;
	}

	public List<Long> getCompanyTypeId() {
		return companyTypeId;
	}

	public void setCompanyTypeId(List<Long> companyTypeId) {
		this.companyTypeId = companyTypeId;
	}

	public List<Integer> getDataType() {
		return dataType;
	}

	public void setDataType(List<Integer> dataType) {
		this.dataType = dataType;
	}

	public List<String> getPublishSite() {
		return publishSite;
	}

	public void setPublishSite(List<String> publishSite) {
		this.publishSite = publishSite;
	}

	public List<Integer> getRelevance() {
		return relevance;
	}

	public void setRelevance(List<Integer> relevance) {
		this.relevance = relevance;
	}

	public List<String> getRelevanceName() {
		return relevanceName;
	}

	public void setRelevanceName(List<String> relevanceName) {
		this.relevanceName = relevanceName;
	}

	public List<Long> getSentimental() {
		return sentimental;
	}

	public void setSentimental(List<Long> sentimental) {
		this.sentimental = sentimental;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<String> getLevel1() {
		return level1;
	}

	public void setLevel1(List<String> level1) {
		this.level1 = level1;
	}

	public List<String> getLevel2() {
		return level2;
	}

	public void setLevel2(List<String> level2) {
		this.level2 = level2;
	}

	public List<String> getLevel3() {
		return level3;
	}

	public void setLevel3(List<String> level3) {
		this.level3 = level3;
	}

	public Boolean getNeedDaily() {
		return needDaily;
	}

	public void setNeedDaily(Boolean needDaily) {
		this.needDaily = needDaily;
	}

	public Long getIssueBonds() {
		return issueBonds;
	}

	public void setIssueBonds(Long issueBonds) {
		this.issueBonds = issueBonds;
	}

	public List<String> getMarketNames() {
		return marketNames;
	}

	public void setMarketNames(List<String> marketNames) {
		this.marketNames = marketNames;
	}

	public Long getNewOTCMarket() {
		return newOTCMarket;
	}

	public void setNewOTCMarket(Long newOTCMarket) {
		this.newOTCMarket = newOTCMarket;
	}

	public Long getPpType() {
		return ppType;
	}

	public void setPpType(Long ppType) {
		this.ppType = ppType;
	}

	public Long getShareType() {
		return shareType;
	}

	public void setShareType(Long shareType) {
		this.shareType = shareType;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getRelevanceType() {
		return relevanceType;
	}

	public void setRelevanceType(Long relevanceType) {
		this.relevanceType = relevanceType;
	}
    
    

}
