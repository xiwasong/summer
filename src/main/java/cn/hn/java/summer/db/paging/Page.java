package cn.hn.java.summer.db.paging;

import cn.hn.java.summer.constants.Default;

/**
 * 分页对象
 * @author sjg
 * @version 1.0.1 2013-10-24
 *
 */
public class Page {

	/**
	 * 分页大小
	 */
	private int pageSize;
	
	/**
	 * 当前页
	 */
	private int page;

	public Page(){		
	}
	
	public Page(int pageSize,int page){
		this.pageSize=pageSize;
		this.page=page;
	}
	
	public int getPageSize() {
		return pageSize==0?Default.PAGE_SIZE :pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPage() {
		return page==0?1:page;
	}

	public void setPage(int page) {
		this.page = page;
	}
}
