package com.hong.es.entity;

import java.time.LocalDateTime;
import java.util.Date;

public class NewsLabelJX {
	    private Long id;
	    private Long newsBasicinfoSid;

	    private Long companyId;

	    private String label;

	    private Long isDel;

	    private Date createDate;

	    private Date updateDate;
	    
	    private String updateDateStr;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Long getNewsBasicinfoSid() {
			return newsBasicinfoSid;
		}

		public void setNewsBasicinfoSid(Long newsBasicinfoSid) {
			this.newsBasicinfoSid = newsBasicinfoSid;
		}

		public Long getCompanyId() {
			return companyId;
		}

		public void setCompanyId(Long companyId) {
			this.companyId = companyId;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public Long getIsDel() {
			return isDel;
		}

		public void setIsDel(Long isDel) {
			this.isDel = isDel;
		}

		public Date getCreateDate() {
			return createDate;
		}

		public void setCreateDate(Date createDate) {
			this.createDate = createDate;
		}

		public Date getUpdateDate() {
			return updateDate;
		}

		public void setUpdateDate(Date updateDate) {
			this.updateDate = updateDate;
		}

		public String getUpdateDateStr() {
			return updateDateStr;
		}

		public void setUpdateDateStr(String updateDateStr) {
			this.updateDateStr = updateDateStr;
		}

		 
}
