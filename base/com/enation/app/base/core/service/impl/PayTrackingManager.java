package com.enation.app.base.core.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.AdvMapper;
import com.enation.app.base.core.service.IStoreAdminManager;
import com.enation.app.base.core.service.IPayTrackingManager;
import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.shop.core.model.Consume;
import com.enation.app.shop.core.model.PushMsg;
import com.enation.app.shop.core.model.Seller;
import com.enation.app.shop.core.model.PayTracking;
import com.enation.app.shop.core.service.IConsumeManager;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.app.shop.core.service.ISellerManager;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

/**
 * 商家管理
 * @author yexf
 * 2017-4-12
 */
public class PayTrackingManager extends BaseSupport<PayTracking> implements IPayTrackingManager {

	private IAdminUserManager adminUserManager;
	
	private IMemberManager memberManager;
	private IConsumeManager consumeManager;
	private IStoreAdminManager storeAdminManager;
	
	@Override
	public void addSeller(Seller seller) {
		this.baseDaoSupport.insert("es_seller", seller);
	}
	
	@Override
	public void delSeller(Integer[] ids) {
		if (ids == null || ids.equals(""))
			return;
		String id_str = StringUtil.arrayToString(ids, ",");
		String sql = "delete from es_seller where seller_id in (" + id_str
				+ ")";
		this.baseDaoSupport.execute(sql);
	}

	@Override
	public Page pagePushMsg(String order, int page, int pageSize) {
		order = order == null ? " push_msg_id desc " : order;
		String sql = " select * from es_push_msg epm ";
		sql += " order by " + order; 
		Page rpage = this.daoSupport.queryForPage(sql, page, pageSize,new AdvMapper());
		return rpage;
	}
	
	@Override
	public Page pageSeller(String order, String sort, int pageNo, int pageSize) {
		order = order == null ? " es.seller_id " : order;
		sort = sort == null ? " desc " : sort;
		StringBuffer sql = new StringBuffer(" select es.*, ea.username, egc.name cat_name from es_seller es ");
		sql.append(" left join es_adminuser ea on ea.userid = es.user_id ");
		sql.append(" left join es_goods_cat egc on egc.cat_id = es.cat_id where 1 = 1 ");
		AdminUser adminUser = this.adminUserManager.getCurrentUser();
		if(adminUser.getFounder() != 1){
			sql.append(" and ea.userid = ").append(adminUser.getUserid());
		}
		sql.append(" order by ").append(order).append(" ").append(sort); 
		Page rpage = this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize, Seller.class);
		return rpage;
	}
	
	
	@Override
	public Page pagePushMsgByMemberId(String member_id, String order, int page, int pageSize) {
		
		order = order == null ? " push_msg_id desc " : order;
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT epm.*, emp.member_id FROM es_push_msg epm ");
		sql.append(" LEFT JOIN (SELECT * FROM es_member_push WHERE member_id = ?) emp  ");
		sql.append(" ON epm.push_msg_id = emp.push_msg_id ");
		
		sql.append(" order by ").append(order); 
		Page pushMsgPage = this.daoSupport.queryForPage(sql.toString(), page, pageSize, PushMsg.class, member_id);
		return pushMsgPage;
	}
	
	
	@Override
	public Page search(String title, int pageNo, int pageSize, String order) {
		StringBuffer sql = new StringBuffer( "select *, 0 member_id from es_push_msg where 1 = 1 ");
		
		if(!StringUtil.isEmpty(title)){
			sql.append(" and title like'%"+title+"%'");
		}
		
		order = order == null ? " push_msg_id desc " : order;
		sql.append(" order by " + order );
		
		Page page = this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize);
		return page;
	}

	@Override
	public void insertMemberPush(Integer member_id, String push_msg_id) {

		StringBuffer member_sql = new StringBuffer();
		member_sql.append(" select count(*) from es_member_push where member_id = ? and push_msg_id = ? ");
		int i = this.baseDaoSupport.queryForInt(member_sql.toString(), member_id, push_msg_id);
		if(i <= 0){
			Map<String, Object> fields = new HashMap<String, Object>();
			fields.put("member_id", member_id);
			fields.put("push_msg_id", push_msg_id);
			fields.put("is_see", "1");
			long time = System.currentTimeMillis();
			fields.put("create_time", time/1000);
			this.baseDaoSupport.insert("es_member_push", fields);
		}
	}

	@Override
	public List<Seller> getRecentSellerList(String addr_lat, String addr_lng) {

		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT DISTINCT es.*, eo.order_id, Cal_Distance_Fun(?, ?, es.seller_lat, es.seller_lng) distance ");
		sql.append(" FROM es_order eo LEFT JOIN es_seller es ON es.seller_id = eo.seller_id WHERE ");
		//GROUP BY eo.seller_id
		sql.append(" eo.seller_id IS NOT NULL ORDER BY eo.order_id DESC LIMIT 0,5 ");
			
		List<Seller> sellerList = null;//this.baseDaoSupport.queryForList(sql.toString(), Seller.class, addr_lat, addr_lng);
		
		return sellerList;
	}

	@Override
	public Page getSellerList(String search_code, String cat_id,
			String distance, String sort_type, String page_no, String page_size, String addr_lng, String addr_lat) {
		
		distance = distance == null ? "5" : distance;
		
		List<Object> params = new ArrayList<Object>();
		
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT es.*, Cal_Distance_Fun(").append(addr_lat).append(",").append(addr_lng).append(", es.seller_lat, es.seller_lng) distance ");
		sql.append(" FROM es_seller es WHERE 1 = 1 ");
		if(!StringUtil.isEmpty(search_code)){
			sql.append(" and es.seller_name like concat('%',?,'%') ");
			params.add(search_code);
		}
		if(!StringUtil.isEmpty(cat_id)){
			sql.append(" and es.cat_id = ? ");
			params.add(cat_id);
		}
		
		sql.append(" and Cal_Distance_Fun(").append(addr_lat).append(",").append(addr_lng).append(", es.seller_lat, es.seller_lng) <= ").append(distance);
		
		sql.append(" ORDER BY distance asc ");
			
		Page sellerPage = null;//this.baseDaoSupport.queryForPage2(sql.toString(), Integer.parseInt(page_no), Integer.parseInt(page_size), Seller.class, params.toArray());
		
		return sellerPage;
	}
	
	@Override
	public List getAllSellerList() {
		
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT es.* from es_seller es where 1 = 1 ");
		AdminUser adminUser = this.adminUserManager.getCurrentUser();
		if(adminUser.getFounder() != 1){
			sql.append(" and es.user_id = ").append(adminUser.getUserid());
		}
		sql.append(" ORDER BY es.seller_id desc ");
		List list = this.baseDaoSupport.queryForList(sql.toString());
		
		return list;
	}

	@Override
	public Seller getSellerById(String seller_id) {
		StringBuffer sql = new StringBuffer();
		sql.append(" select es.*, ea.username, egc.name cat_name from es_seller es ");
		sql.append(" left join es_adminuser ea on ea.userid = es.user_id ");
		sql.append(" left join es_goods_cat egc on egc.cat_id = es.cat_id ");
		sql.append(" where es.seller_id = ? ");
		List<Seller> list = null;//this.baseDaoSupport.queryForList(sql.toString(), Seller.class, seller_id);
		Seller seller = null;
		if(list != null && list.size() !=0){
			seller = list.get(0);
		}
		return seller;
	}
	
	@Override
	@Transactional
	public void addPayTrackingAndUpdMember(PayTracking spl) {
		//添加商家付款记录
		this.baseDaoSupport.insert("es_pay_tracking", spl);
		int pay_tracking_id = this.baseDaoSupport.getLastId("es_pay_tracking");
		//扣除会员余额
		this.memberManager.subBalanceNew(spl.getMember_id().toString(), spl.getPrice());
		//增加商家钱包
		this.storeAdminManager.addBalanceByid(spl.getStore_admin_id(), spl.getPrice());
		//增加消费记录
		Consume consume = new Consume();
		long create_time = System.currentTimeMillis();
		consume.setCreate_time(create_time);
		consume.setSign_id(pay_tracking_id);
		consume.setPrice(spl.getPrice());
		consume.setType(2); // 付款商家
		consume.setMember_id(spl.getMember_id());
		this.consumeManager.addConsume(consume);
	}

	@Override
	public void updateSeller(Seller seller) {
		//this.baseDaoSupport.update("es_seller", seller, " seller_id = " + seller.getSeller_id());
	}
	
	@Override
	public Page getStorePayPage(String member_id, String page_no,
			String page_size) {

		StringBuffer sql = new StringBuffer();
		
		sql.append(" select ept.*, esa.store_name from es_pay_tracking ept ");
		sql.append(" left join es_store_admin esa on esa.store_admin_id = ept.store_admin_id ");
		sql.append(" where ept.member_id = ?  ");
		sql.append(" order by ept.pay_tracking_id desc ");

		Page page = this.baseDaoSupport.queryForPage(sql.toString(), 
				Integer.parseInt(page_no), Integer.parseInt(page_size), PayTracking.class, member_id);
		
		return page;
	}

	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}

	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}

	public IMemberManager getMemberManager() {
		return memberManager;
	}

	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}

	public IConsumeManager getConsumeManager() {
		return consumeManager;
	}

	public void setConsumeManager(IConsumeManager consumeManager) {
		this.consumeManager = consumeManager;
	}

	public IStoreAdminManager getStoreAdminManager() {
		return storeAdminManager;
	}

	public void setStoreAdminManager(IStoreAdminManager storeAdminManager) {
		this.storeAdminManager = storeAdminManager;
	}

	
}
