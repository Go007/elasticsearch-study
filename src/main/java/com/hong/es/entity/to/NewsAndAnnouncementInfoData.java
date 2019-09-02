package com.hong.es.entity.to;

import java.util.List;

public class NewsAndAnnouncementInfoData extends BaseInData {
    private int dataType;
    private List<String> sentimentalList;
    private List<String> companyIdList;
    private List<String> label1List;
    private List<String> label2List;
    private int relevancyLevel;
    private String keyword;
    //0/1/2:全部/本企业公告/提及本企业公告
    private int announcementType;
    //时间区间类型
    private int timeRange;

    //来源过滤

    private String sourceType;

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public List<String> getSentimentalList() {
        return sentimentalList;
    }

    public void setSentimentalList(List<String> sentimentalList) {
        this.sentimentalList = sentimentalList;
    }

    public List<String> getCompanyIdList() {
        return companyIdList;
    }

    public void setCompanyIdList(List<String> companyIdList) {
        this.companyIdList = companyIdList;
    }

    public List<String> getLabel1List() {
        return label1List;
    }

    public void setLabel1List(List<String> label1List) {
        this.label1List = label1List;
    }

    public List<String> getLabel2List() {
        return label2List;
    }

    public void setLabel2List(List<String> label2List) {
        this.label2List = label2List;
    }

    public int getRelevancyLevel() {
        return relevancyLevel;
    }

    public void setRelevancyLevel(int relevancyLevel) {
        this.relevancyLevel = relevancyLevel;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getAnnouncementType() {
        return announcementType;
    }

    public void setAnnouncementType(int announcementType) {
        this.announcementType = announcementType;
    }

    public int getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(int timeRange) {
        this.timeRange = timeRange;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }
}