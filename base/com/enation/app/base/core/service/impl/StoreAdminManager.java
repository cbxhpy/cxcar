package com.enation.app.base.core.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enation.app.base.core.model.AdvMapper;
import com.enation.app.base.core.service.IStoreAdminManager;
import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.shop.core.model.PushMsg;
import com.enation.app.shop.core.model.Seller;
import com.enation.app.shop.core.model.StoreAdmin;
import com.enation.app.shop.core.service.ISellerManager;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

/**
 * 商家管理员管理
 * @author yexf
 * 2017-4-12
 */
public class StoreAdminManager extends BaseSupport<StoreAdmin> implements IStoreAdminManager {

	private IAdminUserManager adminUserManager;
	
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
	public Page pageSeller(String sort, String order, int pageNo, int pageSize) {
		order = order == null ? " es.seller_id " : order;
		sort = sort == null ? " desc " : sort;
		StringBuffer sql = new StringBuffer(" select es.*, ea.username, egc.name cat_name from es_seller es ");
		//sql.append(" left join es_adminuser ea on ea.userid = es.user_id ");
		//sql.append(" left join es_goods_cat egc on egc.cat_id = es.cat_id where 1 = 1 ");
		AdminUser adminUser = this.adminUserManager.getCurrentUser();
		if(adminUser.getFounder() != 1){
			sql.append(" and ea.userid = ").append(adminUser.getUserid());
		}
		sql.append(" order by ").append(order).append(" ").append(sort); 
		Page rpage = this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize, Seller.class);
		return rpage;
	}

	@Override
	public List<Seller> getRecentSellerList(String addr_lat, String addr_lng) {

		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT DISTINCT es.*, eo.order_id, Cal_Distance_Fun(?, ?, es.seller_lat, es.seller_lng) distance ");
		sql.append(" FROM es_order eo LEFT JOIN es_seller es ON es.seller_id = eo.seller_id WHERE ");
		//GROUP BY eo.seller_id
		sql.append(" eo.seller_id IS NOT NULL ORDER BY eo.order_id DESC LIMIT 0,5 ");
			
		List<Seller> sellerList = null; //this.baseDaoSupport.queryForList(sql.toString(), Seller.class, addr_lat, addr_lng);
		
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
			
		Page sellerPage = null; //this.baseDaoSupport.queryForPage2(sql.toString(), Integer.parseInt(page_no), Integer.parseInt(page_size), Seller.class, params.toArray());
		
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
	public StoreAdmin getStoreAdminById(String store_admin_id) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT esa.* FROM es_store_admin esa WHERE esa.store_admin_id = ? ");
		List<StoreAdmin> list = this.baseDaoSupport.queryForList(sql.toString(), StoreAdmin.class, store_admin_id);
		StoreAdmin storeAdmin = null;
		if(list != null && list.size() !=0){
			storeAdmin = list.get(0);
		}
		return storeAdmin;
	}

	@Override
	public void addBalanceByid(Integer store_admin_id, Double price) {
		StringBuffer sql = new StringBuffer();
		
		sql.append(" update es_store_admin set balance = balance + ? where store_admin_id = ? ");
		this.baseDaoSupport.execute(sql.toString(), price, store_admin_id);
	}
	
	@Override
	public void updateSeller(Seller seller) {
		//this.baseDaoSupport.update("es_seller", seller, " seller_id = " + seller.getSeller_id());
	}

	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}

	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}


	
}
