package com.enation.app.shop.core.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.model.CarMachine;
import com.enation.app.shop.core.model.MemberProfit;
import com.enation.app.shop.core.model.WashRecord;
import com.enation.app.shop.core.service.IMemberProfitManager;
import com.enation.app.shop.core.utils.DateUtil;
import com.enation.app.shop.core.utils.DetailUtil;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.ExcelUtil;
import com.enation.framework.util.StringUtil;

/**
 * 收益管理
 *
 */
public class MemberProfitManager extends BaseSupport<MemberProfit> implements IMemberProfitManager {

	@Override
	public void addMemberProfit(MemberProfit memberProfit) {
		this.baseDaoSupport.insert("es_member_profit", memberProfit);
	}

	@Override
	public void calculateProfit(WashRecord washRecord) {
		//平台
		this.saveProfit(1, null, null, null, washRecord);
		//运营商
		this.saveProfit(2, null, null, null, washRecord);
		//市代
		this.saveProfit(3, washRecord.getProvince_id(), washRecord.getCity_id(), null, washRecord);
		//县代
		this.saveProfit(4, washRecord.getProvince_id(), washRecord.getCity_id(), washRecord.getRegion_id(), washRecord);
		//投资人
		this.saveProfit(5, null, null, null, washRecord);
		//招商
		this.saveProfit(6, null, null, null, washRecord);
	}
	
	@Transactional
	public void saveProfit(Integer roleType, Integer provinceId, Integer cityId, Integer regionId, WashRecord washRecord){
		if(roleType==null){
			return;
		}
		if(roleType.intValue()==3&&(provinceId==null||cityId==null)){
			return;
		}
		if(roleType.intValue()==4&&(provinceId==null||cityId==null||regionId==null)){
			return;
		}
		Map<String, Object> memberRole=this.getMemberRole(roleType, provinceId, cityId, regionId);
		if(memberRole==null){
			return;
		}
		if(memberRole.get("roleId")==null){
			return;
		}
		if(memberRole.get("profitRatio")==null){
			return;
		}
		Integer roleId = Integer.valueOf(memberRole.get("roleId").toString());
		Double profitRatio = Double.valueOf(memberRole.get("profitRatio").toString());
		List<Map> list = this.getMemberId(roleId);
		if(list == null){
			return;
		}
		for (int i = 0; i < list.size(); i++) {
			Map map = list.get(i);
			Double incomePrice = profitRatio*washRecord.getTotal_price()/list.size();
			Integer memberId = Integer.valueOf(map.get("memberId").toString());
			if(roleType.intValue() == 1){//平台
				/*if(this.countMemberProfit(0, washRecord.getWash_record_id())>0){
					return;
				}*/
				MemberProfit memberProfit = new MemberProfit();
				memberProfit.setWash_record_id(washRecord.getWash_record_id());
				memberProfit.setMember_id(0);
				memberProfit.setMachine_id(washRecord.getCar_machine_id());
				memberProfit.setMachine_name(washRecord.getMachine_name());
				memberProfit.setProfit_type(2);
				memberProfit.setTotal_price(washRecord.getTotal_price());
				memberProfit.setProfit_ratio(profitRatio);
				memberProfit.setIncome_price(incomePrice);
				memberProfit.setCreate_time(System.currentTimeMillis());
				this.addMemberProfit(memberProfit);
			}else if(roleType.intValue()==2||roleType.intValue()==3||roleType.intValue()==4||roleType.intValue()==5||roleType.intValue()==6){//其他用户
				if(memberId==null){
					return;
				}
				/*if(this.countMemberProfit(memberId, washRecord.getWash_record_id())>0){
					return;
				}*/
				MemberProfit memberProfit=new MemberProfit();
				memberProfit.setWash_record_id(washRecord.getWash_record_id());
				memberProfit.setMember_id(memberId);
				memberProfit.setMachine_id(washRecord.getCar_machine_id());
				memberProfit.setMachine_name(washRecord.getMachine_name());
				memberProfit.setProfit_type(2);
				memberProfit.setTotal_price(washRecord.getTotal_price());
				memberProfit.setProfit_ratio(profitRatio);
				memberProfit.setIncome_price(incomePrice);
				memberProfit.setCreate_time(System.currentTimeMillis());
				this.addMemberProfit(memberProfit);
				this.addProfit(memberId, incomePrice);
			}
		}
	}
	
	/**
	 * 获取分润比例
	 * @param roleType
	 * @param provinceId
	 * @param cityId
	 * @param regionId
	 * @return
	 */
	public Map<String, Object> getMemberRole(Integer roleType, Integer provinceId, Integer cityId, Integer regionId){
		try {
			if(roleType==null){
				return null;
			}
			Map<String, Object> memberRole = null;
			StringBuffer sql=new StringBuffer();
			sql.append("SELECT role_id AS roleId, IFNULL(profit_ratio/100, 0) AS profitRatio FROM es_member_role WHERE 1=1 AND role_type= ").append(roleType);
			if(provinceId!=null){
				sql.append(" AND province_id= ").append(provinceId);
			}
			if(cityId!=null){
				sql.append(" AND city_id= ").append(cityId);
			}
			if(regionId!=null){
				sql.append(" AND region_id= ").append(regionId);
			}
			sql.append(" ORDER BY role_id DESC LIMIT 1");
			List<Map> list = this.baseDaoSupport.queryForList(sql.toString());
			if(list != null && list.size() != 0){
				memberRole = list.get(0);
			}
			return memberRole;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 获取分润角色id
	 * @param roleId
	 * @return
	 */
	public List<Map> getMemberId(Integer roleId){
		try {
			if(roleId==null){
				return null;
			}
			String sql="SELECT member_id as memberId FROM es_member WHERE role_id LIKE '%,"+roleId+",%' ORDER BY member_id DESC";
			return this.baseDaoSupport.queryForList(sql);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 增加分润收入
	 * 2017-12-7
	 * @param  
	 * @param member_id
	 * @param join_income 
	 */
	public void addProfit(Integer member_id, Double join_income) {
		StringBuffer sql = new StringBuffer();
		sql.append(" update es_member set join_income = join_income + ? where member_id = ?  ");
		this.baseDaoSupport.execute(sql.toString(), join_income, member_id);
	}
	
	/**
	 * 修改用户余额
	 * @param memberId
	 */
	public void updateBalance(Integer memberId, Double incomePrice){
		String sql="UPDATE es_member SET balance=balance + ?, join_income=join_income + ? WHERE member_id = ?";
		this.baseDaoSupport.execute(sql, incomePrice, incomePrice, memberId);
	}
	
	/**
	 * 查询是否已获得分润
	 * @param memberId
	 * @param washRecordId
	 * @return
	 */
	public int countMemberProfit(Integer memberId, Integer washRecordId){
		String sql="SELECT count(1) FROM es_member_profit WHERE member_id=? AND wash_record_id=?";
		return this.baseDaoSupport.queryForInt(sql, memberId, washRecordId);
	}
	
	@Override
	public Page pageMemberProfit(String sort, String order, int page, int pageSize, 
			String uname, String profit_type, String machine_number, String start_time, String end_time) {
		order = order == null ? " a.profit_id desc " : order;
		sort = sort == null ? " desc " : sort;
		StringBuffer sql = new StringBuffer(" select a.*, em.uname, em.role_name, ecm.machine_number from es_member_profit a ");
		sql.append(" left join es_member em on em.member_id = a.member_id ");
		sql.append(" left join es_car_machine ecm on ecm.car_machine_id = a.machine_id where 1 = 1 ");
		if(!StringUtil.isEmpty(uname)){
			sql.append(" and em.uname like '%").append(uname).append("%'");
		}
		if(!StringUtil.isEmpty(profit_type)){
			sql.append(" and a.profit_type = ").append(profit_type);
		}
		if(!StringUtil.isEmpty(machine_number)){
			sql.append(" and ecm.machine_number = ").append(machine_number);
		}
		if(!StringUtil.isEmpty(start_time)){			
			long stime = DateUtil.StringToLong(start_time+" 00:00:00","yyyy-MM-dd HH:mm:ss");
			sql.append(" and a.create_time > "+stime);
		}
		if(!StringUtil.isEmpty(end_time)){			
			long etime = DateUtil.StringToLong(end_time +" 23:59:59", "yyyy-MM-dd HH:mm:ss");
			sql.append(" and a.create_time < "+(etime));
		}
		sql.append(" order by ").append(order); 
		Page rpage = this.daoSupport.queryForPage(sql.toString(), page, pageSize, MemberProfit.class);
		return rpage;
	}
	
	@Override
	public String recordExportToExcel(String start_time, String end_time) {
		try {
			
			StringBuffer sql = new StringBuffer(" select a.*, em.uname, em.role_name, ecm.machine_number from es_member_profit a ");
			sql.append(" left join es_member em on em.member_id = a.member_id ");
			sql.append(" left join es_car_machine ecm on ecm.car_machine_id = a.machine_id where 1 = 1 ");
			
			if(!StringUtil.isEmpty(start_time)){			
				long stime = DateUtil.StringToLong(start_time+" 00:00:00","yyyy-MM-dd HH:mm:ss");
				sql.append(" and a.create_time > "+stime);
			}
			if(!StringUtil.isEmpty(end_time)){			
				long etime = DateUtil.StringToLong(end_time +" 23:59:59", "yyyy-MM-dd HH:mm:ss");
				sql.append(" and a.create_time < "+(etime));
			}
			sql.append(" order by a.profit_id desc ");
			
			List<Map> list = this.baseDaoSupport.queryForList(sql.toString());
			
			ExcelUtil excelUtil = new ExcelUtil(); 
			 
			InputStream in = new FileInputStream(new File(EopSetting.EOP_PATH+"/excel/profit.xls")) ;// FileUtil.getResourceAsStream("com/enation/app/shop/component/bonus/service/impl/bonus_list.xls");
			excelUtil.openModal( in );
			
			int i=1;
			for (Map record : list) {
				excelUtil.writeStringToCell(i, 0, StringUtil.isNull(record.get("uname"))); // 手机号
				excelUtil.writeStringToCell(i, 1, StringUtil.isNull(record.get("role_name"))); // 会员角色
				excelUtil.writeStringToCell(i, 2, StringUtil.isNull(record.get("machine_number"))); // 编号
				excelUtil.writeStringToCell(i, 3, StringUtil.isNull(record.get("machine_name"))); // 洗车机名称
				excelUtil.writeStringToCell(i, 4, DetailUtil.getProfitType(StringUtil.isNullRt0(record.get("profit_type")))); // 收益类型（1：合伙人收益；2：分润）
				excelUtil.writeStringToCell(i, 5, StringUtil.isNull(record.get("total_price"))); // 订单总额
				excelUtil.writeStringToCell(i, 6, StringUtil.isNull(record.get("income_price"))); // 收益金额
				excelUtil.writeStringToCell(i, 7, DateUtil.toString(new Date((Long)record.get("create_time")), "yyyy/MM/dd HH:mm:ss")); // 创建时间
				i++;
			}

			String fileName = DateUtil.toString( new Date(),"yyyyMMddHHmmss");
			File file = new File(EopSetting.IMG_SERVER_PATH + "/profit_excel");
			if (!file.exists()){
				file.mkdirs();
			}
			
			String filePath = EopSetting.IMG_SERVER_PATH+"/profit_excel/"+fileName+".xls";
			excelUtil.writeToFile(filePath);
			return EopSetting.IMG_SERVER_DOMAIN+"/profit_excel/"+fileName+".xls";
		} catch (Exception e) {
			e.printStackTrace();
		  return null;
		}
	}
	
}
