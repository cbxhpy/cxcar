package com.enation.app.shop.core.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enation.app.shop.core.model.CarMachine;
import com.enation.app.shop.core.model.Consume;
import com.enation.app.shop.core.model.PushMsg;
import com.enation.app.shop.core.service.IConsumeManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

/**
 * 消费明细管理
 * @author yexf
 * 2017-4-12
 */
public class ConsumeManager extends BaseSupport<Consume> implements IConsumeManager {

	@Override
	public void addConsume(Consume consume) {
		this.baseDaoSupport.insert("es_consume", consume);
	}
	
	@Override
	public void delConsume(Integer[] ids) {
		if (ids == null || ids.equals(""))
			return;
		String id_str = StringUtil.arrayToString(ids, ",");
		String sql = "delete from es_consume where consume_id in (" + id_str
				+ ")";
		this.baseDaoSupport.execute(sql);
	}

	@Override
	public Page getConsumePage(String member_id, int pageNo, int pageSize) {

		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT * FROM es_consume WHERE member_id = ? order by consume_id desc ");
		
		Page page = this.baseDaoSupport.queryForPage(sql.toString(), pageNo, pageSize, Consume.class, member_id);
		
		return page;
	}

	@Override
	public Page pageConsume(String order, String sort, int page, int pageSize, String uname, String machine_number) {
		order = order == null ? " ec.consume_id " : order;
		sort = sort == null ? " desc " : sort;
		StringBuffer sql = new StringBuffer(" SELECT ec.*, em.uname, ecm.machine_number FROM es_consume ec ");
		sql.append(" left join es_member em on em.member_id = ec.member_id ");
		sql.append(" LEFT JOIN es_wash_record ewr ON ewr.wash_record_id = ec.sign_id AND ec.type = '1' ");
		sql.append(" LEFT JOIN es_car_machine ecm ON ecm.car_machine_id = ewr.car_machine_id where 1 = 1 ");
		if(!StringUtil.isEmpty(uname)){
			sql.append(" and em.uname like '%").append(uname).append("%'");
		}
		if(!StringUtil.isEmpty(machine_number)){
			sql.append(" and ecm.machine_number like '%").append(machine_number).append("%'");
		}
		sql.append(" order by ").append(order).append(" ").append(sort);
		Page rpage = this.daoSupport.queryForPage(sql.toString(), page, pageSize, Consume.class);
		return rpage;
	}

}
