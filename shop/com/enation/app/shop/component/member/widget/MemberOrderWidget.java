package com.enation.app.shop.component.member.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.service.IMemberOrderManager;
import com.enation.app.shop.core.service.IMemberPointManger;
import com.enation.app.shop.core.service.IOrderFlowManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IPromotionManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.user.IUserService;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.eop.sdk.widget.AbstractMemberWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

/**
 * 我的订单列表挂件
 * 
 * @author kingapex
 * 
 */
@Component("member_order")
@Scope("prototype")
public class MemberOrderWidget extends AbstractMemberWidget {

	private IMemberOrderManager memberOrderManager;
	
	private IOrderManager orderManager;
	
	private IPromotionManager promotionManager;
	
	private IOrderFlowManager orderFlowManager;
	private IMemberPointManger memberPointManger;

	@Override
	protected void display(Map<String, String> params) {
	
		if (action == null || action.equals("") || action.equals("list")) { //显示订单列表
			action = "list";
			this.list();
		}else if(action.equals("cancel")){ //显示取消订单界面
			this.cancel();
		}else if(action.equals("savecancel")){ //保存取消订单
			 this.saveCancle();
		}else if(action.equals("confirmRog")){ //确认收货
			this.confirmRog();
		}else if(action.equals("restore")){ //还原订单
			this.restore();
		}else if(action.equals("thaw")){
			this.thaw();
		}
		
	
	} 

	
	
	private void thaw(){
		try{
			HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
			int orderid = StringUtil.toInt(request.getParameter("orderid"),true);
			this.memberPointManger.thaw(orderid);
			this.showSuccessJson("成功解冻");
		}catch(RuntimeException e){
			this.logger.error("手动解冻积分"+ e.getMessage(),e);
			this.showErrorJson(e.getMessage());
		}
	}
	
	
	private void list(){
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String page = request.getParameter("page");
		page = (page == null || page.equals("")) ? "1" : page;
		this.setPageName("order_list");
		int pageSize = 10;

		String status = request.getParameter("status");
		String keyword = request.getParameter("keyword");
		if(!StringUtil.isEmpty(keyword) && !StringUtil.isEmpty( EopSetting.ENCODING) ){
			
			keyword = StringUtil.to(keyword,EopSetting.ENCODING);
		}
		
		Page ordersPage = memberOrderManager.pageOrders(
				Integer.valueOf(page), pageSize,status,keyword);
		Long totalCount = ordersPage.getTotalCount();

		List ordersList = (List) ordersPage.getResult();
		ordersList = ordersList == null ? new ArrayList() : ordersList;

		this.putData("totalCount", totalCount);
		this.putData("pageSize", pageSize);
		this.putData("page", page);
		this.putData("ordersList", ordersList);
		if(status!=null){
			this.putData("status",Integer.valueOf( status) );
		}
		this.putData("keyword",keyword);
		this.putData(OrderStatus.getOrderStatusMap());
	}
	
	/**
	 * 显示取消订单的界面
	 */
	private void cancel(){
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		 		
		IUserService userService = UserServiceFactory.getUserService();
		if(userService.getCurrentMember() == null){
			this.showError("对不起，您没有权限进行此项操作！");
			return;
		}			
		String sn = request.getParameter("sn");
		String from = request.getParameter("from");
		Order order = orderManager.get(sn);
		if(order == null){
			this.showError("对不起，此订单不存在！");
			return;
		}
		if(order.getStatus() == null || order.getStatus().intValue() != 0){
			this.showError("对不起，此订单不能取消！");
			return;
		}
		if(order.getMember_id().intValue() != userService.getCurrentMember().getMember_id().intValue()){
			this.showError("对不起，您没有权限进行此项操作！");
			return;
		}
		this.putData("sn",sn);
		this.putData("from",from);
		this.setActionPageName("order_cancel");
	}
	
	
	private void saveCancle(){
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String ajax = request.getParameter("ajax");
		try{

			String sn = request.getParameter("sn");
			String cancel_reason = request.getParameter("cancel_reason");
			this.orderFlowManager.cancel(sn, cancel_reason);
			Member member  = UserServiceFactory.getUserService().getCurrentMember();
			
			if(StringUtil.isEmpty(ajax)){
			
				if(member!=null){
					this.showSuccess("订单取消成功","查看此订单","member_orderdetail_"+sn+".html");
				}
				else{
					this.showSuccess("订单取消成功","查看此订单","orderdetail_"+sn+".html");
				}
			}else{
				this.showSuccessJson("订单取消成功");
			}
			
		}catch(RuntimeException re){
			if(StringUtil.isEmpty(ajax)){
				this.showError(re.getMessage());
			}else{
				this.showErrorJson(re.getMessage());
			}
		
		}
	 
	}
	
	
	/**
	 * 还原订单		
	 */
	private void restore(){
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();	
		IUserService userService = UserServiceFactory.getUserService();
		if(userService.getCurrentMember() == null){
			this.showErrorJson("对不起，您没有权限进行此项操作！");
			return;
		}			
		String sn = request.getParameter("sn");
		String from = request.getParameter("from");
		Order order = orderManager.get(sn);
		if(order == null){
			this.showErrorJson("对不起，此订单不存在！");
			return;
		}
		if(order.getStatus() == null || order.getStatus().intValue() != 8){
			this.showErrorJson("对不起，此订单不能还原！");
			return;
		}
		if(order.getMember_id().intValue() != userService.getCurrentMember().getMember_id().intValue()){
			this.showErrorJson("对不起，您没有权限进行此项操作！");
			return;
		}
		try{
			this.orderFlowManager.restore(sn);
			this.showSuccessJson("还原订单成功！");
		}catch(RuntimeException re){
			this.showErrorJson(re.getMessage());
			return;
		}

	}
	
	/**
	 * 确认收货
	 */
	private void confirmRog(){
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();	
		String orderId = request.getParameter("orderId");
		Member member  = UserServiceFactory.getUserService().getCurrentMember();
		
		if(member!=null){
			this.orderFlowManager.rogConfirm(Integer.parseInt(orderId), member.getMember_id(), member.getUname(), member.getUname(),DateUtil.getDatelineLong());
		}else{
			this.orderFlowManager.rogConfirm(Integer.parseInt(orderId), null,"游客", "游客",DateUtil.getDatelineLong());
		}
		this.showSuccessJson("确认收货成功！");
	}
	
	@Override
	protected void config(Map<String, String> params) {

	}

	public IMemberOrderManager getMemberOrderManager() {
		return memberOrderManager;
	}

	public void setMemberOrderManager(IMemberOrderManager memberOrderManager) {
		this.memberOrderManager = memberOrderManager;
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


	public IOrderManager getOrderManager() {
		return orderManager;
	}



	public IMemberPointManger getMemberPointManger() {
		return memberPointManger;
	}



	public void setMemberPointManger(IMemberPointManger memberPointManger) {
		this.memberPointManger = memberPointManger;
	}

}
