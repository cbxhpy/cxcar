package com.enation.app.shop.core.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.shop.core.model.CarMachine;
import com.enation.app.shop.core.model.PushMsg;
import com.enation.app.shop.core.service.IMachineManager;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

/**
 * 洗车机管理
 * @author yexf
 * 2017-4-12
 */
public class MachineManager extends BaseSupport<CarMachine> implements IMachineManager {

	private IAdminUserManager adminUserManager;
	
	@Override
	public void addCarMachine(CarMachine carMachine) {
		this.baseDaoSupport.insert("es_car_machine", carMachine);

	}
	
	@Override
	public void delCarMachines(Integer[] ids) {
		if (ids == null || ids.equals(""))
			return;
		String id_str = StringUtil.arrayToString(ids, ",");
		String sql = "delete from es_car_machine where car_machine_id in (" + id_str
				+ ")";
		this.baseDaoSupport.execute(sql);
	}

	@Override
	public Page pageMachine(String order, String sort, int page, int pageSize, String uname, String machine_number,
			Integer province_id, Integer city_id, Integer region_id, String machine_name) {
		order = StringUtil.isEmpty(order) ? " ecm.car_machine_id " : order;
		sort = StringUtil.isEmpty(sort) ? " desc " : sort;
		StringBuffer sql = new StringBuffer(" select ecm.*, em.uname from es_car_machine ecm ");
		sql.append(" left join es_member em on em.member_id = ecm.member_id where 1 = 1 ");
		if(!StringUtil.isEmpty(uname)){
			sql.append(" and em.uname like '%").append(uname).append("%'");
		}
		if(!StringUtil.isEmpty(machine_number)){
			sql.append(" and ecm.machine_number like '%").append(machine_number).append("%'");
		}
		if(!StringUtil.isEmpty(machine_name)){
			sql.append(" and ecm.machine_name like '%").append(machine_name).append("%'");
		}
		if(province_id != null && province_id != 0){
			sql.append(" and ecm.province_id = ").append(province_id);
		}
		if(city_id != null && city_id != 0){
			sql.append(" and ecm.city_id = ").append(city_id);
		}
		if(region_id != null && region_id != 0){
			sql.append(" and ecm.region_id = ").append(region_id);
		}
		AdminUser adminUser = this.adminUserManager.getCurrentUser();
		if(adminUser.getFounder() != 1){
			if(adminUser.getRegion_id() != null && adminUser.getRegion_id() != 0){
				sql.append(" and ecm.region_id = ").append(adminUser.getRegion_id());
			}else if(adminUser.getCity_id() != null && adminUser.getCity_id() != 0){
				sql.append(" and ecm.city_id = ").append(adminUser.getCity_id());
			}else if(adminUser.getProvince_id() != null && adminUser.getProvince_id() != 0){
				sql.append(" and ecm.province_id = ").append(adminUser.getProvince_id());
			}
		}
		sql.append(" order by ").append(order).append(" ").append(sort); 
		Page rpage = this.daoSupport.queryForPage(sql.toString(), page, pageSize, CarMachine.class);
		return rpage;
	}
	
	@Override
	public List<CarMachine> getMachineList(String addr_lng, String addr_lat,
			Double distance, String search_address) {
		search_address = StringUtil.isNull(search_address);
		StringBuffer sql = new StringBuffer();
		List<CarMachine> list = new ArrayList<CarMachine>();
		List<Object> params = new ArrayList<Object>();
		params.add(addr_lat);
		params.add(addr_lng);
		params.add(search_address);
		sql.append(" SELECT t.* FROM (SELECT *, Cal_Distance_Fun(?, ?, machine_lat, machine_lng) ");
		sql.append(" AS car_distance FROM es_car_machine) t WHERE 1 = 1 ");
		sql.append(" and t.address like concat('%',?,'%') ");

		if(distance == null){
			sql.append(" order by t.car_distance asc ");
		}else{
			sql.append(" and t.car_distance <= ? ");
			sql.append(" order by t.car_distance asc ");
			params.add(distance);
		}
		list = this.baseDaoSupport.queryForList(sql.toString(), CarMachine.class, params.toArray());
		
		
		return list;
	}

	@Override
	public List<CarMachine> getMachineByUse(String is_use) {
		StringBuffer sql = new StringBuffer();
		List<CarMachine> list = new ArrayList<CarMachine>();
		sql.append(" SELECT t.*  FROM es_car_machine t WHERE 1 = 1 ");
		sql.append(" and t.is_use = ? ");

		list = this.baseDaoSupport.queryForList(sql.toString(), CarMachine.class, is_use);
		
		return list;
	}
	
	@Override
	public CarMachine getMachineByNumber(String machine_number) {

		StringBuffer sql = new StringBuffer();
		
		sql.append(" select * from es_car_machine where machine_number = ? ");
		
		List<CarMachine> list = this.baseDaoSupport.queryForList(sql.toString(), CarMachine.class, machine_number);
		
		if(list.size() != 0){
			return list.get(0);
		}else{
			return null;
		}
		
	}
	
	@Override
	public void updateHardwareStatus(String id, String status) {
		StringBuffer sql = new StringBuffer();
		
		sql.append(" update es_machine set status = ? where id = ? ");
		
		this.baseDaoSupport.execute(sql.toString(), status, id);
	}
	
	@Override
	public Map<String, Object> getYJMachineByNumber(String machine_number) {
		StringBuffer sql = new StringBuffer();
		
		sql.append(" select * from es_machine where sid = ? order by id limit 0,1 ");
		
		List<Map> list = this.baseDaoSupport.queryForList(sql.toString(), machine_number);
		
		if(list.size() != 0){
			return list.get(0);
		}else{
			return null;
		}
	}
	
	@Override
	public CarMachine getMachineById(String car_machine_id) {

		StringBuffer sql = new StringBuffer();
		
		sql.append(" select ecm.*, em.uname from es_car_machine ecm ");
		sql.append(" left join es_member em on em.member_id = ecm.member_id ");
		sql.append(" where ecm.car_machine_id = ? ");
		
		List<CarMachine> list = this.baseDaoSupport.queryForList(sql.toString(), CarMachine.class, car_machine_id);
		
		if(list.size() != 0){
			return list.get(0);
		}else{
			return null;
		}
		
	}

	@Override
	public void updateMachineIsUse(String is_use, String car_machine_id) {
		Map fields = new HashMap();
		fields.put("is_use", is_use);
		this.baseDaoSupport.update("es_car_machine", fields, " car_machine_id = " + car_machine_id);
		//this.baseDaoSupport.update("es_car_machine", carMachine, " car_machine_id = " + carMachine.getCar_machine_id());
	}

	@Override
	public void updateMachine(CarMachine carMachine) {
		this.baseDaoSupport.update("es_car_machine", carMachine, " car_machine_id = " + carMachine.getCar_machine_id());
	}

	@Override
	public int startMachine(String machine_number, String member_id) {
		StringBuffer sql = new StringBuffer();
		sql.append(" select fn_start_machine(?, ?) ");
		int i = this.baseDaoSupport.queryForInt(sql.toString(), machine_number, member_id);
		System.out.println("return code:"+i);
		return i;
	}

	@Override
	public Map<String, Object> getMachineStatus(String new_number) {
		
		StringBuffer sql = new StringBuffer();
		
		/*sql.append(" CALL pr_get_jiesuan_status(?, ?, ?) ");
		Map<String, Object> map = this.baseDaoSupport.executeCall(sql.toString(), new_number);*/
		

		sql.append(" SELECT fn_get_jiesuan_status(?) AS status_price ");
		
		Map<String, Object> map = this.baseDaoSupport.queryForMap(sql.toString(), new_number);
		
		return map;
	}

	@Override
	public Integer checkMachineIsUse(String machine_number) {

		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT fn_get_machine_isuse(?) AS isuer ");
		
		Integer isuse = this.baseDaoSupport.queryForInt(sql.toString(), machine_number);
		
		return isuse;
	}
	
	@Override
	public Boolean checkMemberIsOpenMachine(String member_id) {

		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT count(*) from es_machine where uid = ? and status = 0 ");
		
		Integer num = this.baseDaoSupport.queryForInt(sql.toString(), member_id);
		
		if(num > 0){
			return true;
		}else{
			return false;
		}

	}
	
	@Override
	public void updateUseMachineToOver() {
		StringBuffer sql = new StringBuffer();
		sql.append(" UPDATE es_machine SET STATUS = 1 WHERE last_time < DATE_SUB(NOW(), INTERVAL 15 MINUTE) AND STATUS = 0 ");
		this.baseDaoSupport.execute(sql.toString());
	}

	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}

	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}


}
