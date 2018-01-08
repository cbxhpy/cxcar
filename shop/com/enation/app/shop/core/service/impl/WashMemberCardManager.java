package com.enation.app.shop.core.service.impl;

import java.util.List;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.model.WashMemberCard;
import com.enation.app.shop.core.service.IWashMemberCardManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

/**
 * 会员洗车会员卡管理
 * @author yexf
 * 2017-4-16
 */
public class WashMemberCardManager extends BaseSupport<WashMemberCard> implements IWashMemberCardManager {
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int addWashMemberCard(WashMemberCard washMemberCard) {
		this.baseDaoSupport.insert("es_wash_member_card", washMemberCard);
		return this.baseDaoSupport.getLastId("es_wash_member_card");
		
	}
	
	@Override
	public void delWashMemberCard(Integer[] ids) {
		if (ids == null || ids.equals(""))
			return;
		String id_str = StringUtil.arrayToString(ids, ",");
		String sql = "delete from es_wash_member_card where wash_member_card_id in (" + id_str
				+ ")";
		this.baseDaoSupport.execute(sql);
	}

	@Override
	public Page pageWashMemberCard(String order, String sort, int page, int pageSize, String sn, String uname) {
		order = order == null ? " ewmc.wash_member_card_id " : order;
		sort = sort == null ? " desc " : sort;
		StringBuffer sql = new StringBuffer(" select ewmc.*, em.uname from es_wash_member_card ewmc ");
		sql.append(" left join es_member em on em.member_id = ewmc.member_id where 1 = 1 ");
		if(!StringUtil.isEmpty(sn)){
			sql.append(" and ewmc.sn like '%").append(sn).append("%'");
		}
		if(!StringUtil.isEmpty(uname)){
			sql.append(" and em.uname like '%").append(uname).append("%'");	
		}
		
		sql.append(" order by ").append(order).append(" ").append(sort);
		Page rpage = this.daoSupport.queryForPage(sql.toString(), page, pageSize, WashMemberCard.class);
		return rpage;
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
	public List<WashMemberCard> getMemberCardList(String member_id) {

		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT * FROM es_wash_member_card where member_id = ? ");
		
		List<WashMemberCard> list = this.baseDaoSupport.queryForList(sql.toString(), WashMemberCard.class, member_id);
		
		return list;
	}

	@Override
	public WashMemberCard getWashMemberCard(String wash_member_card_id) {
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT ewmc.*, em.uname FROM es_wash_member_card ewmc ");
		sql.append(" left join es_member em on em.member_id = ewmc.member_id ");
		sql.append(" where wash_member_card_id = ? ");
		
		List<WashMemberCard> list = this.baseDaoSupport.queryForList(sql.toString(), WashMemberCard.class, wash_member_card_id);
		
		WashMemberCard washMemberCard = null;
		if(list != null && list.size() != 0){
			washMemberCard = list.get(0);
		}
		
		return washMemberCard;
	}

	@Override
	public WashMemberCard getWashMemberCardBySn(String sn) {
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT ewmc.*, em.uname FROM es_wash_member_card ewmc ");
		sql.append(" left join es_member em on em.member_id = ewmc.member_id ");
		sql.append(" where sn = ? ");
		
		List<WashMemberCard> list = this.baseDaoSupport.queryForList(sql.toString(), WashMemberCard.class, sn);
		
		WashMemberCard washMemberCard = null;
		if(list != null && list.size() != 0){
			washMemberCard = list.get(0);
		}
		
		return washMemberCard;
	}

	@Override
	public void updateWashMemberCard(WashMemberCard wmc) {
		this.baseDaoSupport.update("es_wash_member_card", wmc, " wash_member_card_id = " + wmc.getWash_member_card_id());
	}


}
