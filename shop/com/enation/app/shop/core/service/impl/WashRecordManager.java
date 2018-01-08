package com.enation.app.shop.core.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.base.core.service.auth.IPermissionManager;
import com.enation.app.base.core.service.auth.impl.PermissionConfig;
import com.enation.app.shop.core.model.CarMachine;
import com.enation.app.shop.core.model.MemberProfit;
import com.enation.app.shop.core.model.PushMsg;
import com.enation.app.shop.core.model.WashMemberCoupons;
import com.enation.app.shop.core.model.WashRecord;
import com.enation.app.shop.core.service.IMachineManager;
import com.enation.app.shop.core.service.IMemberProfitManager;
import com.enation.app.shop.core.service.IWashRecordManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.app.shop.core.utils.DateUtil;
import com.enation.app.shop.core.utils.DetailUtil;
import com.enation.app.shop.core.utils.StringUtil;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.ExcelUtil;

/**
 * 洗车记录管理
 * @author yexf
 * 2017-4-12
 */
public class WashRecordManager extends BaseSupport<WashRecord> implements IWashRecordManager {

	private WashMemberCouponsManager washMemberCouponsManager;
	private MemberManager memberManager;
	private IMemberProfitManager memberProfitManager;
	private IMachineManager machineManager;
	private IAdminUserManager adminUserManager;
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int addWashRecord(WashRecord washRecord) {
		this.baseDaoSupport.insert("es_wash_record", washRecord);
		int i = this.baseDaoSupport.getLastId("es_wash_record");
		return i;
	}
	
	@Override
	public void delWashRecord(Integer[] ids) {
		if (ids == null || ids.equals(""))
			return;
		String id_str = StringUtil.arrayToString(ids, ",");
		String sql = "delete from es_wash_record where wash_record_id in (" + id_str
				+ ")";
		this.baseDaoSupport.execute(sql);
	}

	@Override
	public Page pageWashRecord(String order, String sort, int page, 
			int pageSize, String uname, String machine_number,
				String start_time, String end_time, String car_machine_id) {
		order = order == null ? " ewr.wash_record_id " : order;
		sort = sort == null ? " desc " : sort;
		StringBuffer sql = new StringBuffer(" select ewr.*, em.uname, ecm.machine_number, ecm.machine_name, ecm.address from es_wash_record ewr ");
		sql.append(" left join es_car_machine ecm on ecm.car_machine_id = ewr.car_machine_id ");
		sql.append(" left join es_member em on em.member_id = ewr.member_id where ewr.pay_status = 1 and ewr.is_hide = 0 ");
		if(!StringUtil.isEmpty(uname)){
			sql.append(" and em.uname like '%").append(uname).append("%'");
		}
		if(!StringUtil.isEmpty(car_machine_id)){
			sql.append(" and ecm.car_machine_id = ").append(car_machine_id);
		}
		if(!StringUtil.isEmpty(machine_number)){
			sql.append(" and ecm.machine_number like '%").append(machine_number).append("%'");
		}
		if(start_time!=null&&!StringUtil.isEmpty(start_time)){			
			long stime = DateUtil.StringToLong(start_time+" 00:00:00","yyyy-MM-dd HH:mm:ss");
			sql.append(" and ewr.create_time > "+stime);
		}
		if(end_time!=null&&!StringUtil.isEmpty(end_time)){			
			long etime = DateUtil.StringToLong(end_time +" 23:59:59", "yyyy-MM-dd HH:mm:ss");
			sql.append(" and ewr.create_time < "+(etime));
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
		Page rpage = this.daoSupport.queryForPage(sql.toString(), page, pageSize, WashRecord.class);
		return rpage;
	}
	
	/*@Override
	public Page getIncomePage(Member member, int pageNo, int pageSize) {
		
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT ewr.total_price/ecm.partner_num total_price, ewr.create_time, ecm.machine_name, ecm.address ");
		sql.append(" FROM es_wash_record ewr LEFT JOIN es_car_machine ecm ON ecm.car_machine_id = ewr.car_machine_id WHERE ewr.car_machine_id IN ");
		sql.append(" ( SELECT car_machine_id FROM es_car_machine WHERE partner_phone LIKE '%").append(member.getUname()).append("%' ) ");
		sql.append(" and ewr.pay_status = 1 order by wash_record_id desc ");
		
		Page page = this.baseDaoSupport.queryForPage(sql.toString(), pageNo, pageSize, WashRecord.class);
		return page;
	}*/
	@Override
	public Page getIncomePage(Member member, int pageNo, int pageSize) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT income_price, create_time, machine_name ");
		sql.append(" FROM es_member_profit WHERE member_id =  ").append(member.getMember_id());
		sql.append(" order by profit_id desc ");
		Page page = this.baseDaoSupport.queryForPage(sql.toString(), pageNo, pageSize);
		return page;
	}

	@Override
	public Page getWashRecordPage(String member_id, int pageNo, int pageSize){
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT ewr.*, ecm.machine_name, ecm.address FROM es_wash_record ewr ");
		sql.append(" LEFT JOIN es_car_machine ecm ON ecm.car_machine_id = ewr.car_machine_id ");
		sql.append(" WHERE ewr.is_hide = 0 and ewr.member_id = ? ");
		sql.append(" and ewr.pay_status = 1 order by wash_record_id desc ");
		
		Page page = this.baseDaoSupport.queryForPage(sql.toString(), pageNo, pageSize, WashRecord.class, member_id);
		return page;
	}
	
	/*@Override
	public Double getYesterDayIncome(Member member) {

		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT IFNULL(SUM(ewr.total_price/ecm.partner_num), 0) yes_income FROM es_wash_record ewr ");
		sql.append(" LEFT JOIN es_car_machine ecm ON ecm.car_machine_id = ewr.car_machine_id ");
		sql.append(" WHERE ewr.pay_status = 1 ");
		sql.append(" AND TO_DAYS( NOW( ) ) - TO_DAYS( FROM_UNIXTIME(ewr.create_time/1000,'%Y-%m-%d %h:%i:%s')) <= 1 ");
		sql.append(" and ewr.car_machine_id in ( SELECT car_machine_id FROM es_car_machine WHERE partner_phone LIKE '%").append(member.getUname()).append("%' ) ");
		Double yes_income = 0.00;
		List<Map> list = this.baseDaoSupport.queryForList(sql.toString());
		if(list.size() != 0){
			Map map = list.get(0);
			yes_income = Double.parseDouble(map.get("yes_income").toString());
		}
		
		return yes_income;
	}*/
	@Override
	public Double getYesterDayIncome(Member member) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT IFNULL(SUM(income_price), 0) as income_price FROM es_member_profit ");
		sql.append(" WHERE member_id = ").append(member.getMember_id());
		sql.append(" AND TO_DAYS( NOW( ) ) - TO_DAYS( FROM_UNIXTIME(create_time/1000,'%Y-%m-%d %h:%i:%s')) = 1 ");
		Double yes_income = 0.00;
		List<Map> list = this.baseDaoSupport.queryForList(sql.toString());
		if(list.size() != 0){
			Map map = list.get(0);
			yes_income = Double.parseDouble(map.get("income_price").toString());
		}
		return yes_income;
	}

	@Override
	public WashRecord getWashRecordById(String wash_record_id) {
		StringBuffer sql = new StringBuffer();
		
		sql.append(" select ewr.*, ecm.machine_number, ecm.machine_name, ecm.province_id, ecm.city_id, ecm.region_id from es_wash_record ewr ");
		sql.append(" LEFT JOIN es_car_machine ecm ON ecm.car_machine_id = ewr.car_machine_id ");
		sql.append(" where ewr.wash_record_id = ? ");
		
		List<WashRecord> list = this.baseDaoSupport.queryForList(sql.toString(), WashRecord.class, wash_record_id);

		if(list.size() != 0){
			return list.get(0);
		}else{
			return null;
		}
	}

	@Override
	public void updatePayStatus(WashRecord washRecord) {
		this.baseDaoSupport.update("es_wash_record", washRecord, "wash_record_id = "+washRecord.getWash_record_id());
	}

	@Override
	public List<WashRecord> getWashRecordListByNoPayOrNoCheckOut(
			Integer member_id) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT ewr.*, ecm.machine_name, ecm.address FROM es_wash_record ewr ");
		sql.append(" LEFT JOIN es_car_machine ecm ON ecm.car_machine_id = ewr.car_machine_id ");
		sql.append(" WHERE ewr.is_hide = 0 and ewr.member_id = ? ");
		sql.append(" and (ewr.pay_status = 0 or ewr.use_status = 0) order by wash_record_id desc ");
		
		List<WashRecord> list = this.baseDaoSupport.queryForList(sql.toString(), WashRecord.class, member_id);
		return list;
	}
	
	@Override
	public String recordExportToExcel(String start_time, String end_time) {
		try {
			
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT ewr.*, em.uname, ecm.machine_name, ecm.machine_number, ecm.address FROM es_wash_record ewr ");
			sql.append(" LEFT JOIN es_car_machine ecm ON ecm.car_machine_id = ewr.car_machine_id ");
			sql.append(" left join es_member em on em.member_id = ewr.member_id ");
			sql.append(" WHERE ewr.is_hide = 0 and 1 = 1 ");
			if(start_time!=null&&!StringUtil.isEmpty(start_time)){			
				long stime = DateUtil.StringToLong(start_time+" 00:00:00","yyyy-MM-dd HH:mm:ss");
				sql.append(" and ewr.create_time > "+stime);
			}
			if(end_time!=null&&!StringUtil.isEmpty(end_time)){			
				long etime = DateUtil.StringToLong(end_time +" 23:59:59", "yyyy-MM-dd HH:mm:ss");
				sql.append(" and ewr.create_time < "+(etime));
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
			sql.append(" and ewr.pay_status = 1 order by wash_record_id desc ");
			
			List<Map> list = this.baseDaoSupport.queryForList(sql.toString());
			
			ExcelUtil excelUtil = new ExcelUtil(); 
			 
			InputStream in = new FileInputStream(new File(EopSetting.EOP_PATH+"/excel/record.xls")) ;// FileUtil.getResourceAsStream("com/enation/app/shop/component/bonus/service/impl/bonus_list.xls");
			
			IPermissionManager permissionManager = SpringContextHolder.getBean("permissionManager");
			boolean b = permissionManager.checkHaveAuth(PermissionConfig.getAuthId("operator_city"));
			excelUtil.openModal( in );
			
			int i=1;
			for (Map record : list) {
				if(adminUser.getFounder() == 1 || !b){
					excelUtil.writeStringToCell(i, 0, StringUtil.isNull(record.get("uname"))); //会员
				}
				excelUtil.writeStringToCell(i, 1, StringUtil.isNull(record.get("machine_number"))); //机器编号
				excelUtil.writeStringToCell(i, 2, StringUtil.isNull(record.get("machine_name"))); //机器名称
				excelUtil.writeStringToCell(i, 3, StringUtil.isNull(record.get("address"))); //地点
				excelUtil.writeStringToCell(i, 4, StringUtil.isNull(record.get("wash_time"))); //洗车时间（s）
				excelUtil.writeStringToCell(i, 5, StringUtil.isNull(record.get("total_price"))); //总金额
				excelUtil.writeStringToCell(i, 6, StringUtil.isNull(record.get("discount_price"))); //优惠金额
				excelUtil.writeStringToCell(i, 7, StringUtil.isNull(record.get("pay_price"))); //支付金额
				excelUtil.writeStringToCell(i, 8, DateUtil.toString(new Date((Long)record.get("create_time")), "yyyy/MM/dd HH:mm:ss")); //创建时间
				i++;
			}

			String fileName = DateUtil.toString( new Date(),"yyyyMMddHHmmss");
			File file = new File(EopSetting.IMG_SERVER_PATH + "/record_excel");
			if (!file.exists()){
				file.mkdirs();
			}	
			
			String filePath = EopSetting.IMG_SERVER_PATH+"/record_excel/"+fileName+".xls";
			excelUtil.writeToFile(filePath);//aa
			return EopSetting.IMG_SERVER_DOMAIN+"/record_excel/"+fileName+".xls";
		} catch (Exception e) {
			e.printStackTrace();
		  return null;
		}
	}
	
	@Override
	public WashRecord getLastByMachineId(Integer car_machine_id, String member_id) {
		
		StringBuffer sql = new StringBuffer();
		
		sql.append(" select ewr.* from es_wash_record ewr ");
		sql.append(" where car_machine_id = ? and member_id = ? order by wash_record_id desc limit 0,1 ");
		
		List<WashRecord> list = this.baseDaoSupport.queryForList(sql.toString(), WashRecord.class, car_machine_id, member_id);
		WashRecord wr = null;
		
		if(list != null && list.size() > 0){
			wr = list.get(0);
		}
		
		return wr;
		
	}

	@Override
	@Transactional
	public void endWashCar(String wash_record_id, String price, String end_time) {
		WashRecord washRecord = this.getWashRecordById(wash_record_id);
		CarMachine carMachine = this.machineManager.getMachineById(washRecord.getCar_machine_id().toString());
		if(washRecord.getPay_status() == 1){
			//已经支付，直接返回
			return;
		}
		
		long end_time_long = DateUtil.StringToLong(end_time, "yyyy-MM-dd HH:mm:ss");
		washRecord.setWash_time(CurrencyUtil.mul((end_time_long - washRecord.getCreate_time()), 0.001).intValue());
		
		washRecord.setTotal_price(StringUtil.getDouble2(StringUtil.getDouble2(Double.valueOf(price))));//分->元
		String member_id = String.valueOf(washRecord.getMember_id());
		
		//比总金额小的优惠劵列表
		WashMemberCoupons wmc = null;
		List<WashMemberCoupons> wmcList = this.washMemberCouponsManager.getCanUseCoupons(member_id, washRecord.getTotal_price());
		if(wmcList != null && wmcList.size() != 0){
			wmc = wmcList.get(0);
			wmc.setIs_use(1);
			washRecord.setWash_member_coupon_id(wmc.getWash_member_coupons_id());
			washRecord.setDiscount_price(wmc.getDiscount());
		}
		
		washRecord.setUse_status(1);
		Member member = this.memberManager.getMemberByMemberId(member_id);
		
		//更改WashRecord支付状态
		washRecord.setPay_price(washRecord.getNeedPayMoney());
		washRecord.setPay_status(1);
		washRecord.setLess_water(CurrencyUtil.mul(CurrencyUtil.div(CurrencyUtil.div(washRecord.getWash_time(), 60d, 2), 5d, 2), 20.6).intValue());
		washRecord.setLess_electric(CurrencyUtil.mul(CurrencyUtil.div(CurrencyUtil.div(washRecord.getWash_time(), 60d, 2), 5d, 2), 0.08).intValue());
		washRecord.setSports_achieve(CurrencyUtil.mul(CurrencyUtil.div(CurrencyUtil.div(washRecord.getWash_time(), 60d, 2), 2d, 2), 4.25).intValue());
		washRecord.setUpdate_time(System.currentTimeMillis());
		
		member.setLess_water(member.getLess_water() + washRecord.getLess_water());
		member.setLess_electric(member.getLess_electric() + washRecord.getLess_electric());
		member.setSports_achieve(member.getSports_achieve() + washRecord.getSports_achieve());
		member.setWash_time(member.getWash_time() + washRecord.getWash_time());
		
		Long now_time = System.currentTimeMillis();
		Integer mac_member_id = carMachine.getMember_id();
		int p_num = carMachine.getPartner_num() + 1;
		Double income_price = CurrencyUtil.div(washRecord.getTotal_price(), p_num, 2);
		Double ratio = CurrencyUtil.div(1d, p_num, 2);
		
		//合伙人数量大于0的时候才有机器拥有者的合伙人收益
		if(carMachine.getPartner_num() > 0){
			//机器拥有者的合伙人收益
			MemberProfit memberProfit = new MemberProfit();
			memberProfit.setCreate_time(now_time);
			memberProfit.setMachine_id(washRecord.getCar_machine_id());
			memberProfit.setWash_record_id(washRecord.getWash_record_id());
			memberProfit.setMachine_name(carMachine.getMachine_name());
			memberProfit.setProfit_type(1);
			memberProfit.setProfit_ratio(ratio);
			memberProfit.setTotal_price(washRecord.getTotal_price());
			memberProfit.setIncome_price(income_price);
			memberProfit.setMember_id(mac_member_id);
			this.memberProfitManager.addMemberProfit(memberProfit);
			this.memberManager.addBalance(mac_member_id, income_price);
		}

		//合伙人的收益
		String p_phone = carMachine.getPartner_phone();
		if(!StringUtil.isEmpty(p_phone)){
			String[] p_phones = p_phone.split(",");
			List<Map> p_list = this.memberManager.getListByUnames(p_phone);
			Map<String, String> unameMap = new HashMap<>();
			//将合伙人放入map<uname, member_id>里面
			for(Map m : p_list){
				unameMap.put(m.get("uname").toString(), m.get("member_id").toString());
			}
			for(String uname : p_phones){
				Integer member_id_ = Integer.parseInt(unameMap.get(uname));
				MemberProfit memberProfit_ = new MemberProfit();
				memberProfit_.setCreate_time(now_time);
				memberProfit_.setMachine_id(washRecord.getCar_machine_id());
				memberProfit_.setWash_record_id(washRecord.getWash_record_id());
				memberProfit_.setMachine_name(carMachine.getMachine_name());
				memberProfit_.setProfit_type(1);
				memberProfit_.setProfit_ratio(ratio);
				memberProfit_.setTotal_price(washRecord.getTotal_price());
				memberProfit_.setIncome_price(income_price);
				memberProfit_.setMember_id(member_id_);
				this.memberProfitManager.addMemberProfit(memberProfit_);
				this.memberManager.addBalance(member_id_, income_price);
			}
		}
		
		//更新会员余额，增加consume记录，更改WashRecord支付状态，更改优惠劵使用状态
		this.memberManager.updateBalanceSub(member, washRecord, wmc);
		
		//没有合伙人的时候才分润
		if(carMachine.getPartner_num() != null && carMachine.getPartner_num() == 0){
			this.memberProfitManager.calculateProfit(washRecord);
		}
		//改变洗车机状态为未使用
		//this.machineManager.updateMachineIsUse("0", carMachine.getCar_machine_id().toString());
	}

	public WashMemberCouponsManager getWashMemberCouponsManager() {
		return washMemberCouponsManager;
	}

	public void setWashMemberCouponsManager(
			WashMemberCouponsManager washMemberCouponsManager) {
		this.washMemberCouponsManager = washMemberCouponsManager;
	}

	public MemberManager getMemberManager() {
		return memberManager;
	}

	public void setMemberManager(MemberManager memberManager) {
		this.memberManager = memberManager;
	}

	public IMemberProfitManager getMemberProfitManager() {
		return memberProfitManager;
	}

	public void setMemberProfitManager(IMemberProfitManager memberProfitManager) {
		this.memberProfitManager = memberProfitManager;
	}

	public IMachineManager getMachineManager() {
		return machineManager;
	}

	public void setMachineManager(IMachineManager machineManager) {
		this.machineManager = machineManager;
	}

	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}

	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}


	
}
