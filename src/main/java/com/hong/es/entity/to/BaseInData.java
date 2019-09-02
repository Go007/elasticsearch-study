package com.hong.es.entity.to;

/**
 * Created by qin on 2016/8/9.
 */
public class BaseInData {
    //页码
   private int page;
   //页面显示条数
   private int pageSize;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
