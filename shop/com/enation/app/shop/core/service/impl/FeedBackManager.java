package com.enation.app.shop.core.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.model.FeedBack;
import com.enation.app.shop.core.service.IFeedBackManager;
import com.enation.eop.resource.model.Dictionary;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

/**
 * @author 创建人：yexf
 * @version 版本号：V1.0
 * @Description 功能说明：用户反馈业务
 * @date 创建时间：2017年7月7日
 */
public class FeedBackManager extends BaseSupport<FeedBack> implements IFeedBackManager {

	@Override
	public void addFeedBack(FeedBack feedBack) {
		this.baseDaoSupport.insert("es_feedback", feedBack);

	}
	
	@Override
	public void delFeedBacks(Integer[] ids) {
		if (ids == null || ids.equals(""))
			return;
		String id_str = StringUtil.arrayToString(ids, ",");
		String sql = "delete from es_feedback where feedback_id in (" + id_str
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
	public Page pageFeedBack(String order, String sort, int page, int pageSize,
		String machine_number, String uname) {
		order = order == null ? " efb.feedback_id " : order;
		sort = sort == null ? " desc " : sort;
		StringBuffer sql = new StringBuffer(" select efb.*, em.uname from es_feedback efb ");
		sql.append(" left join es_member em on em.member_id = efb.member_id where 1 = 1 ");
		if(!StringUtil.isEmpty(machine_number)){
			sql.append(" and efb.machine_number like '%").append(machine_number).append("%'");
		}
		if(!StringUtil.isEmpty(uname)){
			sql.append(" and em.uname like '%").append(uname).append("%'");	
		}
		
		sql.append(" order by ").append(order).append(" ").append(sort);
		Page rpage = this.daoSupport.queryForPage(sql.toString(), page, pageSize, FeedBack.class);
		return rpage;
	}

	@Override
	public FeedBack getFeedBack(String feed_back_id) {
		StringBuffer sql = new StringBuffer();
		
		sql.append(" select efb.*, em.uname from es_feedback efb ");
		sql.append(" left join es_member em on em.member_id = efb.member_id ");
		sql.append(" where efb.feedback_id = ? ");
		
		List<FeedBack> list = this.baseDaoSupport.queryForList(sql.toString(), FeedBack.class, feed_back_id);
		
		FeedBack feedBack = null;
		if(list != null && list.size() != 0){
			feedBack = list.get(0);
		}
		
		return feedBack;
	}

	@Override
	public void updFeedBack(FeedBack feedBack) {
		this.baseDaoSupport.update("es_feedback", feedBack, " feedback_id = "+feedBack.getFeedback_id());
	}

}
