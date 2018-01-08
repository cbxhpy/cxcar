package com.enation.app.shop.core.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enation.app.base.core.model.AdvMapper;
import com.enation.app.shop.core.model.PushMsg;
import com.enation.app.shop.core.service.IPushMsgManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

/**
 * @author 创建人：Administrator
 * @version 版本号：V1.0
 * @Description 功能说明：推送信息
 * @date 创建时间：2017年3月25日
 */
public class PushMsgManager extends BaseSupport<PushMsg> implements IPushMsgManager {

	@Override
	public void addPushMsg(PushMsg pushMsg) {
		this.baseDaoSupport.insert("es_push_msg", pushMsg);

	}
	
	@Override
	public void delPushMsgs(Integer[] ids) {
		if (ids == null || ids.equals(""))
			return;
		String id_str = StringUtil.arrayToString(ids, ",");
		String sql = "delete from es_push_msg where push_msg_id in (" + id_str
				+ ")";
		this.baseDaoSupport.execute(sql);
	}
	
	@Override
	public PushMsg getPushMsgDetail(String push_msg_id) {
		PushMsg pushMsg = this.baseDaoSupport.queryForObject("select * from es_push_msg where push_msg_id = ?", PushMsg.class, push_msg_id);
		return pushMsg;
	}

	@Override
	public Page pagePushMsg(String order, int page, int pageSize) {
		order = order == null ? " push_msg_id desc " : order;
		String sql = " select * from es_push_msg epm ";
		sql += " order by " + order; 
		Page rpage = this.daoSupport.queryForPage(sql, page, pageSize,new AdvMapper());
		return rpage;
	}
	
	@Override
	public Page pagePushMsgByMemberId(String member_id, String order, int page, int pageSize) {
		
		order = order == null ? " push_msg_id desc " : order;
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT epm.*, emp.member_id FROM es_push_msg epm ");
		sql.append(" LEFT JOIN (SELECT * FROM es_member_push WHERE member_id = ?) emp  ");
		sql.append(" ON epm.push_msg_id = emp.push_msg_id ");
		
		sql.append(" order by ").append(order); 
		Page pushMsgPage = this.daoSupport.queryForPage(sql.toString(), page, pageSize, PushMsg.class, member_id);
		return pushMsgPage;
	}
	
	@Override
	public void updatePushMsg(PushMsg pushMsg) {
		this.baseDaoSupport.update("es_push_msg", pushMsg, "push_msg_id = " + pushMsg.getPush_msg_id());
	}
	
	@Override
	public List<PushMsg> listPushMsgByMemberId(String member_id) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT epm.*, emp.member_id FROM es_push_msg epm ");
		sql.append(" LEFT JOIN (SELECT * FROM es_member_push WHERE member_id = ?) emp  ");
		sql.append(" ON epm.push_msg_id = emp.push_msg_id ");
		
		sql.append(" order by push_msg_id desc "); 
		List<PushMsg> list = this.baseDaoSupport.queryForList(sql.toString(), PushMsg.class, member_id);
		return list;
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

}
