/*
============================================================================
版权所有 (C) 2008-2010 易族智汇（北京）科技有限公司，并保留所有权利。
网站地址：http://www.javamall.com.cn

您可以在完全遵守《最终用户授权协议》的基础上，将本软件应用于任何用途（包括商
业用途），而不必支付软件版权授权费用。《最终用户授权协议》可以从我们的网站下载；
如果担心法律风险，您也可以联系我们获得书面版本的授权协议。在未经授权情况下不
允许对程序代码以任何形式任何目的的修改或再发布。
============================================================================
*/
package com.enation.app.shop.component.goodscore.widget.goods.search2.pager;

import com.enation.app.shop.core.utils.UrlUtils;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.pager.AbstractPageHtmlBuilder;
import com.enation.framework.util.StringUtil;

public class SearchPagerHtmlBuilder extends AbstractPageHtmlBuilder {

	public SearchPagerHtmlBuilder(long _pageNum, long _totalCount, int _pageSize) {
		super(_pageNum, _totalCount, _pageSize);
	}

	/**
	 * 计算并初始化信息
	 *
	 */
	@Override
	protected void init() {	
		pageSize = pageSize < 1 ? 1 : pageSize;

		pageCount = totalCount / pageSize;
		pageCount = totalCount % pageSize > 0 ? pageCount + 1 : pageCount;

		pageNum = pageNum > pageCount ? pageCount : pageNum;
		pageNum = pageNum < 1 ? 1 : pageNum;

		url = request.getServletPath();

		if (!StringUtil.isEmpty(EopSetting.ENCODING)) {
			url = StringUtil.to(url, EopSetting.ENCODING);
		}
		
		url = UrlUtils.getParamStr(url);
		url = UrlUtils.getExParamUrl(url, "page");
	}
	
	/**
	 * 根据页数生成超级连接href的字串
	 * @param page
	 * @return
	 */
	@Override
	protected String getUrlStr(long page) {
		String page_url;
		String ex_param = UrlUtils.getExParamUrl(url, "page");
		page_url = "search-" + ("".equals(ex_param) ? "" : "-");
		page_url += "page-" + page;

		page_url= page_url + ".html";
		
		StringBuffer linkHtml = new StringBuffer();
		linkHtml.append("href='");
		linkHtml.append(page_url);
		linkHtml.append("'>");
		return linkHtml.toString();
	}

}
