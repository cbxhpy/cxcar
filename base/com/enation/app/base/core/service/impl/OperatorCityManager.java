package com.enation.app.base.core.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enation.app.base.core.model.AdvMapper;
import com.enation.app.base.core.service.IOperatorCityManager;
import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.shop.core.model.PushMsg;
import com.enation.app.shop.core.model.Seller;
import com.enation.app.shop.core.service.ISellerManager;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.resource.model.OperatorCity;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

/**
 * 运营商城市
 * @author yexf
 * @date 2017-11-26 上午9:22:37  
 */
public class OperatorCityManager extends BaseSupport<OperatorCity> implements IOperatorCityManager {

	private IAdminUserManager adminUserManager;
	
	@Override
	public void addOperatorCity(OperatorCity operatorCity) {
		this.baseDaoSupport.insert("es_operator_city", operatorCity);
	}
	
	@Override
	public void delOperatorCity(Integer[] ids) {
		if (ids == null || ids.equals(""))
			return;
		String id_str = StringUtil.arrayToString(ids, ",");
		String sql = "delete from es_operator_city where operator_city_id in (" + id_str
				+ ")";
		this.baseDaoSupport.execute(sql);
	}

	
	@Override
	public Page pageOperatorCity(String order, String sort, int pageNo, int pageSize) {
		order = order == null ? " eoc.operator_city_id " : order;
		sort = sort == null ? " desc " : sort;
		StringBuffer sql = new StringBuffer(" select eoc.* from es_operator_city eoc ");
		sql.append(" order by ").append(order).append(" ").append(sort); 
		Page rpage = this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize, OperatorCity.class);
		return rpage;
	}

	@Override
	public List<OperatorCity> getRecentSellerList(String addr_lat, String addr_lng) {

		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT DISTINCT es.*, eo.order_id, Cal_Distance_Fun(?, ?, es.seller_lat, es.seller_lng) distance ");
		sql.append(" FROM es_order eo LEFT JOIN es_seller es ON es.seller_id = eo.seller_id WHERE ");
		//GROUP BY eo.seller_id
		sql.append(" eo.seller_id IS NOT NULL ORDER BY eo.order_id DESC LIMIT 0,5 ");
			
		List<OperatorCity> sellerList = this.baseDaoSupport.queryForList(sql.toString(), OperatorCity.class, addr_lat, addr_lng);
		
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
			
		Page sellerPage = this.baseDaoSupport.queryForPage2(sql.toString(), Integer.parseInt(page_no), Integer.parseInt(page_size), OperatorCity.class, params.toArray());
		
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
	public OperatorCity getOperatorCityById(String operator_city_id) {
		StringBuffer sql = new StringBuffer();
		sql.append(" select eoc.* from es_operator_city eoc ");
		sql.append(" where eoc.operator_city_id = ? ");
		List<OperatorCity> list = this.baseDaoSupport.queryForList(sql.toString(), OperatorCity.class, operator_city_id);
		OperatorCity operatorCity = null;
		if(list != null && list.size() !=0){
			operatorCity = list.get(0);
		}
		return operatorCity;
	}

	@Override
	public void updateOperatorCity(OperatorCity operatorCity) {
		this.baseDaoSupport.update("es_operator_city", operatorCity, " operator_city_id = " + operatorCity.getOperator_city_id());
	}

	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}

	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}

	@Override
	public void delOperatorCity(String tables, String coms, String values) {
		StringBuffer sql = new StringBuffer();
		sql.append(" update ").append(tables).append(" set ").append(coms).append(" = ").append(values);
		this.baseDaoSupport.execute(sql.toString());
	}

	
}
