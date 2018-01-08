package com.enation.app.shop.core.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enation.app.shop.core.model.CarMachine;
import com.enation.app.shop.core.model.JoinApply;
import com.enation.app.shop.core.service.IJoinApplyManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

/**
 * 加盟申请管理
 * @author yexf
 * 2017-4-16
 */
public class JoinApplyManager extends BaseSupport<JoinApply> implements IJoinApplyManager {

	@Override
	public void addJoinApply(JoinApply joinApply) {
		this.baseDaoSupport.insert("es_join_apply", joinApply);

	}
	
	@Override
	public void delJoinApplys(Integer[] ids) {
		if (ids == null || ids.equals(""))
			return;
		String id_str = StringUtil.arrayToString(ids, ",");
		String sql = "delete from es_join_apply where join_apply_id in (" + id_str
				+ ")";
		this.baseDaoSupport.execute(sql);
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
	public List<CarMachine> getMachineList(String addr_lng, String addr_lat,
			Double distance, String search_address) {

		StringBuffer sql = new StringBuffer();
		List<CarMachine> list = new ArrayList<CarMachine>();
		
		sql.append(" SELECT t.* FROM (SELECT *, Cal_Distance_Fun(?, ?, machine_lat, machine_lng) ");
		sql.append(" AS car_distance FROM es_car_machine) t WHERE 1 = 1 ");
		
		if(!StringUtil.isEmpty(search_address)){
			sql.append(" and t.address like ").append("'%").append(search_address).append("%'");
		}

		/*if(distance == null){
			list = this.baseDaoSupport.queryForList(sql.toString(), CarMachine.class, addr_lat, addr_lng);
		}else{
			sql.append(" and t.car_distance <= ? ");
			list = this.baseDaoSupport.queryForList(sql.toString(), CarMachine.class, addr_lat, addr_lng, distance);
		}*/
		
		return list;
	}

	@Override
	public CarMachine getMachineByNumber(String machine_number) {

		StringBuffer sql = new StringBuffer();
		
		sql.append(" select * from es_car_machine where machine_number = ? ");
		
		List<CarMachine> list = null;//this.baseDaoSupport.queryForList(sql.toString(), CarMachine.class, machine_number);
		
		if(list.size() != 0){
			return list.get(0);
		}else{
			return null;
		}
		
	}

	@Override
	public Page pageJoinApply(String sort, String order, int page,
			int pageSize, String name, String phone, String uname) {
		order = order == null ? " eja.join_apply_id " : order;
		sort = sort == null ? " desc " : sort;
		StringBuffer sql = new StringBuffer(" select eja.*, em.uname from es_join_apply eja ");
		sql.append(" left join es_member em on em.member_id = eja.member_id where 1 = 1 ");
		if(!StringUtil.isEmpty(name)){
			sql.append(" and eja.name like '%").append(name).append("%'");
		}
		if(!StringUtil.isEmpty(phone)){
			sql.append(" and eja.phone like '%").append(phone).append("%'");	
		}
		if(!StringUtil.isEmpty(uname)){
			sql.append(" and em.uname like '%").append(uname).append("%'");	
		}
		
		sql.append(" order by ").append(order).append(" ").append(sort);
		Page rpage = this.daoSupport.queryForPage(sql.toString(), page, pageSize, JoinApply.class);
		return rpage;
	}



}
