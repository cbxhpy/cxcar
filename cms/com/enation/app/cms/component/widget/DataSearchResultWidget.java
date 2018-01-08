package com.enation.app.cms.component.widget;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.cms.component.widget.pager.SearchPagerHtmlBuilder;
import com.enation.app.cms.core.service.IDataManager;
import com.enation.eop.sdk.widget.AbstractWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

/**
 * 数据搜索挂件
 * @author kingapex
 * 2010-7-15下午09:32:37
 */
@Component("dataSearchResult")
@Scope("prototype")
public class DataSearchResultWidget extends AbstractWidget {

	private IDataManager dataManager;
	private String catid;
	@Override
	protected void config(Map<String, String> params) {

	}

	@Override
	protected void display(Map<String, String> params) {
		String pager = params.get("pager"); // on开启，off关闭
		pager = StringUtil.isEmpty(pager) ? "on" : pager;
		Integer modelid = Integer.valueOf(params.get("modelid"));

		String pageSizeParam = params.get("pagesize");
		int pageSize = StringUtil.isEmpty(pageSizeParam) ? 20 : Integer.valueOf(pageSizeParam);

		String connector = params.get("connector");
		if(connector==null) {
			connector = " and ";
		}
		
		String outname = params.get("outname");
		if(outname==null)
			outname = "dataList";
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String pageParam = request.getParameter("page");
		int pageNo = StringUtil.isEmpty(pageParam) ? 1 : Integer.valueOf(pageParam);
		
		String showchilds = params.get("showchild");// 是否显示子站数据 yes/no
		boolean showchild = showchilds == null ? false : (showchilds.trim().toUpperCase().equals("YES"));
		
		if ("on".equals(pager)) {
			Page dataPage = dataManager.search(pageNo, pageSize, modelid, connector, catid);
			List dataList = (List) dataPage.getResult();
			this.putData(outname, dataList);
			
			SearchPagerHtmlBuilder pagerHtmlBuilder = new SearchPagerHtmlBuilder(pageNo, dataPage.getTotalCount(), pageSize);
			String pagerHtml = pagerHtmlBuilder.buildPageHtml();
			this.putData("pager", pagerHtml);
		} else {
			List dataList = dataManager.search(modelid, connector);
			this.putData(outname, dataList);

			this.putData("pager", "<!--不分页-->");
		}
	}
	
	public IDataManager getDataManager() {
		return dataManager;
	}

	public void setDataManager(IDataManager dataManager) {
		this.dataManager = dataManager;
	}

	public String getCatid() {
		return catid;
	}

	public void setCatid(String catid) {
		this.catid = catid;
	}

}
