package com.enation.app.shop.core.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.AdvMapper;
import com.enation.app.shop.core.model.PushMsg;
import com.enation.app.shop.core.model.WashCoupons;
import com.enation.app.shop.core.model.WashMemberCoupons;
import com.enation.app.shop.core.service.IDictionaryManager;
import com.enation.app.shop.core.service.IWashCouponsManager;
import com.enation.app.shop.core.utils.DateUtil;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

/**
 * 洗车优惠劵管理
 * @author yexf
 * 2017-4-26
 */
public class WashCouponsManager extends BaseSupport<WashCoupons> implements IWashCouponsManager {

	private IDictionaryManager dictionaryManager;
	
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
	@Transactional
	public void receiveCoupons(Integer recomId, Integer beRecomId) {
		int count=this.countWashMemberCoupons(beRecomId);
		if(count>0){
			throw new IllegalArgumentException("您已领取过洗车券，不要贪心哦~");
		}
		WashCoupons washCoupons=this.getWashCoupons(recomId, 2);
		if(washCoupons==null){
			washCoupons=new WashCoupons();
			washCoupons.setNum(-1);
			washCoupons.setReceive_num(-1);
			washCoupons.setCreate_time(System.currentTimeMillis());
			washCoupons.setMember_id(recomId);
			washCoupons.setType(2);
			this.addWashCoupons(washCoupons);
			washCoupons.setWash_coupons_id(this.baseDaoSupport.getLastId("es_wash_coupons"));
		}
		Map map = this.dictionaryManager.getDataMap("coupons_set");//优惠劵配置项
		String use_day= StringUtil.isNull(map.get("use_day"));//有效天数
		String discount= StringUtil.isNull(map.get("discount"));//优惠劵金额
		//String num= StringUtil.isNull(map.get("num"));//可领取总数
		Long create_time=System.currentTimeMillis();
		Long end_time=DateUtil.getDateAfter(new Date(), Integer.valueOf(use_day)).getTime();
		//推荐人
		for (int i = 0; i < 5; i++) {
			WashMemberCoupons washMemberCoupons=new WashMemberCoupons();
			washMemberCoupons.setWash_coupons_id(washCoupons.getWash_coupons_id());
			washMemberCoupons.setMember_id(recomId);
			washMemberCoupons.setDiscount(Double.valueOf(discount));
			washMemberCoupons.setCreate_time(create_time);
			washMemberCoupons.setEnd_time(end_time);
			washMemberCoupons.setIs_use(0);
			this.addWashMemberCoupons(washMemberCoupons);
		}
		//被推荐人
		for (int i = 0; i < 5; i++) {
			WashMemberCoupons washMemberCoupons=new WashMemberCoupons();
			washMemberCoupons.setWash_coupons_id(washCoupons.getWash_coupons_id());
			washMemberCoupons.setMember_id(beRecomId);
			washMemberCoupons.setDiscount(Double.valueOf(discount));
			washMemberCoupons.setCreate_time(create_time);
			washMemberCoupons.setEnd_time(end_time);
			washMemberCoupons.setIs_use(0);
			this.addWashMemberCoupons(washMemberCoupons);
		}
	}
	
	/**
	 * 获取洗车券
	 * @param memberId
	 * @param type
	 * @return
	 */
	public WashCoupons getWashCoupons(Integer memberId, Integer type){
		String sql = "SELECT * FROM es_wash_coupons WHERE type=2 AND member_id=?";
		List<WashCoupons> list = this.daoSupport.queryForList(sql, WashCoupons.class, memberId);
		if (list == null || list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}
	
	/**
	 * 添加洗车券
	 * @param washCoupons
	 */
	public void addWashCoupons(WashCoupons washCoupons) {
		this.baseDaoSupport.insert("es_wash_coupons", washCoupons);

	}
	
	/**
	 * 添加会员领取洗车券
	 * @param washMemberCoupons
	 */
	public void addWashMemberCoupons(WashMemberCoupons washMemberCoupons) {
		this.baseDaoSupport.insert("es_wash_member_coupons", washMemberCoupons);

	}
	
	/**
	 * 查询是否已领取洗车券
	 * @param recomId
	 * @param beRecomId
	 */
	public int countWashMemberCoupons(Integer memberId){
		String sql="SELECT COUNT(1) FROM es_wash_member_coupons a LEFT JOIN es_wash_coupons b ON a.wash_coupons_id=b.wash_coupons_id WHERE b.type=2 AND a.member_id=?";
		return this.baseDaoSupport.queryForInt(sql, memberId);
	}

	public IDictionaryManager getDictionaryManager() {
		return dictionaryManager;
	}

	public void setDictionaryManager(IDictionaryManager dictionaryManager) {
		this.dictionaryManager = dictionaryManager;
	}

}
