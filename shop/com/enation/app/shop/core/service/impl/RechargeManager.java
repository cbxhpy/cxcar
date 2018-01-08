package com.enation.app.shop.core.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.model.CarMachine;
import com.enation.app.shop.core.model.Recharge;
import com.enation.app.shop.core.service.IRechargeManager;
import com.enation.app.shop.core.utils.DateUtil;
import com.enation.app.shop.core.utils.DetailUtil;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.ExcelUtil;
import com.enation.framework.util.StringUtil;

/**
 * 充值明细管理
 * @author yexf
 * 2017-4-12
 */
public class RechargeManager extends BaseSupport<Recharge> implements IRechargeManager {

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int addRecharge(Recharge recharge) {
		this.baseDaoSupport.insert("es_recharge", recharge);
		return 0;
	}
	
	@Override
	public void delRecharge(Integer[] ids) {
		if (ids == null || ids.equals(""))
			return;
		String id_str = StringUtil.arrayToString(ids, ",");
		String sql = "delete from es_recharge where recharge_id in (" + id_str
				+ ")";
		this.baseDaoSupport.execute(sql);
	}

	@Override
	public Page getRechargePage(String member_id, int pageNo, int pageSize) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT * FROM es_recharge WHERE member_id = ? AND pay_status = 1 order by recharge_id desc ");
		Page page = this.baseDaoSupport.queryForPage(sql.toString(), pageNo, pageSize, Recharge.class, member_id);
		return page;
	}

	@Override
	public Recharge getRechargeBySn(String ordersn) {
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT * FROM es_recharge WHERE sn = ? ");
		
		List<Recharge> list = this.baseDaoSupport.queryForList(sql.toString(), Recharge.class, ordersn);
		
		if(list.size() != 0){
			return list.get(0);
		}else{
			return null;
		}
	}

	@Override
	public void updateRecharge(Recharge recharge) {
		this.baseDaoSupport.update("es_recharge", recharge, " recharge_id = " + recharge.getRecharge_id());
	}

	@Override
	public Page pageRecharge(String order, String sort, int page, int pageSize, String sn, 
			String uname, String start_time, String end_time, String pay_status) {
		order = order == null ? " er.recharge_id " : order;
		sort = sort == null ? " desc " : sort;
		StringBuffer sql = new StringBuffer(" select er.*, em.uname from es_recharge er ");
		sql.append(" left join es_member em on em.member_id = er.member_id where 1 = 1");
		if(!StringUtil.isEmpty(sn)){
			sql.append(" and er.sn like '%").append(sn).append("%'");
		}
		if(!StringUtil.isEmpty(uname)){
			sql.append(" and em.uname like '%").append(uname).append("%'");	
		}
		if(start_time!=null&&!StringUtil.isEmpty(start_time)){			
			long stime = DateUtil.StringToLong(start_time+" 00:00:00","yyyy-MM-dd HH:mm:ss");
			sql.append(" and er.create_time > "+stime);
		}
		if(end_time!=null&&!StringUtil.isEmpty(end_time)){			
			long etime = DateUtil.StringToLong(end_time +" 23:59:59", "yyyy-MM-dd HH:mm:ss");
			sql.append(" and er.create_time < "+(etime));
		}
		if(!StringUtil.isEmpty(pay_status)){
			sql.append(" and er.pay_status = ").append(pay_status);
		}
		sql.append(" order by ").append(order).append(" ").append(sort);
		Page rpage = this.daoSupport.queryForPage(sql.toString(), page, pageSize, Recharge.class);
		return rpage;
	}

	@Override
	public String recordExportToExcel(String start_time, String end_time) {
		try {
			
			StringBuffer sql = new StringBuffer(" select er.*, em.uname from es_recharge er ");
			sql.append(" left join es_member em on em.member_id = er.member_id where 1 = 1");
			if(start_time!=null&&!StringUtil.isEmpty(start_time)){			
				long stime = DateUtil.StringToLong(start_time+" 00:00:00","yyyy-MM-dd HH:mm:ss");
				sql.append(" and er.create_time > "+stime);
			}
			if(end_time!=null&&!StringUtil.isEmpty(end_time)){			
				long etime = DateUtil.StringToLong(end_time +" 23:59:59", "yyyy-MM-dd HH:mm:ss");
				sql.append(" and er.create_time < "+(etime));
			}
			sql.append(" order by er.recharge_id desc ");
			
			List<Map> list = this.baseDaoSupport.queryForList(sql.toString());
			
			ExcelUtil excelUtil = new ExcelUtil(); 
			 
			InputStream in = new FileInputStream(new File(EopSetting.EOP_PATH+"/excel/recharge.xls")) ;// FileUtil.getResourceAsStream("com/enation/app/shop/component/bonus/service/impl/bonus_list.xls");
			excelUtil.openModal( in );
			
			int i=1;
			for (Map record : list) {
				excelUtil.writeStringToCell(i, 0, StringUtil.isNull(record.get("sn"))); //编号
				excelUtil.writeStringToCell(i, 1, StringUtil.isNull(record.get("uname"))); //手机号
				excelUtil.writeStringToCell(i, 2, StringUtil.isNull(record.get("price"))); //充值金额
				excelUtil.writeStringToCell(i, 3, StringUtil.isNull(record.get("balance"))); //获得金额
				excelUtil.writeStringToCell(i, 4, DetailUtil.getPayType(StringUtil.isNullRt0(record.get("pay_type")))); //支付类型（1：支付宝 2：微信）
				excelUtil.writeStringToCell(i, 5, DetailUtil.getPayStatus(StringUtil.isNull(record.get("pay_status")))); //支付状态（0：未支付 1：已支付）
				excelUtil.writeStringToCell(i, 6, DateUtil.toString(new Date((Long)record.get("create_time")), "yyyy/MM/dd HH:mm:ss")); //创建时间
				if(!StringUtil.isEmpty(StringUtil.isNull(record.get("pay_time")))){
					excelUtil.writeStringToCell(i, 7, DateUtil.toString(new Date((Long)record.get("pay_time")), "yyyy/MM/dd HH:mm:ss")); //支付时间
				}
				i++;
			}

			String fileName = DateUtil.toString( new Date(),"yyyyMMddHHmmss");
			File file = new File(EopSetting.IMG_SERVER_PATH + "/recharge_excel");
			if (!file.exists()){
				file.mkdirs();
			}	
			
			String filePath = EopSetting.IMG_SERVER_PATH+"/recharge_excel/"+fileName+".xls";
			excelUtil.writeToFile(filePath);
			return EopSetting.IMG_SERVER_DOMAIN+"/recharge_excel/"+fileName+".xls";
		} catch (Exception e) {
			e.printStackTrace();
		  return null;
		}
	}


}
