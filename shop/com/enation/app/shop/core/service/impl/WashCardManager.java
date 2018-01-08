package com.enation.app.shop.core.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enation.app.base.core.model.AdvMapper;
import com.enation.app.shop.core.model.CarMachine;
import com.enation.app.shop.core.model.PushMsg;
import com.enation.app.shop.core.model.WashCard;
import com.enation.app.shop.core.service.IWashCardManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

/**
 * 洗车会员卡管理
 * @author yexf
 * 2017-4-12
 */
public class WashCardManager extends BaseSupport<WashCard> implements IWashCardManager {

	@Override
	public void addWashCard(WashCard washCard) {
		this.baseDaoSupport.insert("es_wash_card", washCard);
	}
	
	@Override
	public void delWashCards(Integer[] ids) {
		if (ids == null || ids.equals(""))
			return;
		String id_str = StringUtil.arrayToString(ids, ",");
		String sql = "delete from es_wash_card where wash_card_id in (" + id_str
				+ ")";
		this.baseDaoSupport.execute(sql);
	}

	@Override
	public List<WashCard> getWashCardPage() {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT * FROM es_wash_card ORDER BY wash_card_id DESC ");
		List<WashCard> list = this.baseDaoSupport.queryForList(sql.toString(), WashCard.class);
		return list;
	}

	@Override
	public WashCard getWashCard(String sign_id) {
		StringBuffer sql = new StringBuffer();
		
		sql.append(" select * from es_wash_card where wash_card_id = ? ");
		
		List<WashCard> list = this.baseDaoSupport.queryForList(sql.toString(), WashCard.class, sign_id);
		
		if(list.size() != 0){
			return list.get(0);
		}else{
			return null;
		}
	}

	@Override
	public Page pageWashCard(String order, String sort, int pageNo, int pageSize) {
		order = order == null ? " ewc.wash_card_id " : order;
		sort = sort == null ? " desc " : sort;
		StringBuffer sql = new StringBuffer(" select * from es_wash_card ewc ");
		sql.append(" order by ").append(order).append(" ").append(sort); 
		Page rpage = this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize, WashCard.class);
		return rpage;
	}

	@Override
	public void updateWashCard(WashCard washCard) {
		this.baseDaoSupport.update("es_wash_card", washCard, " wash_card_id = " + washCard.getWash_card_id());
	}


}
