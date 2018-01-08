package com.enation.app.shop.core.service.impl;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.Delivery;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.service.IMemberOrderManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.user.IUserService;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;


public class MemberOrderManager extends BaseSupport implements
		IMemberOrderManager {
	
	@Override
	public Page pageOrders(int pageNo, int pageSize) {
		IUserService userService = UserServiceFactory.getUserService();
		Member member = userService.getCurrentMember();
		
		String sql = "select * from order where member_id = ? and disabled=0 order by create_time desc";
		Page rpage = this.baseDaoSupport.queryForPage(sql, pageNo, pageSize, member.getMember_id());
		List<Map> list = (List<Map>) (rpage.getResult());
		return rpage;
	}
	
	@Override
	public Page getPageOrder(String status_str, String member_id,
			String page_no, String page_size) {
		
		StringBuffer sql = new StringBuffer();
		
		sql.append(" select eo.* from es_order eo where eo.member_id = ? and eo.disabled = 0 ");
		if(!StringUtil.isEmpty(status_str)){
			sql.append(" and eo.status in (").append(status_str).append(") ");
		}
		sql.append(" order by eo.order_id desc ");
		
		Page orderPage = this.baseDaoSupport.queryForPage(sql.toString(), Integer.parseInt(page_no), Integer.parseInt(page_size), member_id);
		
		return orderPage;
	}
	
	@Override
	public Page getSellerOrderPage(String status_str, String member_id,
			String page_no, String page_size) {
		
		StringBuffer sql = new StringBuffer();
		
		sql.append(" select eo.order_id, eo.sn, eo.seller_id, eoi.goods_id serve_id, eo.status, eo.create_time, es.seller_name, ");
		sql.append(" eoi.name serve_name, eo.order_amount, eoi.image, eoi.price, eoi.num from es_order eo ");
		sql.append(" left join es_order_items eoi on eo.order_id = eoi.order_id ");
		sql.append(" left join es_seller es on es.seller_id = eo.seller_id ");
		sql.append(" where eo.member_id = ? and eo.disabled = 0 ");
		
		if(!StringUtil.isEmpty(status_str)){
			sql.append(" and eo.status in (").append(status_str).append(") ");
		}
		sql.append(" order by eo.order_id desc ");
		
		Page orderPage = this.baseDaoSupport.queryForPage(sql.toString(), Integer.parseInt(page_no), Integer.parseInt(page_size), member_id);
		
		return orderPage;
	}
	
	@Override
	public Map<String, Object> getSellerOrderDetail(String order_id) {
		
		StringBuffer sql = new StringBuffer();
		
		sql.append(" select eo.order_id, eo.sn, eo.seller_id, eoi.goods_id serve_id, eo.status, eo.create_time, es.seller_name, ");
		sql.append(" es.seller_lng, es.seller_lat, es.phone, es.address, es.score, eg.introduce serve_content, em.bind_phone, ");
		sql.append(" eoi.name serve_name, eo.order_amount, eoi.image, eoi.price, eoi.num from es_order eo ");
		sql.append(" left join es_order_items eoi on eo.order_id = eoi.order_id ");
		sql.append(" left join es_seller es on es.seller_id = eo.seller_id ");
		sql.append(" left join es_goods eg on eoi.goods_id = eg.goods_id ");
		sql.append(" left join es_member em on em.member_id = eo.member_id ");
		sql.append(" where eo.order_id = ? ");
		
		List<Map<String, Object>> orderList = this.baseDaoSupport.queryForList(sql.toString(), order_id);
		Map<String, Object> orderMap = null;
		if(orderList != null && orderList.size() != 0){
			orderMap = orderList.get(0);
		}
		return orderMap;
	}
	
	@Override
	public Page pageOrders(int pageNo, int pageSize, String status, String keyword){
		IUserService userService = UserServiceFactory.getUserService();
		Member member = userService.getCurrentMember();
		
		String sql = "select * from " + this.getTableName("order") + " where member_id = '" + member.getMember_id() + "' and disabled = 0 ";
		if(!StringUtil.isEmpty(status)){
			int statusNumber = -999;
			statusNumber = StringUtil.toInt(status);
			//等待付款的订单 按付款状态查询
			if(statusNumber==0){
				sql+=" AND status!="+OrderStatus.ORDER_CANCELLATION+" AND pay_status="+OrderStatus.PAY_NO;
			}else{
				sql += " AND status='" + statusNumber + "'";
			}
		}
		if(!StringUtil.isEmpty(keyword)){
			sql += " AND order_id in (SELECT i.order_id FROM " + this.getTableName("order_items") + " i LEFT JOIN "+this.getTableName("order")+" o ON i.order_id=o.order_id WHERE o.member_id='"+member.getMember_id()+"' AND i.name like '%" + keyword + "%')";
		}
		sql += " order by create_time desc";
	 
		Page rpage = this.daoSupport.queryForPage(sql, pageNo, pageSize, Order.class);
		 
		return rpage;
	}
	
	@Override
	public Page pageGoods(int pageNo, int pageSize,String keyword){
		IUserService userService = UserServiceFactory.getUserService();
		Member member = userService.getCurrentMember();
		
		String sql = "select * from goods where goods_id in (SELECT i.goods_id FROM es_order_items i LEFT JOIN order o ON i.order_id=o.order_id WHERE o.member_id='"+member.getMember_id()
				+"' AND o.status in (" + OrderStatus.ORDER_COMPLETE +","+OrderStatus.ORDER_ROG+ " )) ";
		if(!StringUtil.isEmpty(keyword)){
			sql += " AND (sn='" + keyword + "' OR name like '%" + keyword + "%')";
		}
		sql += " order by goods_id desc";
		Page rpage = this.baseDaoSupport.queryForPage(sql, pageNo, pageSize);
		List<Map> list = (List<Map>) (rpage.getResult());
		return rpage;
	}
	
	@Override
	public Delivery getOrderDelivery(int order_id) {
		Delivery delivery = (Delivery)this.baseDaoSupport.queryForObject("select * from delivery where order_id = ?", Delivery.class, order_id);
		return delivery;
	}
	
	@Override
	public List listOrderLog(int order_id) {
		String sql = "select * from order_log where order_id = ?";
		List list = this.baseDaoSupport.queryForList(sql, order_id);
		list = list == null ? new ArrayList() : list;
		return list;
	}
	
	@Override
	public List listGoodsItems(int order_id) {
		String sql = "select * from order_items where order_id = ?";
		List list = this.baseDaoSupport.queryForList(sql, order_id);
		list = list == null ? new ArrayList() : list;
		return list;
	}
	
	@Override
	public List listGiftItems(int orderid) {
		String sql  ="select * from order_gift where order_id=?";
		return this.baseDaoSupport.queryForList(sql, orderid);
	}

	@Override
	public boolean isBuy(int goodsid) {
		IUserService userService = UserServiceFactory.getUserService();
		Member member = userService.getCurrentMember();
		if(member==null) return false;
		String sql  ="select count(0) from " + this.getTableName("order_items") +
					 " where  order_id in(select order_id from "+this.getTableName("order")+" where member_id=?) and goods_id =? ";
		int count  = this.daoSupport.queryForInt(sql, member.getMember_id(),goodsid);
		return count>0;
	}

	

}
