package com.enation.app.shop.core.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.base.core.service.auth.IPermissionManager;
import com.enation.app.base.core.service.auth.impl.PermissionConfig;
import com.enation.app.shop.core.model.Consume;
import com.enation.app.shop.core.model.SpreadRecord;
import com.enation.app.shop.core.model.TransferLog;
import com.enation.app.shop.core.service.IConsumeManager;
import com.enation.app.shop.core.service.IDictionaryManager;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.app.shop.core.service.ISpreadRecordManager;
import com.enation.app.shop.core.service.ITransferLogManager;
import com.enation.app.shop.core.utils.DateUtil;
import com.enation.app.shop.core.utils.DetailUtil;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.ExcelUtil;
import com.enation.framework.util.StringUtil;

/**
 * 转让交易记录
 * @author yexf
 * @date 2018-1-4 下午8:26:54 
 */
public class TransferLogManager extends BaseSupport<TransferLog> implements ITransferLogManager {

	private IAdminUserManager adminUserManager;
	private IMemberManager memberManager;
	private IConsumeManager consumeManager;
	private IDictionaryManager dictionaryManager;
	
	@Override
	public void addTransferLog(TransferLog transferLog) {
		this.baseDaoSupport.insert("es_transfer_log", transferLog);
	}
	
	@Override
	public void delTransferLog(Integer[] ids) {
		if (ids == null || ids.equals(""))
			return;
		String id_str = StringUtil.arrayToString(ids, ",");
		String sql = "delete from es_transfer_log where transfer_log_id in (" + id_str
				+ ")";
		this.baseDaoSupport.execute(sql);
	}
	
	@Override
	public void updateForMap(Map<String, Object> transferLog, Map map) {
		this.baseDaoSupport.update("es_transfer_log", transferLog, map);
	}

	@Override
	public Page pageTransferLog(String order, String sort, int pageNo, int pageSize, 
			String uname, String to_uname, String member_id, String start_time, String end_time) {
		order = order == null ? " etl.transfer_log_id " : order;
		sort = sort == null ? " desc " : sort;
		StringBuffer sql = new StringBuffer(" select etl.*, em.uname, toem.uname to_uname from es_transfer_log etl ");
		sql.append(" left join es_member em on em.member_id = etl.member_id ");
		sql.append(" left join es_member toem on toem.member_id = etl.to_member_id where 1 = 1 ");
		
		if(!StringUtil.isEmpty(uname)){
			sql.append(" and em.uname like '%").append(uname).append("%'");
		}
		if(!StringUtil.isEmpty(to_uname)){
			sql.append(" and toem.uname like '%").append(to_uname).append("%'");
		}
		if(!StringUtil.isEmpty(member_id)){
			sql.append(" and etl.member_id = ").append(member_id);
		}
		if(start_time!=null&&!StringUtil.isEmpty(start_time)){			
			long stime = DateUtil.StringToLong(start_time+" 00:00:00","yyyy-MM-dd HH:mm:ss");
			sql.append(" and etl.create_time > "+stime);
		}
		if(end_time!=null&&!StringUtil.isEmpty(end_time)){			
			long etime = DateUtil.StringToLong(end_time +" 23:59:59", "yyyy-MM-dd HH:mm:ss");
			sql.append(" and etl.create_time < "+(etime));
		}
		
		sql.append(" order by ").append(order).append(" ").append(sort); 
		Page rpage = this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize, TransferLog.class);
		return rpage;
	}

	@Override
	public Page getTransferLogPage(String member_id, int pageNo,
			int pageSize) {
		StringBuffer sql = new StringBuffer();
		sql.append(" select etl.*, em.uname, toem.uname to_uname from es_transfer_log etl ");
		sql.append(" left join es_member em on em.member_id = etl.member_id ");
		sql.append(" left join es_member toem on toem.member_id = etl.to_member_id where 1 = 1 ");
		sql.append(" and (etl.member_id = ? or etl.to_member_id = ?) ");
		sql.append(" order by etl.transfer_log_id desc ");
		
		Page page = this.baseDaoSupport.queryForPage(sql.toString(), pageNo, pageSize, TransferLog.class, member_id, member_id);
		return page;
	}

	
	@Override
	public List<TransferLog> getRecentSellerList(String addr_lat, String addr_lng) {

		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT DISTINCT es.*, eo.order_id, Cal_Distance_Fun(?, ?, es.seller_lat, es.seller_lng) distance ");
		sql.append(" FROM es_order eo LEFT JOIN es_seller es ON es.seller_id = eo.seller_id WHERE ");
		//GROUP BY eo.seller_id
		sql.append(" eo.seller_id IS NOT NULL ORDER BY eo.order_id DESC LIMIT 0,5 ");
			
		List<TransferLog> sellerList = this.baseDaoSupport.queryForList(sql.toString(), TransferLog.class, addr_lat, addr_lng);
		
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
			
		Page sellerPage = null;//this.baseDaoSupport.queryForPage2(sql.toString(), Integer.parseInt(page_no), Integer.parseInt(page_size), SpreadRecord.class, params.toArray());
		
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
		List<SpreadRecord> list = null;//this.baseDaoSupport.queryForList(sql.toString(), SpreadRecord.class, spread_record_id);
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
	public String recordExportToExcel(String start_time, String end_time, String uname, String to_uname) {
		try {
			
			StringBuffer sql = new StringBuffer();
			sql.append(" select etl.*, em.uname, toem.uname to_uname from es_transfer_log etl ");
			sql.append(" left join es_member em on em.member_id = etl.member_id ");
			sql.append(" left join es_member toem on toem.member_id = etl.to_member_id where 1 = 1 ");
			if(start_time!=null&&!StringUtil.isEmpty(start_time)){			
				long stime = DateUtil.StringToLong(start_time+" 00:00:00","yyyy-MM-dd HH:mm:ss");
				sql.append(" and etl.create_time > "+stime);
			}
			if(end_time!=null&&!StringUtil.isEmpty(end_time)){			
				long etime = DateUtil.StringToLong(end_time +" 23:59:59", "yyyy-MM-dd HH:mm:ss");
				sql.append(" and etl.create_time < "+(etime));
			}
			if(!StringUtil.isEmpty(uname)){
				sql.append(" and em.uname like '%").append(uname).append("%'");
			}
			if(!StringUtil.isEmpty(to_uname)){
				sql.append(" and toem.uname like '%").append(to_uname).append("%'");
			}

			sql.append(" order by etl.transfer_log_id desc ");
			
			List<Map> list = this.baseDaoSupport.queryForList(sql.toString());
			
			ExcelUtil excelUtil = new ExcelUtil(); 
			 
			InputStream in = new FileInputStream(new File(EopSetting.EOP_PATH+"/excel/transfer.xls")) ;// FileUtil.getResourceAsStream("com/enation/app/shop/component/bonus/service/impl/bonus_list.xls");
			excelUtil.openModal( in );
			
			int i=1;
			for (Map record : list) {
				excelUtil.writeStringToCell(i, 0, StringUtil.isNull(record.get("uname"))); //转让会员 
				excelUtil.writeStringToCell(i, 1, StringUtil.isNull(record.get("to_uname"))); //接收会员 
				excelUtil.writeStringToCell(i, 2, StringUtil.isNull(record.get("price"))); //金额
				excelUtil.writeStringToCell(i, 3, StringUtil.isNull(record.get("service_charge"))); //手续费
				excelUtil.writeStringToCell(i, 4, DateUtil.toString(new Date((Long)record.get("create_time")), "yyyy/MM/dd HH:mm:ss")); //创建时间
				i++;
			}

			String fileName = DateUtil.toString( new Date(),"yyyyMMddHHmmss");
			File file = new File(EopSetting.IMG_SERVER_PATH + "/transfer_excel");
			if (!file.exists()){
				file.mkdirs();
			}	
			
			String filePath = EopSetting.IMG_SERVER_PATH+"/transfer_excel/"+fileName+".xls";
			excelUtil.writeToFile(filePath);
			return EopSetting.IMG_SERVER_DOMAIN+"/transfer_excel/"+fileName+".xls";
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
	public void updateTransferLog(TransferLog transferLog) {
		this.baseDaoSupport.update("es_transfer_log", transferLog, " transfer_log_id = " + transferLog.getTransfer_log_id());
	}
	
	@Transactional
	@Override
	public void transferBalance(String member_id, String to_member_id,
			String price) {
		Map map = this.dictionaryManager.getDataMap("transfer_rate");
		String transfer_rate = StringUtil.isNullRt0(map.get("transfer_rate"));
		Double service_charge = CurrencyUtil.mul(CurrencyUtil.mul(Double.parseDouble(price), Double.parseDouble(transfer_rate)), 0.01d);
		long create_time = System.currentTimeMillis();
		TransferLog transferLog = new TransferLog();
		transferLog.setCreate_time(create_time);
		transferLog.setMember_id(Integer.parseInt(member_id));
		transferLog.setPrice(CurrencyUtil.add(Double.parseDouble(price), service_charge));
		transferLog.setTo_member_id(Integer.parseInt(to_member_id));
		transferLog.setService_charge(service_charge);
		this.addTransferLog(transferLog);
		int transfer_log_id = this.baseDaoSupport.getLastId("es_transfer_log");
		
		this.memberManager.addBalance(Integer.parseInt(to_member_id), Double.parseDouble(price));
		this.memberManager.subBalanceNew(member_id, CurrencyUtil.add(Double.parseDouble(price), service_charge));
		
		Consume consume = new Consume();
		consume.setCreate_time(create_time);
		consume.setSign_id(transfer_log_id);
		consume.setPrice(CurrencyUtil.add(Double.parseDouble(price), service_charge));
		consume.setType(3);
		consume.setMember_id(Integer.parseInt(member_id));
		this.consumeManager.addConsume(consume);
		
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

	public IDictionaryManager getDictionaryManager() {
		return dictionaryManager;
	}

	public void setDictionaryManager(IDictionaryManager dictionaryManager) {
		this.dictionaryManager = dictionaryManager;
	}

	
}
