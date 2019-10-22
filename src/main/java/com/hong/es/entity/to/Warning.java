package com.hong.es.entity.to;

import java.util.Date;
import java.util.List;

/**
 * elastic serch 预警实体
 * **/
public class Warning {

    private Long id;

    private Long sid;

    private Long basicinfoSid;

    //预警标题
    private String title;

    //链接
    private String sourceUrl;

    //预警时间
    private Date noticeDate;

    //新闻来源
    private String publishSite;

    //预警分类
    private String typeCode;

    //预警分类
    private String typeName;

    //预警规则id
    private Long warningRegulationSid;

    //数据类型 0 新闻舆情 1 公告 2 company_warnings 3 诚信 4 违规
    private Long dataType;

    private List<RelatedcompanyInfo> relatedcompanyInfo;

    List<Warning> recommendNews;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSid() {
        return sid;
    }

    public void setSid(Long sid) {
        this.sid = sid;
    }

    public Long getBasicinfoSid() {
        return basicinfoSid;
    }

    public void setBasicinfoSid(Long basicinfoSid) {
        this.basicinfoSid = basicinfoSid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public Date getNoticeDate() {
        return noticeDate;
    }

    public void setNoticeDate(Date noticeDate) {
        this.noticeDate = noticeDate;
    }

    public String getPublishSite() {
        return publishSite;
    }

    public void setPublishSite(String publishSite) {
        this.publishSite = publishSite;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Long getWarningRegulationSid() {
        return warningRegulationSid;
    }

    public void setWarningRegulationSid(Long warningRegulationSid) {
        this.warningRegulationSid = warningRegulationSid;
    }

    public Long getDataType() {
        return dataType;
    }

    public void setDataType(Long dataType) {
        this.dataType = dataType;
    }

    public List<RelatedcompanyInfo> getRelatedcompanyInfo() {
        return relatedcompanyInfo;
    }

    public void setRelatedcompanyInfo(List<RelatedcompanyInfo> relatedcompanyInfo) {
        this.relatedcompanyInfo = relatedcompanyInfo;
    }

    public List<Warning> getRecommendNews() {
        return recommendNews;
    }

    public void setRecommendNews(List<Warning> recommendNews) {
        this.recommendNews = recommendNews;
    }
}
