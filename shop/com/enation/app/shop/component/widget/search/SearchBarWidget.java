package com.enation.app.shop.component.widget.search;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.widget.AbstractWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.StringUtil;

/**
 * 搜索条挂件
 * @author kingapex
 *2012-3-31下午11:43:38
 */
@Component("searchBar")
@Scope("prototype")
public class SearchBarWidget extends AbstractWidget {

	
	@Override
	protected void config(Map<String, String> params) {

	}

	
	@Override
	protected void display(Map<String, String> params) {
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		String keyword = (String)request.getAttribute("keyword");
		
		String encoding  = EopSetting.ENCODING;
		if(!StringUtil.isEmpty(encoding)){
			keyword = StringUtil.to(keyword,encoding);
		}
		
		this.putData("keyword", keyword);
		this.setPageName("search_bar");
	}

}
