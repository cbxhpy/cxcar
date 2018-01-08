package com.enation.app.cms.component.widget;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.cms.core.service.IDataCatManager;
import com.enation.app.cms.core.service.IDataManager;
import com.enation.eop.sdk.widget.AbstractWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.RequestUtil;

@Component("relatedData")
@Scope("prototype")
public abstract class RequestParamWidget extends AbstractWidget {
	protected IDataCatManager dataCatManager;
	protected IDataManager dataManager;

	protected Integer[] parseId() {
		HttpServletRequest httpRequest = ThreadContextHolder.getHttpRequest();
		String url = RequestUtil.getRequestUrl(httpRequest);
		String pattern = "/(.*)-(\\d+)-(\\d+).html(.*)";
		String page = null;
		String catid = null;
		Pattern p = Pattern.compile(pattern, 2 | Pattern.DOTALL);
		Matcher m = p.matcher(url);
		if (m.find()) {
			page = m.replaceAll("$3");
			catid = m.replaceAll("$2");
			return new Integer[] { Integer.valueOf("" + page), Integer.valueOf("" + catid) };
		}
		return null;
	}

	public IDataCatManager getDataCatManager() {
		return dataCatManager;
	}

	public void setDataCatManager(IDataCatManager dataCatManager) {
		this.dataCatManager = dataCatManager;
	}

	public IDataManager getDataManager() {
		return dataManager;
	}

	public void setDataManager(IDataManager dataManager) {
		this.dataManager = dataManager;
	}

}
