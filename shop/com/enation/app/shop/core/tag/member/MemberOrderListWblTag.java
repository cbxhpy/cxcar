package com.enation.app.shop.core.tag.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.service.IMemberOrderManager;
import com.enation.app.shop.core.service.IMemberPointManger;
import com.enation.app.shop.core.service.IOrderFlowManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IPromotionManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.user.IUserService;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;


/**
 * 会员订单列表标签
 * @author whj
 *2014-02-17下午15:13:00
 */
@Component
@Scope("prototype")
public class MemberOrderListWblTag extends BaseFreeMarkerTag{

	private IMemberOrderManager memberOrderManager;
	
	private IOrderManager orderManager;
	
	private IPromotionManager promotionManager;
	
	private IOrderFlowManager orderFlowManager;
	private IMemberPointManger memberPointManger;
	
	@Override
	public Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();

		IUserService userService = UserServiceFactory.getUserService();
		Member member = userService.getCurrentMember();
		if(member==null){
			throw new TemplateModelException("未登陆不能使用此标签[MemberOrderListTag]");
		}
		Map result = new HashMap();
		
		String page = (String)params.get("page");
		page = (page == null || page.equals("")) ? "1" : page;
		
		int pageSize = 2;
		
		String status = (String)params.get("status");
		String keyword = (String)params.get("keyword");
		
		if(!StringUtil.isEmpty(keyword) && !StringUtil.isEmpty( EopSetting.ENCODING) ){
			keyword = StringUtil.to(keyword,EopSetting.ENCODING);
		}
		
		String status_str = new String();
		status = status == null ? "0" : status;
		if("0".equals(status)){
			
		}
		if("1".equals(status)){//待付款
			status_str = " 0 ";
		}
		if("2".equals(status)){//待发货
			status_str = " 2 ";
		}
		if("3".equals(status)){//待收货
			status_str = " 5 ";
		}
		if("4".equals(status)){//已完成
			status_str = " 7 ";
		}
		
		Page ordersPage = this.memberOrderManager.getPageOrder(status_str, member.getMember_id().toString(), page, pageSize+"");
		Long totalCount = ordersPage.getTotalCount();
		Long tatalPageCount = ordersPage.getTotalPageCount();
		
		List<Map> ordersList = (List<Map>) ordersPage.getResult();
		if(ordersList.size() != 0){
			for(Map orderMap : ordersList){
				if(!StringUtil.isEmpty(StringUtil.isNull(orderMap.get("ship_no")))){
					StringBuffer logiUrl = new StringBuffer();//快递100接口连接
					logiUrl.append("http://m.kuaidi100.com/index_all.html?type=").append(orderMap.get("logi_code"));
					logiUrl.append("&postid=").append(orderMap.get("ship_no")).append("&callbackurl=javascript:location.href=document.referrer;");
					orderMap.put("logi_str", logiUrl.toString());
				}else{
				}
			}
		}
		ordersList = ordersList == null ? new ArrayList() : ordersList;

		result.put("tatalPageCount_"+status, tatalPageCount);
		result.put("totalCount", totalCount);
		result.put("pageSize_"+status, pageSize);
		result.put("page_"+status, page);
		result.put("ordersList", ordersList);

		//Author LiFenLong
		Map<String, Object> orderstatusMap = OrderStatus.getOrderStatusMap();
		for (String orderStatus: orderstatusMap.keySet()) {
			result.put(orderStatus, orderstatusMap.get(orderStatus));
		}
		
		if(!StringUtil.isEmpty(status)){
			result.put("status", Integer.valueOf( status) );
		}
		
		return result;
	}

	public IMemberOrderManager getMemberOrderManager() {
		return memberOrderManager;
	}
	public void setMemberOrderManager(IMemberOrderManager memberOrderManager) {
		this.memberOrderManager = memberOrderManager;
	}

	public IOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}

	public IPromotionManager getPromotionManager() {
		return promotionManager;
	}

	public void setPromotionManager(IPromotionManager promotionManager) {
		this.promotionManager = promotionManager;
	}

	public IOrderFlowManager getOrderFlowManager() {
		return orderFlowManager;
	}

	public void setOrderFlowManager(IOrderFlowManager orderFlowManager) {
		this.orderFlowManager = orderFlowManager;
	}

	public IMemberPointManger getMemberPointManger() {
		return memberPointManger;
	}

	public void setMemberPointManger(IMemberPointManger memberPointManger) {
		this.memberPointManger = memberPointManger;
	}
	
	

}
