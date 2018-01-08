package com.enation.app.cms.component.widget;

import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.cms.core.model.DataCat;
import com.enation.framework.database.Page;
import com.enation.framework.pager.StaticPagerHtmlBuilder;
import com.enation.framework.util.StringUtil;

/**
 * 动态数据列表挂件<br/>
 * 数据分类和页数由地址栏获取
 * @author kingapex
 * 2010-7-6上午11:53:44
 */
@Component("dynamicDataList")
@Scope("prototype")
public class DynamicDataListWidget extends RequestParamWidget {
	
	@Override
	protected void config(Map<String, String> params) {
		
	}

	@Override
	protected void display(Map<String, String> params) {
		String pageSize = params.get("pagesize");
		String pageNoStr = params.get("pageno");
		String term = params.get("term");
		pageSize = StringUtil.isEmpty(pageSize) ? "20" : pageSize;
		Integer[] ids = this.parseId();
		Integer catid = ids[1];
		Integer pageNo = StringUtil.isEmpty(pageNoStr) ? ids[0] : Integer.valueOf(pageNoStr);

		String showchilds = params.get("showchild");// 是否显示子站数据 yes/no
		boolean showchild = showchilds == null ? false : (showchilds.trim().toUpperCase().equals("YES"));

		String orders = params.get("orders");
		
		Page webpage = dataManager.listAll(catid, term, orders, showchild, pageNo, Integer.valueOf(pageSize));
		
		
		StaticPagerHtmlBuilder pagerHtmlBuilder = new StaticPagerHtmlBuilder(pageNo, webpage.getTotalCount(), Integer.valueOf(pageSize));
		String page_html = pagerHtmlBuilder.buildPageHtml();
		
		long totalPageCount = webpage.getTotalPageCount();
		long totalCount = webpage.getTotalCount();
		this.putData("dataList", webpage.getResult());
		this.putData("pager", page_html);
		this.putData("pagesize", pageSize);
		this.putData("pageno", pageNo);
		this.putData("totalcount", totalCount);
		this.putData("totalpagecount", totalPageCount);
		
		/**
		 * Liuzy add(2012-10-28)，解决下面挂件读取上面挂件数据问题，不支持自定义名称前，变量名重复
		 */
		String customName = params.get("custom_name");
		if (customName != null)
			this.putData(customName, webpage.getResult());
		
		//读取父树
		List<DataCat> parents = this.dataCatManager.getParents(catid);
		DataCat cat = parents.get(parents.size() - 1); // 最后一个为此类别本身
		//给页面设置当前类别名称
		this.putData("catname", cat.getName());
		this.putData("cat", cat);

		//<span><a href='http://www.bjmingtao.com/'>首页</a> > <a href='news.html'>铭滔要闻</a> > ${catname}</a></span>
		StringBuffer navBar = new StringBuffer();
		navBar.append("<a href='index.html'>首页</a>");
		for (DataCat c : parents) {
			navBar.append("> <a href='" + c.getUrl() + "'>" + c.getName() + "</a>");
		}
		this.putData("navbar", navBar.toString());
	}

}
