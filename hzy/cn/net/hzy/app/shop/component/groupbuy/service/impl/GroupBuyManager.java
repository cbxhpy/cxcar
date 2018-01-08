package cn.net.hzy.app.shop.component.groupbuy.service.impl;

import org.springframework.stereotype.Component;
import cn.net.hzy.app.shop.component.groupbuy.model.GroupBuy;
import cn.net.hzy.app.shop.component.groupbuy.service.IGroupBuyManager;

import com.enation.app.shop.core.model.Goods;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

@Component
public class GroupBuyManager implements IGroupBuyManager {
	
	private IDaoSupport daoSupport;

	@Override
	public int add(GroupBuy groupBuy) {
		groupBuy.setCreate_time(DateUtil.getDateline());
		this.daoSupport.insert("es_group_buy", groupBuy);
		return this.daoSupport.getLastId("es_groupbuy_goods");
	}

	@Override
	public void update(GroupBuy groupBuy) {
		this.daoSupport.update("es_group_buy", groupBuy, "group_id="+groupBuy.getGroup_id());
	}
	
	/**
	 * 批量将商品放入回收站
	 * 
	 * @param ids
	 */
	@Override
	public void delete(Integer[] ids) {
		if (ids == null)
			return;

		String id_str = StringUtil.arrayToString(ids, ",");
		String sql = "delete from  es_group_buy where group_id in ("
				+ id_str + ")";

		this.daoSupport.execute(sql);
	}

	@Override
	public Page search(int page, int pageSize) {
		
		String sql = "select gb.*,g.store,g.buy_count from es_group_buy gb, es_goods g where gb.goods_id=g.goods_id and gb.group_stat=1 and gb.start_time < "
				+DateUtil.getDateline()+" and gb.end_time > "+DateUtil.getDateline()+
				" order by gb.create_time desc";
		
		return this.daoSupport.queryForPage(sql, page, pageSize);
	}

	@Override
	public GroupBuy get(int gbid) {
		String sql ="select * from es_group_buy where group_id=?";
		GroupBuy groupBuy = (GroupBuy)this.daoSupport.queryForObject(sql, GroupBuy.class, gbid);
		
		sql="select * from es_goods where goods_id=?";
		
		Goods goods  = (Goods)this.daoSupport.queryForObject(sql, Goods.class, groupBuy.getGoods_id());
		groupBuy.setGoods(goods);
		
		return groupBuy;
	}

	@Override
	public GroupBuy getBuyGoodsId(int goodsId) {
		String sql="select * from es_group_buy where goods_id=?";
		GroupBuy groupBuy= (GroupBuy)this.daoSupport.queryForObject(sql, GroupBuy.class, goodsId);
		if(groupBuy!=null){
			groupBuy.setGoods((Goods)this.daoSupport.queryForObject("select * from es_goods where goods_id=?", Goods.class, groupBuy.getGoods_id()));
		}
		
		return  groupBuy;
	}

	@Override
	public Page listGroupBuy(int page, int pageSize, Integer groupStat) {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from es_group_buy");
		
		if(groupStat!=null ){
			sql.append(" where group_stat="+groupStat);
		}
		
		sql.append(" order by create_time desc");
		
		return daoSupport.queryForPage(sql.toString(), page, pageSize);
	}

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}

	
}
