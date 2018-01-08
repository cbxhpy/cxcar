package com.enation.app.shop.core.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.base.core.service.auth.IPermissionManager;
import com.enation.app.base.core.service.auth.impl.PermissionConfig;
import com.enation.app.shop.core.model.SpreadRecord;
import com.enation.app.shop.core.service.ISpreadRecordManager;
import com.enation.app.shop.core.utils.DateUtil;
import com.enation.app.shop.core.utils.DetailUtil;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.ExcelUtil;
import com.enation.framework.util.StringUtil;

/**
 * 推广交易记录
 * @author yexf
 * 2017-4-12
 */
public class SpreadRecordManager extends BaseSupport<SpreadRecord> implements ISpreadRecordManager {

	private IAdminUserManager adminUserManager;
	
	@Override
	public void addSpreadRecord(SpreadRecord spreadRecord) {
		this.baseDaoSupport.insert("es_spread_record", spreadRecord);
	}
	
	@Override
	public void delSpreadRecord(Integer[] ids) {
		if (ids == null || ids.equals(""))
			return;
		String id_str = StringUtil.arrayToString(ids, ",");
		String sql = "delete from es_spread_record where spread_record_id in (" + id_str
				+ ")";
		this.baseDaoSupport.execute(sql);
	}
	
	@Override
	public void updateForMap(Map<String, Object> spreadRecord, Map map) {
		this.baseDaoSupport.update("es_spread_record", spreadRecord, map);
	}

	@Override
	public Page pageSpreadRecord(String order, String sort, int pageNo, int pageSize, 
			String uname, String name, String spread_status, String type, String member_id, String start_time, String end_time) {
		order = order == null ? " esr.spread_record_id " : order;
		sort = sort == null ? " desc " : sort;
		StringBuffer sql = new StringBuffer(" select esr.*, em.uname, em.name from es_spread_record esr ");
		sql.append(" left join es_member em on em.member_id = esr.member_id where 1 = 1 ");
		
		if(!StringUtil.isEmpty(uname)){
			sql.append(" and em.uname like '%").append(uname).append("%'");
		}
		if(!StringUtil.isEmpty(name)){
			sql.append(" and em.name like '%").append(name).append("%'");
		}
		if(!StringUtil.isEmpty(spread_status)){
			sql.append(" and esr.spread_status = ").append(spread_status);
		}
		if(!StringUtil.isEmpty(type)){
			sql.append(" and esr.type = ").append(type);
		}
		if(!StringUtil.isEmpty(member_id)){
			sql.append(" and esr.member_id = ").append(member_id);
		}
		if(start_time!=null&&!StringUtil.isEmpty(start_time)){			
			long stime = DateUtil.StringToLong(start_time+" 00:00:00","yyyy-MM-dd HH:mm:ss");
			sql.append(" and esr.create_time > "+stime);
		}
		if(end_time!=null&&!StringUtil.isEmpty(end_time)){			
			long etime = DateUtil.StringToLong(end_time +" 23:59:59", "yyyy-MM-dd HH:mm:ss");
			sql.append(" and esr.create_time < "+(etime));
		}
		
		AdminUser adminUser = this.adminUserManager.getCurrentUser();
		if(adminUser.getFounder() != 1){
			sql.append(" and em.adminuser_id = ").append(adminUser.getUserid());
		}
		
		sql.append(" order by ").append(order).append(" ").append(sort); 
		Page rpage = this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize, SpreadRecord.class);
		return rpage;
	}

	@Override
	public Page getSpreadRecordPage(String member_id, int pageNo,
			int pageSize) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT esr.* FROM es_spread_record esr WHERE esr.member_id = ? ");
		sql.append(" order by esr.spread_record_id desc ");
		
		Page page = this.baseDaoSupport.queryForPage(sql.toString(), pageNo, pageSize, SpreadRecord.class, member_id);
		return page;
	}

	
	@Override
	public List<SpreadRecord> getRecentSellerList(String addr_lat, String addr_lng) {

		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT DISTINCT es.*, eo.order_id, Cal_Distance_Fun(?, ?, es.seller_lat, es.seller_lng) distance ");
		sql.append(" FROM es_order eo LEFT JOIN es_seller es ON es.seller_id = eo.seller_id WHERE ");
		//GROUP BY eo.seller_id
		sql.append(" eo.seller_id IS NOT NULL ORDER BY eo.order_id DESC LIMIT 0,5 ");
			
		List<SpreadRecord> sellerList = this.baseDaoSupport.queryForList(sql.toString(), SpreadRecord.class, addr_lat, addr_lng);
		
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
			
		Page sellerPage = this.baseDaoSupport.queryForPage2(sql.toString(), Integer.parseInt(page_no), Integer.parseInt(page_size), SpreadRecord.class, params.toArray());
		
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
	public SpreadRecord getSpreadRecordById(String spread_record_id) {
		StringBuffer sql = new StringBuffer();
		sql.append(" select esr.* from es_spread_record esr ");
		sql.append(" where esr.spread_record_id = ? ");
		List<SpreadRecord> list = this.baseDaoSupport.queryForList(sql.toString(), SpreadRecord.class, spread_record_id);
		SpreadRecord spreadRecord = null;
		if(list != null && list.size() !=0){
			spreadRecord = list.get(0);
		}
		return spreadRecord;
	}
	
	@Override
	public Map getCountMessage() {

		Map<String, Integer> countMap = new HashMap<String, Integer>();
		
		StringBuffer sql = new StringBuffer();
		
		sql.append(" select count(*) num from es_member where spread_status = 1 ");
		
		AdminUser adminUser = this.adminUserManager.getCurrentUser();
		if(adminUser.getFounder() != 1){
			sql.append(" and adminuser_id = ").append(adminUser.getUserid());
		}
		
		Map map = this.baseDaoSupport.queryForMap(sql.toString());
		
		StringBuffer all_sql = new StringBuffer();
		
		all_sql.append(" select count(*) num from es_member where spread_status = 2 ");
		
		if(adminUser.getFounder() != 1){
			all_sql.append(" and adminuser_id = ").append(adminUser.getUserid());
		}
		
		Map all_map = this.baseDaoSupport.queryForMap(all_sql.toString());
		
		countMap.put("all_num", Integer.parseInt(all_map.get("num").toString()));
		countMap.put("num", Integer.parseInt(map.get("num").toString()));
		
		return countMap;
	}
	
	@Override
	public String recordExportToExcel(String start_time, String end_time, String member_id) {
		try {
			
			StringBuffer sql = new StringBuffer();
			sql.append(" select esr.*, em.uname, em.name from es_spread_record esr ");
			sql.append(" left join es_member em on em.member_id = esr.member_id where 1 = 1 ");
			if(start_time!=null&&!StringUtil.isEmpty(start_time)){			
				long stime = DateUtil.StringToLong(start_time+" 00:00:00","yyyy-MM-dd HH:mm:ss");
				sql.append(" and esr.create_time > "+stime);
			}
			if(end_time!=null&&!StringUtil.isEmpty(end_time)){			
				long etime = DateUtil.StringToLong(end_time +" 23:59:59", "yyyy-MM-dd HH:mm:ss");
				sql.append(" and esr.create_time < "+(etime));
			}
			if(!StringUtil.isEmpty(member_id)){
				sql.append(" and esr.member_id = ").append(member_id);
			}
			AdminUser adminUser = this.adminUserManager.getCurrentUser();
			if(adminUser.getFounder() != 1){
				sql.append(" and em.adminuser_id = ").append(adminUser.getUserid());
			}
			sql.append(" order by esr.spread_record_id desc ");
			
			List<Map> list = this.baseDaoSupport.queryForList(sql.toString());
			
			ExcelUtil excelUtil = new ExcelUtil(); 
			 
			InputStream in = new FileInputStream(new File(EopSetting.EOP_PATH+"/excel/spread.xls")) ;// FileUtil.getResourceAsStream("com/enation/app/shop/component/bonus/service/impl/bonus_list.xls");
			excelUtil.openModal( in );
			
			int i=1;
			for (Map record : list) {
				excelUtil.writeStringToCell(i, 0, StringUtil.isNull(record.get("uname"))); //推广员
				excelUtil.writeStringToCell(i, 1, StringUtil.isNull(record.get("name"))); //名称
				excelUtil.writeStringToCell(i, 2, StringUtil.isNull(record.get("price"))); //金额
				excelUtil.writeStringToCell(i, 3, StringUtil.isNull(record.get("remark"))); //备注信息
				excelUtil.writeStringToCell(i, 4, DetailUtil.get0_1Type(StringUtil.isNull(record.get("spread_status")))); //提现是否处理（0：未处理，1：已处理）
				excelUtil.writeStringToCell(i, 5, DetailUtil.getSpreadType(StringUtil.isNull(record.get("type")))); //交易类型
				excelUtil.writeStringToCell(i, 6, DateUtil.toString(new Date((Long)record.get("create_time")), "yyyy/MM/dd HH:mm:ss")); //创建时间
				i++;
			}

			String fileName = DateUtil.toString( new Date(),"yyyyMMddHHmmss");
			File file = new File(EopSetting.IMG_SERVER_PATH + "/spread_excel");
			if (!file.exists()){
				file.mkdirs();
			}	
			
			String filePath = EopSetting.IMG_SERVER_PATH+"/spread_excel/"+fileName+".xls";
			excelUtil.writeToFile(filePath);
			return EopSetting.IMG_SERVER_DOMAIN+"/spread_excel/"+fileName+".xls";
		} catch (Exception e) {
			e.printStackTrace();
		  return null;
		}
	}
	
	@Override
	public String countSpread(String start_time, String end_time, String member_id) {
		try {
			
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT SUM(price) total_price FROM es_recharge er WHERE er.pay_status = 1 ");
			sql.append(" AND member_id IN (select member_id from es_member where parent_id in (SELECT member_id FROM es_member em WHERE 1 = 1 and spread_status = 2 ");
			if(!StringUtil.isEmpty(member_id)){
				sql.append(" AND em.member_id = ").append(member_id);
			}
			AdminUser adminUser = this.adminUserManager.getCurrentUser();
			if(adminUser.getFounder() != 1){
				sql.append(" and em.adminuser_id = ").append(adminUser.getUserid());
			}
			sql.append(" )) ");
			if(start_time!=null&&!StringUtil.isEmpty(start_time)){			
				long stime = DateUtil.StringToLong(start_time+" 00:00:00","yyyy-MM-dd HH:mm:ss");
				sql.append(" and er.create_time > "+stime);
			}
			if(end_time!=null&&!StringUtil.isEmpty(end_time)){			
				long etime = DateUtil.StringToLong(end_time +" 23:59:59", "yyyy-MM-dd HH:mm:ss");
				sql.append(" and er.create_time < "+(etime));
			}
			
			Map map = this.baseDaoSupport.queryForMap(sql.toString());
			Double total_price = Double.parseDouble(StringUtil.isNullRt0(map.get("total_price")));
			return total_price.toString();
		} catch (Exception e) {
			e.printStackTrace();
		  return null;
		}
	}
	
	@Override
	public Boolean checkOneceSpread(Integer member_id, Integer use_member_id) {

		StringBuffer sql = new StringBuffer();
		
		sql.append(" select spread_record_id from es_spread_record where member_id = ? and use_member_id = ? and type = 0 ");
		
		List<Map> list = this.baseDaoSupport.queryForList(sql.toString(), member_id, use_member_id);
		
		if(list != null && list.size() != 0){
			return false;
		}else{
			return true;
		}
		
	}

	@Override
	public void updateSpreadRecord(SpreadRecord spreadRecord) {
		this.baseDaoSupport.update("es_spread_record", spreadRecord, " spread_record_id = " + spreadRecord.getSpread_record_id());
	}

	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}

	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}

	
}
