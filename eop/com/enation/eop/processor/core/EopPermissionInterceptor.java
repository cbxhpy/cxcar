package com.enation.eop.processor.core;

import com.enation.eop.sdk.context.EopContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

/**
 * eop权限拦截器
 * 
 * @author kingapex 2010-10-23下午11:55:03
 */
public class EopPermissionInterceptor implements Interceptor {

	@Override
	public void destroy() {
		
	}

	@Override
	public void init() {
		
	}

	@Override
	public String intercept(ActionInvocation inv) throws Exception {
		Integer userid = EopContext.getContext().getCurrentSite().getUserid();
		if (userid.intValue() != 1) {
			return "error";
		}
		String result = inv.invoke();
		return result;
	}

}
