package com.hong.es.entity.to;

import java.util.List;
import java.util.Map;

public class NewsOut {
    private List<Map<String, Object>> subjectWords;
    private String infoSid;
    private String infoCd;
    private String sourceType;
    private String publishSite;
    private String dataTitle;
    private String content;
    private String author;
    private String srcUrl;
    private String dataType;
    private String noticeDt;
    private String simHash;
    private String updtDt;
    private String isdel;
    private List<Map<String,Object>> relatedcompyarray;
    private List<Map<String,Object>> recommendNews;
    //高亮内容
    private String highlightContent;
    //高亮字段
    private List highlightColumns;

    public String getHighlightContent() {
        return highlightContent;
    }

    public void setHighlightContent(String highlightContent) {
        this.highlightContent = highlightContent;
    }

    public List getHighlightColumns() {
        return highlightColumns;
    }

    public void setHighlightColumns(List highlightColumns) {
        this.highlightColumns = highlightColumns;
    }

    public List<Map<String, Object>> getRecommendNews() {
        return recommendNews;
    }

    public void setRecommendNews(List<Map<String, Object>> recommendNews) {
        this.recommendNews = recommendNews;
    }

    public List<Map<String, Object>> getSubjectWords() {
        return subjectWords;
    }

    public void setSubjectWords(List<Map<String, Object>> subjectWords) {
        this.subjectWords = subjectWords;
    }

    public String getInfoSid() {
        return infoSid;
    }

    public void setInfoSid(String infoSid) {
        this.infoSid = infoSid;
    }

    public String getInfoCd() {
        return infoCd;
    }

    public void setInfoCd(String infoCd) {
        this.infoCd = infoCd;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getPublishSite() {
        return publishSite;
    }

    public void setPublishSite(String publishSite) {
        this.publishSite = publishSite;
    }

    public String getDataTitle() {
        return dataTitle;
    }

    public void setDataTitle(String dataTitle) {
        this.dataTitle = dataTitle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSrcUrl() {
        return srcUrl;
    }

    public void setSrcUrl(String srcUrl) {
        this.srcUrl = srcUrl;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getNoticeDt() {
        return noticeDt;
    }

    public void setNoticeDt(String noticeDt) {
        this.noticeDt = noticeDt;
    }

    public String getSimHash() {
        return simHash;
    }

    public void setSimHash(String simHash) {
        this.simHash = simHash;
    }

    public String getUpdtDt() {
        return updtDt;
    }

    public void setUpdtDt(String updtDt) {
        this.updtDt = updtDt;
    }

    public String getIsdel() {
        return isdel;
    }

    public void setIsdel(String isdel) {
        this.isdel = isdel;
    }

    public List<Map<String, Object>> getRelatedcompyarray() {
        return relatedcompyarray;
    }

    public void setRelatedcompyarray(List<Map<String, Object>> relatedcompyarray) {
        this.relatedcompyarray = relatedcompyarray;
    }
}
