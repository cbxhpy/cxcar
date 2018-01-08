package com.enation.app.shop.core.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.base.core.service.auth.IPermissionManager;
import com.enation.app.shop.core.model.Depot;
import com.enation.app.shop.core.model.WarnNum;
import com.enation.app.shop.core.plugin.goods.GoodsStorePluginBundle;
import com.enation.app.shop.core.service.IDepotManager;
import com.enation.app.shop.core.service.IGoodsStoreManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;
@SuppressWarnings({ "unchecked", "rawtypes" })
public class GoodsStoreManager extends BaseSupport implements IGoodsStoreManager {
	
	private GoodsStorePluginBundle goodsStorePluginBundle;
	private IDepotManager depotManager;
	private IAdminUserManager adminUserManager;
	private IPermissionManager permissionManager;
	
	private IProductManager productManager;

	/**
	 * 获取某个货品各个库房的库存
	 */
	@Override
	public List<Map> listProductStore(Integer productid) {
		List<Depot> depotList = depotManager.list();
		List<Map> depotStoreList = new ArrayList<Map>();

		String sql = "select d.*,p.storeid,p.goodsid,p.productid,p.store from "	+ this.getTableName("depot")
				+ " d left join " + this.getTableName("product_store") + " p on d.id=p.depotid where p.productid=?";
		
		List<Map> storeList = this.daoSupport.queryForList(sql, productid);
		for (Depot depot : depotList) {
			Map depotStore = new HashMap();
			depotStore.put("storeid", 0);
			depotStore.put("store", 0); // 如果没有记录为0
			depotStore.put("goodsid", 0);
			depotStore.put("productid", 0);
			if (storeList != null && !storeList.isEmpty()) {
				for (Map store : storeList) {
					int depotid = Integer.parseInt(store.get("id").toString());
					if (depotid == depot.getId().intValue()) { // 找到此仓库的库存
						depotStore.put("storeid", store.get("storeid"));
						depotStore.put("store", store.get("store"));
						depotStore.put("goodsid", store.get("goodsid"));
						depotStore.put("productid", store.get("productid"));
					}
				}
			}
			depotStore.put("name", depot.getName());
			depotStore.put("depotid", depot.getId());

			depotStoreList.add(depotStore);
		}
		return depotStoreList;

	}
	
	/**
	 * 获取某个货品某个库房的库存
	 */
	@Override
	public Integer getbStoreByProId(Integer productid,Integer depotId){
		try {
			return this.daoSupport.queryForInt("select store from es_product_store where productid=? and depotid=?",productid,depotId);
		} catch (Exception e) {
			return 0;
		}
		
	}
	
	@Override
	public List<Map> ListProductDepotStore(Integer productid, Integer finddepotid) {
		List<Depot> depotList = depotManager.list();
		List<Map> depotStoreList = new ArrayList<Map>();

		String sql = "select d.*,p.storeid,p.goodsid,p.productid,p.store from "
				+ this.getTableName("depot") + " d left join " + this.getTableName("product_store")
				+ " p on d.id=p.depotid where p.productid=?";
		List<Map> storeList = this.daoSupport.queryForList(sql, productid);

		for (Depot depot : depotList) {
			if (finddepotid.intValue() != depot.getId())
				continue; // 过滤掉非本仓库的库存

			Map depotStore = new HashMap();
			depotStore.put("storeid", 0); // 如果没有记录为0
			depotStore.put("store", 0); // 如果没有记录为0
			depotStore.put("goodsid", 0);
			depotStore.put("productid", 0);

			if (storeList != null && !storeList.isEmpty()) {
				for (Map store : storeList) {
					int depotid = (Integer) store.get("id");
					if (depotid == depot.getId().intValue()) { // 找到此仓库的库存
						depotStore.put("storeid", store.get("storeid"));
						depotStore.put("store", store.get("store"));
						depotStore.put("goodsid", store.get("goodsid"));
						depotStore.put("productid", store.get("productid"));
					}
				}
			}
			depotStore.put("name", depot.getName());
			depotStore.put("depotid", depot.getId());

			depotStoreList.add(depotStore);
		}
		return depotStoreList;
	}

	@Override
	public List<Map> listProductAllo(Integer orderid, Integer itemid) {
		String sql = "select d.name,a.num from " + this.getTableName("depot")
				+ " d, " + this.getTableName("allocation_item")
				+ " a where a.orderid=? and d.id=a.depotid and a.itemid=?";

		return this.daoSupport.queryForList(sql, orderid, itemid);
	}

	/**
	 * 获取库存维护html
	 */
	@Override
	public String getStoreHtml(Integer goodsid) {
		Map goods = this.getGoods(goodsid);
		return this.goodsStorePluginBundle.getStoreHtml(goods);
	}

	/**
	 * 获取进货html
	 * 
	 * @param goodsid
	 * @return
	 */
	@Override
	public String getStockHtml(Integer goodsid) {
		Map goods = this.getGoods(goodsid);
		return this.goodsStorePluginBundle.getStockHtml(goods);
	}

	/**
	 * 获取报警html
	 * 
	 * @param goodsid
	 * @return
	 */
	@Override
	public String getWarnHtml(Integer goodsid) {
		Map goods = this.getGoods(goodsid);
		return this.goodsStorePluginBundle.getWarnHtml(goods);
	}

	/**
	 * 获取出货html
	 * 
	 * @param goodsid
	 * @return
	 */
	@Override
	public String getShipHtml(Integer goodsid) {
		Map goods = this.getGoods(goodsid);
		return this.goodsStorePluginBundle.getShipHtml(goods);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveStore(int goodsid) {
		Map goods = this.getGoods(goodsid);
		this.goodsStorePluginBundle.onStoreSave(goods);
	}
	
	@Override
	public void saveStoreExcel(String goods_sn, int store, double price) {
		
		Map productMap = this.productManager.getProductBySn(goods_sn);
		
		if(productMap != null){
			if(!StringUtil.isEmpty(StringUtil.isNull(productMap.get("product_id")))){
			
				StringBuffer selec_sql = new StringBuffer();
				selec_sql.append(" select storeid from es_product_store where productid = ? ");
				
				List<Map> storeList = this.baseDaoSupport.queryForList(selec_sql.toString(), productMap.get("product_id").toString());
				
				if(storeList.size() == 0){
					StringBuffer insert_sql = new StringBuffer();
					insert_sql.append(" insert into es_product_store(goodsid, productid, depotid, store, enable_store) values(?, ?, ?, ?, ?) ");
					this.baseDaoSupport.execute(insert_sql.toString(), productMap.get("goods_id").toString(), productMap.get("product_id").toString(), "4", store, store);
				}else{
					StringBuffer update_sql = new StringBuffer();
					update_sql.append(" update es_product_store set store = ?, enable_store = ? where productid = ? ");
					this.baseDaoSupport.execute(update_sql.toString(), store, store, productMap.get("product_id").toString());
				}
				
			
			}
		}
		
	}

	/**
	 * 进货
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveStock(int goodsid) {
		Map goods = this.getGoods(goodsid);
		this.goodsStorePluginBundle.onStockSave(goods);
	}

	/**
	 * 报警
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveWarn(int goodsid) {
		Map goods = this.getGoods(goodsid);
		this.goodsStorePluginBundle.onWarnSave(goods);
	}

	/**
	 * 出货
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveShip(int goodsid) {
		Map goods = this.getGoods(goodsid);
		this.goodsStorePluginBundle.onShipSave(goods);
	}


	private Map getGoods(int goodsid) {
		String sql = "select * from goods  where goods_id=?";
		Map goods = this.baseDaoSupport.queryForMap(sql, goodsid);
		return goods;
	}

	/**
	 * 获取某个货品的报警
	 */
	@Override
	public List<WarnNum> listWarns(Integer goods_id) {
		String sql = "select * from warn_num where  goods_id=?";
		List<WarnNum> list = this.baseDaoSupport.queryForList(sql, WarnNum.class, goods_id);
		List<WarnNum> warnList = new ArrayList<WarnNum>();
		if (list != null && !list.isEmpty()) {
			for (WarnNum warnNum : list) {
				warnList.add(warnNum);
			}
		} else {
			WarnNum warnNum = new WarnNum();
			warnNum.setId(0);
			warnNum.setGoods_id(goods_id);
			warnNum.setWarn_num(0);
			warnList.add(warnNum);
		}
		return warnList;
	}

	@Override
	public List<Map> getDegreeDepotStore(int goodsid, int depotid) {
		String sql = "select p.* from  product_store p where p.goodsid=? and p.depotid=?";
		return this.baseDaoSupport.queryForList(sql, goodsid, depotid);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Page listGoodsStore(Map map, int page, int pageSize, String other,String sort,String order) {
		
		Integer stype = (Integer) map.get("stype");
		String keyword = (String) map.get("keyword");
		String name = (String) map.get("name");
		String sn = (String) map.get("sn");
		
		StringBuffer sql = new StringBuffer();
		sql.append("select g.goods_id,g.sn,g.name,g.store,c.name cname,g.enable_store from es_goods g,es_goods_cat c where g.cat_id=c.cat_id ");
		
		if(stype!=null && keyword!=null){			
			if(stype==0){
				sql.append(" and ( g.name like '%"+keyword.trim()+"%'");
				sql.append(" or g.sn like '%"+keyword.trim()+"%')");
			}
		}
		
		if(!StringUtil.isEmpty(name)){
			sql.append(" and g.name like '%"+name+"%'");
		}
		
		if(!StringUtil.isEmpty(sn)){
			sql.append(" and g.sn like '%"+sn+"%'");
		}
		
		sql.append("order by "+sort+" "+order);
		Page webPage = this.daoSupport.queryForPage(sql.toString(), page, pageSize);
		
		List<Map>goodslist = (List<Map>) webPage.getResult();
		
		StringBuffer goodsidstr = new  StringBuffer();
		for (Map goods : goodslist) {
			Integer goodsid  = (Integer)goods.get("goods_id");
			if(goodsidstr.length()!=0){
				goodsidstr.append(",");
			}
			goodsidstr.append(goodsid);
		}
		
		if(goodsidstr.length()!=0){
			
			String ps_sql ="select ps.* from  es_product_store ps where  ps.goodsid in("+goodsidstr+") ";
			int depotid  = (Integer)map.get("depotid") ;
			if(depotid!=0 ){
				ps_sql=ps_sql+" and depotid="+depotid;
			}
			ps_sql=ps_sql+" order by goodsid,depotid ";
			List<Map> storeList = this.daoSupport.queryForList(ps_sql);
			
			for (Map goods : goodslist) {
				Integer goodsid  = (Integer)goods.get("goods_id");
				if(depotid!=0 ){
					goods.put("d_store", 0);
					goods.put("enable_store", 0);
					for (Map store : storeList) {
						Integer store_goodsid  = (Integer)store.get("goodsid");
						if(store_goodsid.compareTo(goodsid)==0){
							Integer d_store = (Integer.valueOf(goods.get("d_store").toString()))+(Integer.valueOf(store.get("store").toString()));
							Integer enable_store = (Integer.valueOf(goods.get("enable_store").toString()))+(Integer.valueOf(store.get("enable_store").toString()));
							goods.put("d_store", d_store);
							goods.put("enable_store", enable_store);
						}
					}
				}else{
					goods.put("d_store", goods.get("store"));
					goods.put("enable_store", goods.get("enable_store"));
				}
			}
			 
		}
		
		return webPage;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void increaseStroe(int goodsid, int productid, int depotid, int num) {
		
		if(this.checkExists(goodsid, depotid)){
			this.daoSupport.execute("update es_product_store set store =store+?,enable_store=enable_store+? where goodsid=? and depotid=?",num, num,goodsid,depotid);
		}else{
			this.daoSupport.execute("insert into es_product_store(goodsid,productid,depotid,store,enable_store)values(?,?,?,?,?)",goodsid,productid, depotid,num,num);
		}
		
		this.daoSupport.execute("update es_product set  store=store+?,enable_store=enable_store+?  where product_id=?", num, num,productid);
		this.daoSupport.execute("update es_goods set store=store+?,enable_store=enable_store+?  where goods_id=?",num, num,goodsid);
		this.daoSupport.execute("update es_goods set market_enable=?, where goods_id=?", 1,goodsid);				//自动上架
	}
	
	/**
	 * 查询某个仓库的商品库存是否存在
	 * @param goodsid
	 * @param depotid
	 * @return
	 */
	private boolean checkExists(int goodsid,int depotid){
		int count = this.daoSupport.queryForInt("select count(0) from es_product_store where goodsid=? and depotid=?", goodsid,depotid) ;
		return count==0?false:true;
	}
	
	@Override
	public List getStoreList() {
		String sql = "select * from es_depot";
		List list = this.baseDaoSupport.queryForList(sql);
		/*Depot depot = new Depot();
		depot.setName("总库存");
		list.add(0, depot);*/
		return list;
	}
	
	public IProductManager getProductManager() {
		return productManager;
	}
	public void setProductManager(IProductManager productManager) {
		this.productManager = productManager;
	}
	public IPermissionManager getPermissionManager() {
		return permissionManager;
	}

	public void setPermissionManager(IPermissionManager permissionManager) {
		this.permissionManager = permissionManager;
	}	
	public IDepotManager getDepotManager() {
		return depotManager;
	}

	public void setDepotManager(IDepotManager depotManager) {
		this.depotManager = depotManager;
	}

	public GoodsStorePluginBundle getGoodsStorePluginBundle() {
		return goodsStorePluginBundle;
	}

	public void setGoodsStorePluginBundle(GoodsStorePluginBundle goodsStorePluginBundle) {
		this.goodsStorePluginBundle = goodsStorePluginBundle;
	}

	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}

	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}

}
