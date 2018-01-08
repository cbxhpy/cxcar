package com.enation.eop.processor.httpcache;

/**
 * http缓存状态
 * 
 * @author kingapex
 * @date 2011-11-2 下午5:53:57
 * @version V1.0
 */
public class HttpCacheStatus {

	private boolean isModified;
	private long lasyModified;

	public boolean isModified() {
		return isModified;
	}

	public void setModified(boolean isModified) {
		this.isModified = isModified;
	}

	public long getLasyModified() {
		return lasyModified;
	}

	public void setLasyModified(long lasyModified) {
		this.lasyModified = lasyModified;
	}

}
