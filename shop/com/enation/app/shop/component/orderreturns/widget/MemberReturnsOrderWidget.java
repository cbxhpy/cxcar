package com.enation.app.shop.component.orderreturns.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.component.orderreturns.service.IReturnsOrderManager;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.OrderLog;
import com.enation.app.shop.core.model.ReturnsOrder;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.OrderItemStatus;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.eop.sdk.widget.AbstractMemberWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

/**
 * 会员退货挂件
 * 
 * @author kingapex ;modify by dable(2011.9.19)
 * 
 */
@Component
@Scope("prototype")
public class MemberReturnsOrderWidget extends AbstractMemberWidget {

	private IReturnsOrderManager returnsOrderManager;
	private IOrderManager orderManager;
	private IGoodsManager goodsManager;
	
	@Override
	protected void config(Map<String, String> params) {
	}

	@Override
	protected void display(Map<String, String> params) {
		this.setMenu("returns_order");
		if(StringUtil.isEmpty(action)){
			this.showError("非法地址");
		}
		else if(action.equals("goodslist")){
			this.listGoods();
		}
 
		else if (action.equals("list")) {
			this.list();
		}

		else if (action.equals("apply")) {
			this.apply();
		}

		//申请验证
		else if (action.equals("add")) {
			this.add();
		}
	}


	private void listGoods(){
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String returnOrderSn = request.getParameter("returnOrderSn");
		if(returnOrderSn==null||"".equals(returnOrderSn)){
			this.showError("订单号为空！");
			return;
		}
		Order order = orderManager.get(returnOrderSn);
		String goodsSn = returnsOrderManager.getSnByOrderSn(order.getSn());
		String[] goodSn = null;
		List goodIdS = new ArrayList();
		if (goodsSn != null && !goodsSn.equals("")){
			goodSn = StringUtils.split(goodsSn,",");
			for (int j = 0; j < goodSn.length; j++) {
				if(goodSn[j].indexOf("-") != -1){
					goodSn[j]=goodSn[j].substring(0, goodSn[j].indexOf("-"));
				}
			}
			for (int i = 0; i < goodSn.length; i++) {
				goodIdS.add(goodsManager.getGoodBySn(goodSn[i]).getGoods_id());
			}
		}
		List orderItemsList =orderManager.listGoodsItems(order.getOrder_id());
		ReturnsOrder tempReturnsOrder = this.returnsOrderManager.getByOrderSn(order.getSn());
		this.putData("orders", order);
		this.putData("orderItemsList", orderItemsList);
		this.putData("goodIdS", goodIdS);
		this.putData("returnOrder",tempReturnsOrder);
		this.setActionPageName("returns_order_detail");
	}
	
	private void apply() {
	//转跳到申请页
		this.setActionPageName("apply");
	}

	/**
	 * 申请提交方法
	 */
	private void add() {
		this.showMenu(false);
		String pic_path = UploadUtil.uploadUseRequest("pic", "order");
		if(StringUtil.isEmpty(pic_path)){
			this.showError("请您上传需要退换的商品图片！");
			return;
		}
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		Integer type1 = Integer.valueOf(request.getParameter("type1"));
		String orderSn = request.getParameter("ordersn");
		Order order = this.orderManager.get(orderSn);
		Member member = UserServiceFactory.getUserService().getCurrentMember();
		Integer member_id;
		if(order==null){
			this.showError("此订单不存在！");
			return;
		}else{
			if(order.getStatus().intValue()!=OrderStatus.ORDER_SHIP&&order.getStatus().intValue()!=OrderStatus.ORDER_ROG){
				this.showError("您的订单还没有发货！");
				return;
			}
			
			member_id = order.getMember_id();
			if (!member_id.equals(member.getMember_id())){
				this.showError("此订单号不是您的订单号！");
				return;
			}else{
				Integer memberid = member_id;
			}
		}
		ReturnsOrder tempReturnsOrder = this.returnsOrderManager.getByOrderSn(orderSn);
		if(tempReturnsOrder != null){
			this.showError("此订单已经申请过退货或换货，不能再申请！");
			return;
		}
		String applyReason = request.getParameter("applyreason");
		String goods = request.getParameter("goodsns");
		String[] goodsns;
		if (goods != null&&!goods.equals("")){
			goodsns = StringUtils.split(goods,",");
		}else{
			this.showError("您填写的货号为空！");
			return;
		}
		List<Map> items = orderManager.getItemsByOrderid(order.getOrder_id());
		if(items==null){
			this.showError("您的订单下没有货物！");
			return;
		}
		List<String> goodSnUnderOrder=new ArrayList<String>();
		for (int i = 0; i < items.size(); i++) {
			goodSnUnderOrder.add(goodsManager.getGoods((Integer)items.get(i).get("goods_id")).getSn());
		}
		for (int j = 0; j < goodsns.length; j++) {
			if(goodsns[j].indexOf("-") != -1){
				goodsns[j]=goodsns[j].substring(0, goodsns[j].indexOf("-"));
			}
		}
		for (int j = 0; j < goodsns.length; j++) {
			if(!goodSnUnderOrder.contains(goodsns[j])){
				this.showError("您所填写的所有货物号必须属于一个订单中！");
				return;
			}else{
				continue;
			}
		}
		int[] goodids=new int[goodsns.length];;
		if(goodsns!=null){
			for (int i = 0; i < goodsns.length; i++) {
				goodids[i]=this.goodsManager.getGoodBySn(goodsns[i]).getGoods_id();
			}
		}
		ReturnsOrder returnOrder=new ReturnsOrder();
		returnOrder.setPhoto(pic_path);
		returnOrder.setGoodsns(goods);
		returnOrder.setMemberid(member_id);
		returnOrder.setOrdersn(orderSn);
		returnOrder.setApply_reason(applyReason);
		returnOrder.setType(type1);
		int orderid=orderManager.get(orderSn).getOrder_id();
		//写订单退换货日志
		OrderLog log = new OrderLog();
		if(type1==1){
			log.setMessage("用户"+member.getUname()+"申请退货");
			log.setOp_name(member.getUname());
			log.setOp_id(member.getMember_id());
			log.setOrder_id(order.getOrder_id());
			this.returnsOrderManager.add(returnOrder,orderid,OrderItemStatus.APPLY_RETURN,goodids);	
			this.returnsOrderManager.addLog(log);
		}
		if(type1==2){
			log.setMessage("用户"+member.getUname()+"申请换货");
			log.setOp_name(member.getUname());
			log.setOp_id(member.getMember_id());
			log.setOrder_id(order.getOrder_id());
			this.returnsOrderManager.add(returnOrder,orderid,OrderItemStatus.APPLY_CHANGE,goodids);			
			this.returnsOrderManager.addLog(log);
		}
		this.showSuccess("申请已提交，我们会在2个工作日内处理您的请求！","退换申请","member_returns_order.html?action=list");
	}

	/**
	 * 退换货列表方法
	 */
	private void list() {
//		Member member = UserServiceFactory.getUserService().getCurrentMember();
		 HttpServletRequest request =  ThreadContextHolder.getHttpRequest();
		 String ropage  = request.getParameter("ropage");
		 ropage = (ropage == null || ropage.equals("")) ? "1" : ropage;
		
		 int pageSize = 10;
		 Page returnOrderPage = returnsOrderManager.pageReturnOrder(Integer.valueOf(ropage), pageSize);
		 Long totalCount = returnOrderPage.getTotalCount();
		 List returnOrderList = (List)returnOrderPage.getResult();
		 returnOrderList = returnOrderList == null ? new ArrayList() : returnOrderList;
		 this.putData("totalCount", totalCount);
		 this.putData("pageSize", pageSize);
		 this.putData("ropage", ropage);
		 this.putData("returnOrderList", returnOrderList);
		 this.setActionPageName("returns_order_list");
	}

	
	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}

	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}

	public IReturnsOrderManager getReturnsOrderManager() {
		return returnsOrderManager;
	}

	public void setReturnsOrderManager(IReturnsOrderManager returnsOrderManager) {
		this.returnsOrderManager = returnsOrderManager;
	}

	public IOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}

}
