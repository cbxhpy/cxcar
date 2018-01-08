package com.enation.eop.processor;

import com.enation.eop.processor.core.Request;

/**
 * @author kingapex
 * @version 1.0
 * @created 22-十月-2009 18:12:27
 */
public abstract class AbstractFacadePageWrapper extends AbstractWrapper {

	protected FacadePage page;

	/**
	 * 
	 * @param page
	 */
	public AbstractFacadePageWrapper(FacadePage page, Request request) {
		super(request);
		this.page = page;
	}

}