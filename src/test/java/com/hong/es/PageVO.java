package com.hong.es;

import java.util.List;

public class PageVO<T> {

	private static final long serialVersionUID = 1L;
	
    private Long totalCount;

    private Integer page;

    private Integer size;

    private List<T> items;

	public Long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
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

	public List<T> getItems() {
		return items;
	}

	public void setItems(List<T> items) {
		this.items = items;
	}

	@Override
	public String toString() {
		return "PageVO{" +
				"totalCount=" + totalCount +
				", page=" + page +
				", size=" + size +
				", items=" + items +
				'}';
	}
}
