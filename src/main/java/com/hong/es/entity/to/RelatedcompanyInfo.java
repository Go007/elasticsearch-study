package com.hong.es.entity.to;

import java.util.List;

/**
 * 新闻、公告涉及主体信息
 **/
public class RelatedcompanyInfo {

    private Long companyId;

    private String companyName;

    //严重程度代码
    private Long importance;

    //严重程度
    private String importanceName;

    //情感倾向代码
    private Long sentimental;

    //情感倾向
    private String sentimentalName;

    //关联度代码
    private Double relevance;

    //关联度
    private String relevanceName;

    //是否发债
    private Long isBond;

    //是否私募
    private Long ppType;

    private Long shareType;

    private Long newOtcMarket;

    //申万一级id
    private Long swIndustryId;

    //申万一级
    private String swIndustry;

    //证监会大类
    private Long csrcIndustryId;

    //证监会大类
    private String csrcIndustry;

    //敞口
    private String exposure;

    private Long exposureId;

    //企业性质id
    private Long orgFormId;

    //企业性质
    private String orgFormName;

    //企业类型id
    private Long companyTypeId;

    //企业类型
    private String companyType;

    //新闻标题，冗余字段，便于模糊搜索匹配公司名和新闻标题时使用
    private String title;

    //企业类型
    private List<Label> labels;

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Long getImportance() {
        return importance;
    }

    public void setImportance(Long importance) {
        this.importance = importance;
    }

    public String getImportanceName() {
        return importanceName;
    }

    public void setImportanceName(String importanceName) {
        this.importanceName = importanceName;
    }

    public Long getSentimental() {
        return sentimental;
    }

    public void setSentimental(Long sentimental) {
        this.sentimental = sentimental;
    }

    public String getSentimentalName() {
        return sentimentalName;
    }

    public void setSentimentalName(String sentimentalName) {
        this.sentimentalName = sentimentalName;
    }

    public Double getRelevance() {
        return relevance;
    }

    public void setRelevance(Double relevance) {
        this.relevance = relevance;
    }

    public String getRelevanceName() {
        return relevanceName;
    }

    public void setRelevanceName(String relevanceName) {
        this.relevanceName = relevanceName;
    }

    public Long getIsBond() {
        return isBond;
    }

    public void setIsBond(Long isBond) {
        this.isBond = isBond;
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

    public Long getNewOtcMarket() {
        return newOtcMarket;
    }

    public void setNewOtcMarket(Long newOtcMarket) {
        this.newOtcMarket = newOtcMarket;
    }

    public Long getSwIndustryId() {
        return swIndustryId;
    }

    public void setSwIndustryId(Long swIndustryId) {
        this.swIndustryId = swIndustryId;
    }

    public String getSwIndustry() {
        return swIndustry;
    }

    public void setSwIndustry(String swIndustry) {
        this.swIndustry = swIndustry;
    }

    public Long getCsrcIndustryId() {
        return csrcIndustryId;
    }

    public void setCsrcIndustryId(Long csrcIndustryId) {
        this.csrcIndustryId = csrcIndustryId;
    }

    public String getCsrcIndustry() {
        return csrcIndustry;
    }

    public void setCsrcIndustry(String csrcIndustry) {
        this.csrcIndustry = csrcIndustry;
    }

    public String getExposure() {
        return exposure;
    }

    public void setExposure(String exposure) {
        this.exposure = exposure;
    }

    public Long getExposureId() {
        return exposureId;
    }

    public void setExposureId(Long exposureId) {
        this.exposureId = exposureId;
    }

    public Long getOrgFormId() {
        return orgFormId;
    }

    public void setOrgFormId(Long orgFormId) {
        this.orgFormId = orgFormId;
    }

    public String getOrgFormName() {
        return orgFormName;
    }

    public void setOrgFormName(String orgFormName) {
        this.orgFormName = orgFormName;
    }

    public Long getCompanyTypeId() {
        return companyTypeId;
    }

    public void setCompanyTypeId(Long companyTypeId) {
        this.companyTypeId = companyTypeId;
    }

    public String getCompanyType() {
        return companyType;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }

    @Override
    public String toString() {
        return "RelatedcompanyInfo{" +
                "companyId=" + companyId +
                ", companyName='" + companyName + '\'' +
                ", importance=" + importance +
                ", importanceName='" + importanceName + '\'' +
                ", sentimental=" + sentimental +
                ", sentimentalName='" + sentimentalName + '\'' +
                ", relevance=" + relevance +
                ", relevanceName='" + relevanceName + '\'' +
                ", isBond=" + isBond +
                ", ppType=" + ppType +
                ", shareType=" + shareType +
                ", newOtcMarket=" + newOtcMarket +
                ", swIndustryId=" + swIndustryId +
                ", swIndustry='" + swIndustry + '\'' +
                ", csrcIndustryId=" + csrcIndustryId +
                ", csrcIndustry='" + csrcIndustry + '\'' +
                ", exposure='" + exposure + '\'' +
                ", exposureId=" + exposureId +
                ", orgFormId=" + orgFormId +
                ", orgFormName='" + orgFormName + '\'' +
                ", companyTypeId=" + companyTypeId +
                ", companyType='" + companyType + '\'' +
                ", title='" + title + '\'' +
                ", labels=" + labels +
                '}';
    }
}
