package com.enation.app.shop.component.ordercore.widget;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IOrderReportManager;
import com.enation.app.shop.core.service.IPromotionManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.HeaderConstants;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.widget.AbstractWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

/**
 * 游客订单查询挂件
 * @author kingapex
 *2012-3-31下午11:34:52
 */
@Component("searchorder")
@Scope("prototype")
public class SearchOrderWidget extends AbstractWidget {

	private IOrderManager orderManager;
	private IOrderReportManager orderReportManager;
	private IPromotionManager promotionManager;
	
	@Override
	protected void config(Map<String, String> params) {

	}

	@Override
	protected void display(Map<String, String> params) {
		
		EopSite site = EopContext.getContext().getCurrentSite();
		
		this.putData(HeaderConstants.title, "游客订单查询 - "+ site.getSitename());
		this.putData(HeaderConstants.keywords, "游客订单查询,"+ site.getSitename());
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		if (action == null || action.equals("")) {

		}else if (action.equals("search")) {
			String ship_name = request.getParameter("ship_name");
			if(ship_name != null && !ship_name.equals("")){
				String encoding = EopSetting.ENCODING;
				if(!StringUtil.isEmpty(encoding)){
					ship_name = StringUtil.toUTF8(ship_name);
				}
			}
			String ship_tel = request.getParameter("ship_tel");

			if(StringUtil.isEmpty(ship_name)){
				this.showError("请输入收货人姓名!");
				return;
			}
			if(StringUtil.isEmpty(ship_tel)){
				this.showError("请输入手机或固定号码!");
				return;
			}
			
			String page = request.getParameter("page");
			page = (page == null || page.equals("")) ? "1" : page;
			
			int pageSize = 10;
			
			Page ordersPage = orderManager.searchForGuest(Integer.parseInt(page), pageSize, ship_name, ship_tel);
			
			this.putData("ordersPage",ordersPage);
			this.putData("totalCount",ordersPage.getTotalCount());
			this.putData("ship_name",ship_name);
			this.putData("ship_tel",ship_tel);
			this.putData("pageSize", pageSize);
			this.putData("page", page);
			 this.putData(OrderStatus.getOrderStatusMap());
			this.setActionPageName("order_search");
			
		} 
	}

	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}

	public IOrderReportManager getOrderReportManager() {
		return orderReportManager;
	}

	public void setOrderReportManager(IOrderReportManager orderReportManager) {
		this.orderReportManager = orderReportManager;
	}

	public IPromotionManager getPromotionManager() {
		return promotionManager;
	}

	public void setPromotionManager(IPromotionManager promotionManager) {
		this.promotionManager = promotionManager;
	}

}
