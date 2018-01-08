package com.enation.app.shop.core.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Adv;
import com.enation.app.base.core.service.IAdvManager;
import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.shop.core.model.Cat;
import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.model.GoodsStores;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.model.SpecValue;
import com.enation.app.shop.core.model.Tag;
import com.enation.app.shop.core.model.support.GoodsEditDTO;
import com.enation.app.shop.core.plugin.goods.GoodsDataFilterBundle;
import com.enation.app.shop.core.plugin.goods.GoodsPluginBundle;
import com.enation.app.shop.core.service.IDepotMonitorManager;
import com.enation.app.shop.core.service.IGoodsCatManager;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IGoodsStoreManager;
import com.enation.app.shop.core.service.IMemberLvManager;
import com.enation.app.shop.core.service.IMemberPriceManager;
import com.enation.app.shop.core.service.IPackageProductManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.app.shop.core.service.ITagManager;
import com.enation.app.shop.core.service.SnDuplicateException;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.StringUtil;

/**
 * Goods业务管理
 * 
 * @author kingapex 2010-1-13下午12:07:07
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class GoodsManager extends BaseSupport implements IGoodsManager {
	
	private SimpleJdbcTemplate simpleJdbcTemplate;
	
	private ITagManager tagManager;
	private GoodsPluginBundle goodsPluginBundle;
	private IPackageProductManager packageProductManager;
	private IGoodsCatManager goodsCatManager;
	private IMemberPriceManager memberPriceManager;
	private IMemberLvManager memberLvManager;
	private IDepotMonitorManager depotMonitorManager;
	private GoodsDataFilterBundle goodsDataFilterBundle;
	
	private IProductManager productManager; 
	private IGoodsStoreManager goodsStoreManager;
	private IGoodsManager goodsManager;
	private IAdvManager advManager;
	
	private IAdminUserManager adminUserManager;
	
	// private IDaoSupport<Goods> daoSupport;
	
	/**
	 * 添加商品，同时激发各种事件
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void add(Goods goods) {
		try {
			Map goodsMap = po2Map(goods);
			
			// 触发商品添加前事件
			goodsPluginBundle.onBeforeAdd(goodsMap);
			goodsMap.put("disabled", 0);
			goodsMap.put("create_time", DateUtil.getDatelineLong());
			goodsMap.put("view_count", 0);
			goodsMap.put("buy_count", 0);
			goodsMap.put("last_modify", DateUtil.getDatelineLong());
			this.baseDaoSupport.insert("goods", goodsMap);
			Integer goods_id = this.baseDaoSupport.getLastId("goods");
			goods.setGoods_id(goods_id);
			goodsMap.put("goods_id", goods_id);
			goodsPluginBundle.onAfterAdd(goodsMap);
		} catch (RuntimeException e) {
			if (e instanceof SnDuplicateException) {
				throw e;
			}
			e.printStackTrace();
		}
	}

	/**
	 * 修改商品同时激发各种事件
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void edit(Goods goods) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("开始保存商品数据...");
			}
			Map goodsMap = this.po2Map(goods);
			this.goodsPluginBundle.onBeforeEdit(goodsMap);
			this.baseDaoSupport.update("goods", goodsMap,
					"goods_id=" + goods.getGoods_id());
			this.goodsPluginBundle.onAfterEdit(goodsMap);
			if (logger.isDebugEnabled()) {
				logger.debug("保存商品数据完成.");
			}
		} catch (RuntimeException e) {
			if (e instanceof SnDuplicateException) {
				throw e;
			}
			e.printStackTrace();
		}
	}

	/**
	 * 得到修改商品时的数据
	 * 
	 * @param goods_id
	 * @return
	 */
	@Override
	public GoodsEditDTO getGoodsEditData(Integer goods_id) {
		GoodsEditDTO editDTO = new GoodsEditDTO();
		StringBuffer sql = new StringBuffer(" select eg.*, es.seller_name from es_goods eg ");
		sql.append(" left join es_seller es on es.seller_id = eg.seller_id ");
		sql.append(" where goods_id = ? ");
		
		Map goods = this.baseDaoSupport.queryForMap(sql.toString(), goods_id);

		String intro = (String) goods.get("intro");
		if (intro != null) {
			intro = UploadUtil.replacePath(intro);
			goods.put("intro", intro);
		}

		Map<Integer, String> htmlMap = goodsPluginBundle.onFillEditInputData(goods);

		editDTO.setGoods(goods);
		editDTO.setHtmlMap(htmlMap);

		return editDTO;
	}

	/**
	 * 读取一个商品的详细<br/>
	 * 处理由库中读取的默认图片和所有图片路径:<br>
	 * 如果是以本地文件形式存储，则将前缀替换为静态资源服务器地址。
	 */
	@Override
	public Map get(Integer goods_id) {
		String sql = "select g.*,b.name as brand_name from "
				+ this.getTableName("goods") + " g left join "
				+ this.getTableName("brand") + " b on g.brand_id=b.brand_id ";
		sql += "  where goods_id=?";
 
		Map goods = this.daoSupport.queryForMap(sql, goods_id);

		/**
		 * ====================== 对商品图片的处理 ======================
		 */
 
		String small = (String) goods.get("small");
		if (small != null) {
			small = UploadUtil.replacePath(small);
			goods.put("small", small);
		}
		String big = (String) goods.get("big");
		if (big != null) {
			big = UploadUtil.replacePath(big);
			goods.put("big", big);
		}
		 
 
		return goods;
	}

	@Override
	public void getNavdata(Map goods) {
		// lzf 2011-08-29 add,lzy modified 2011-10-04
		int catid = (Integer) goods.get("cat_id");
		List list = goodsCatManager.getNavpath(catid);
		goods.put("navdata", list);
		// lzf add end
	}	

	private String getListSql(int disabled) {
		String selectSql = this.goodsPluginBundle.onGetSelector();
		String fromSql = this.goodsPluginBundle.onGetFrom();

		String sql = "select g.*, b.name as brand_name, t.name as type_name, c.name as cat_name, s.seller_name "
				+ selectSql
				+ " from "
				+ this.getTableName("goods")
				+ " g left join "
				+ this.getTableName("goods_cat")
				+ " c on g.cat_id = c.cat_id left join "
				+ this.getTableName("seller")
				+ " s on g.seller_id = s.seller_id left join "
				+ this.getTableName("brand")
				+ " b on g.brand_id = b.brand_id and b.disabled=0 left join "
				+ this.getTableName("goods_type")
				+ " t on g.type_id = t.type_id "
				+ fromSql
				+ " where g.goods_type = 'normal' and g.disabled=" + disabled;
	
		return sql;
	}

	/**
	 * 取得捆绑商品列表
	 * @param disabled
	 * @return
	 */
	private String getBindListSql(int disabled) {
		String sql = "select g.*,b.name as brand_name ,t.name as type_name,c.name as cat_name from "
				+ this.getTableName("goods")
				+ " g left join "
				+ this.getTableName("goods_cat")
				+ " c on g.cat_id=c.cat_id left join "
				+ this.getTableName("brand")
				+ " b on g.brand_id = b.brand_id left join "
				+ this.getTableName("goods_type")
				+ " t on g.type_id =t.type_id"
				+ " where g.goods_type = 'bind' and g.disabled=" + disabled;
		return sql;
	}	

	/**
	 * 后台搜索商品
	 * @param params 通过map的方式传递搜索参数
	 * @param page
	 * @param pageSize
	 * @return
	 */
	@Override
	public Page searchBindGoods(String name, String sn, String order, int page,
			int pageSize) {

		String sql = getBindListSql(0);

		if (order == null) {
			order = "goods_id desc";
		}

		if (name != null && !name.equals("")) {
			sql += "  and g.name like '%" + name + "%'";
		}

		if (sn != null && !sn.equals("")) {
			sql += "   and g.sn = '" + sn + "'";
		}

		sql += " order by g." + order;
		Page webpage = this.daoSupport.queryForPage(sql, page, pageSize);

		List<Map> list = (List<Map>) (webpage.getResult());

		for (Map map : list) {
			List productList = packageProductManager.list(Integer.valueOf(map
					.get("goods_id").toString()));
			productList = productList == null ? new ArrayList() : productList;
			map.put("productList", productList);
		}

		return webpage;
	}

	/**
	 * 读取商品回收站列表
	 * @param name
	 * @param sn
	 * @param order
	 * @param page
	 * @param pageSize
	 * @return
	 */
	@Override
	public Page pageTrash(String name, String sn, String order, int page,
			int pageSize) {

		String sql = getListSql(1);
		if (order == null) {
			order = "goods_id desc";
		}

		if (name != null && !name.equals("")) {
			sql += "  and g.name like '%" + name + "%'";
		}

		if (sn != null && !sn.equals("")) {
			sql += "   and g.sn = '" + sn + "'";
		}

		sql += " order by g." + order;

		Page webpage = this.daoSupport.queryForPage(sql, page, pageSize);

		return webpage;
	}
	
	/***
	 * 库存余量提醒分页列表
	 * @param warnTotal 总报警数
	 * @param page
	 * @param pageSize
	 */
	@Override
	public List<GoodsStores> storeWarnGoods(int warnTotal, int page, int pageSize) {
		// String sql =
		// " where g.market_enable = 1 and g.goods_type = 'normal' and g.disabled= 0 order by g.goods_id desc ";
		String select_sql = "select gc.name as gc_name,b.name as b_name,g.cat_id,g.goods_id,g.name,g.sn,g.price,g.last_modify,g.market_enable,s.sumstore ";
		String left_sql = " left join " + this.getTableName("goods") + " g  on s.goodsid = g.goods_id  left join " + this.getTableName("goods_cat") + " gc on gc.cat_id = g.cat_id left join " + this.getTableName("brand") + " b on b.brand_id = g.brand_id ";
		List<GoodsStores> list = new ArrayList<GoodsStores>();

		String sql_2 = select_sql
				+ " from  (select ss.* from (select goodsid,productid,sum(store) sumstore from " + this.getTableName("product_store") + "  group by goodsid,productid   ) ss "+
				"  left join " + this.getTableName("warn_num") + " wn on wn.goods_id = ss.goodsid  where ss.sumstore <=  (case when (wn.warn_num is not null or wn.warn_num <> 0) then wn.warn_num else ?  end )  ) s  "
				+ left_sql;
		List<GoodsStores> list_2 = this.daoSupport.queryForList(sql_2, new RowMapper() {
					@Override
					public Object mapRow(ResultSet rs, int arg1)
							throws SQLException {
						GoodsStores gs = new GoodsStores();
						gs.setGoods_id(rs.getInt("goods_id"));
						gs.setName(rs.getString("name"));
						gs.setSn(rs.getString("sn"));
						gs.setRealstore(rs.getInt("sumstore"));
						gs.setPrice(rs.getDouble("price"));
						gs.setLast_modify(rs.getLong("last_modify"));
						gs.setBrandname(rs.getString("b_name"));
						gs.setCatname(rs.getString("gc_name"));
						gs.setMarket_enable(rs.getInt("market_enable"));
						gs.setCat_id(rs.getInt("cat_id"));
						return gs;
					}
				}, warnTotal);
		list.addAll(list_2);// 普通商品		

		return list;
	}

	/**
	 * 批量将商品放入回收站
	 * @param ids
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Integer[] ids) {
		if (ids == null)
			return;

		for (Integer id : ids) {
			this.tagManager.saveRels(id, null);
		}
		String id_str = StringUtil.arrayToString(ids, ",");
		String sql = "update  goods set disabled=1  where goods_id in ("
				+ id_str + ")";

		this.baseDaoSupport.execute(sql);
	}

	/**
	 * 还原
	 * @param ids
	 */
	@Override
	public void revert(Integer[] ids) {
		if (ids == null)
			return;
		String id_str = StringUtil.arrayToString(ids, ",");
		String sql = "update  goods set disabled=0  where goods_id in ("
				+ id_str + ")";
		this.baseDaoSupport.execute(sql);
	}

	/**
	 * 清除
	 * @param ids
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void clean(Integer[] ids) {
		if (ids == null)
			return;
		for (Integer id : ids) {
			this.tagManager.saveRels(id, null);
		}
		this.goodsPluginBundle.onGoodsDelete(ids);
		String id_str = StringUtil.arrayToString(ids, ",");
		String sql = "delete  from goods  where goods_id in (" + id_str + ")";
		this.baseDaoSupport.execute(sql);
	}

	@Override
	public List list(Integer[] ids) {
		if (ids == null || ids.length == 0)
			return new ArrayList();
		String idstr = StringUtil.arrayToString(ids, ",");
		String sql = "select * from goods where goods_id in(" + idstr + ")";
		return this.baseDaoSupport.queryForList(sql);
	}

	public GoodsPluginBundle getGoodsPluginBundle() {
		return goodsPluginBundle;
	}

	public void setGoodsPluginBundle(GoodsPluginBundle goodsPluginBundle) {
		this.goodsPluginBundle = goodsPluginBundle;
	}

	/**
	 * 将po对象中有属性和值转换成map
	 * 
	 * @param po
	 * @return
	 */
	protected Map po2Map(Object po) {
		Map poMap = new HashMap();
		Map map = new HashMap();
		try {
			map = BeanUtils.describe(po);
		} catch (Exception ex) {
		}
		Object[] keyArray = map.keySet().toArray();
		for (int i = 0; i < keyArray.length; i++) {
			String str = keyArray[i].toString();
			if (str != null && !str.equals("class")) {
				if (map.get(str) != null) {
					poMap.put(str, map.get(str));
				}
			}
		}
		return poMap;
	}

	public IPackageProductManager getPackageProductManager() {
		return packageProductManager;
	}

	public void setPackageProductManager(
			IPackageProductManager packageProductManager) {
		this.packageProductManager = packageProductManager;
	}

	@Override
	public Goods getGoods(Integer goods_id) {
		
		StringBuffer sql = new StringBuffer();
		sql.append(" select * from goods where goods_id = ? ");
		
		Goods goods = (Goods) this.baseDaoSupport.queryForObject(sql.toString(), Goods.class, goods_id);
		return goods;
	}
	
	@Override
	public Goods getGoodsByServeId(String serve_id){
		StringBuffer sql = new StringBuffer();
		sql.append(" select eg.*, es.seller_name, es.region from es_goods eg ");
		sql.append(" left join es_seller es on es.seller_id = eg.seller_id ");
		sql.append(" where eg.goods_id = ? ");
		
		Goods goods = (Goods) this.baseDaoSupport.queryForObject(sql.toString(), Goods.class, serve_id);
		
		return goods;
	}

	public IGoodsCatManager getGoodsCatManager() {
		return goodsCatManager;
	}

	public void setGoodsCatManager(IGoodsCatManager goodsCatManager) {
		this.goodsCatManager = goodsCatManager;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void batchEdit() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String[] ids = request.getParameterValues("goodsidArray");
		String[] names = request.getParameterValues("name");
		String[] prices = request.getParameterValues("price");
		String[] cats = request.getParameterValues("catidArray");
		String[] market_enable = request.getParameterValues("market_enables");
		String[] store = request.getParameterValues("store");
		String[] sord = request.getParameterValues("sord");

		String sql = "";

		for (int i = 0; i < ids.length; i++) {
			sql = "";
			if (names != null && names.length > 0) {
				if (!StringUtil.isEmpty(names[i])) {
					if (!sql.equals(""))
						sql += ",";
					sql += " name='" + names[i] + "'";
				}
			}

			if (prices != null && prices.length > 0) {
				if (!StringUtil.isEmpty(prices[i])) {
					if (!sql.equals(""))
						sql += ",";
					sql += " price=" + prices[i];
				}
			}
			if (cats != null && cats.length > 0) {
				if (!StringUtil.isEmpty(cats[i])) {
					if (!sql.equals(""))
						sql += ",";
					sql += " cat_id=" + cats[i];
				}
			}
			if (store != null && store.length > 0) {
				if (!StringUtil.isEmpty(store[i])) {
					if (!sql.equals(""))
						sql += ",";
					sql += " store=" + store[i];
				}
			}
			if (market_enable != null && market_enable.length > 0) {
				if (!StringUtil.isEmpty(market_enable[i])) {
					if (!sql.equals(""))
						sql += ",";
					sql += " market_enable=" + market_enable[i];
				}
			}
			if (sord != null && sord.length > 0) {
				if (!StringUtil.isEmpty(sord[i])) {
					if (!sql.equals(""))
						sql += ",";
					sql += " sord=" + sord[i];
				}
			}
			sql = "update  goods set " + sql + " where goods_id=?";
			this.baseDaoSupport.execute(sql, ids[i]);

		}
	}

	@Override
	public Map census() {
		// 计算上架商品总数
		String sql = "select count(0) from goods ";
		int allcount = this.baseDaoSupport.queryForInt(sql);
				
		// 计算上架商品总数
		sql = "select count(0) from goods where market_enable=1 and  disabled = 0";
		int salecount = this.baseDaoSupport.queryForInt(sql);

		// 计算下架商品总数
		sql = "select count(0) from goods where market_enable=0 and  disabled = 0";
		int unsalecount = this.baseDaoSupport.queryForInt(sql);

		// 计算回收站总数
		sql = "select count(0) from goods where   disabled = 1";
		int disabledcount = this.baseDaoSupport.queryForInt(sql);

		// 读取商品评论数
		sql = "select count(0) from comments where   for_comment_id is null  and commenttype='goods' and object_type='discuss'";
		int discusscount = this.baseDaoSupport.queryForInt(sql);

		// 读取商品评论数
		sql = "select count(0) from comments where for_comment_id is null  and  commenttype='goods' and object_type='ask'";
		int askcount = this.baseDaoSupport.queryForInt(sql);

		Map<String, Integer> map = new HashMap<String, Integer>(2);
		map.put("salecount", salecount);
		map.put("unsalecount", unsalecount);
		map.put("disabledcount", disabledcount);
		map.put("allcount", allcount);
		map.put("discuss", discusscount);
		map.put("ask", askcount);
		return map;
	}
 
	/**
	 * 获取某个分类的推荐商品
	 */
	@Override
	public List getRecommentList(int goods_id, int cat_id, int brand_id, int num) {
		 //原美睛网代码，去掉
		return null;
	}

	@Override
	public List list() {
		String sql = "select * from goods where disabled = 0";
		return this.baseDaoSupport.queryForList(sql);
	}

	

	@Override
	public void updateField(String filedname, Object value, Integer goodsid) {
		this.baseDaoSupport.execute("update goods set " + filedname + "=? where goods_id=?", value, goodsid);
	}

	@Override
	public Goods getGoodBySn(String goodSn) {
		Goods goods = (Goods) this.baseDaoSupport.queryForObject("select * from goods where sn=?", Goods.class, goodSn);
		return goods;
	}

	@Override
	public Page listByCatid(String cat_id, String page_no, String page_size) {
		
		StringBuffer sql = new StringBuffer();
		
		sql.append(" select eg.goods_id, eg.name from es_goods eg where eg.market_enable = 1 and eg.disabled = 0 ");
		
		if(!StringUtil.isEmpty(cat_id)){
			Cat cat = this.goodsCatManager.getById(Integer.parseInt(cat_id));
			sql.append(" and eg.cat_id in ( ");
			sql.append(" select c.cat_id from ").append(this.getTableName("goods_cat"));
			sql.append(" c where c.cat_path like '").append(cat.getCat_path()).append("%') ");
		}
		
		sql.append(" order by eg.goods_id desc ");
		
		Page goodsPage = this.baseDaoSupport.queryForPage(sql.toString(), Integer.parseInt(page_no), Integer.parseInt(page_size));
		
		return goodsPage;
	}
	
	@Override
	public Page listByAdvidOrSearch(String adv_id, String search_code, String page_no, String page_size) {
		
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT eg.goods_id, ep.product_id, ep.name, ep.price, eg.mktprice original_price, eg.thumbnail image, ep.enable_store, eg.have_spec, etr.tag_id FROM es_product ep LEFT JOIN es_goods eg ON eg.goods_id = ep.goods_id ");
		sql.append(" LEFT JOIN es_tag_rel etr ON etr.rel_id = eg.goods_id ");
		sql.append(" WHERE eg.market_enable = 1 AND eg.disabled = 0 ");
		
		if(!StringUtil.isEmpty(adv_id)){
			Adv adv = this.advManager.getAdvDetail(Long.parseLong(adv_id));
			if(adv != null){
				Tag tag = this.tagManager.getByName(adv.getCompany());
				if(tag != null){
					sql.append(" AND tag_id = ").append(tag.getTag_id());
				}
			}
		}
		
		if(!StringUtil.isEmpty(search_code)){
			sql.append(" AND ep.name LIKE '%");
			sql.append(search_code);
			sql.append("%' ");
		}
		
		sql.append(" order by eg.goods_id desc ");
		
		Page goodsPage = this.baseDaoSupport.queryForPage(sql.toString(), Integer.parseInt(page_no), Integer.parseInt(page_size));
		
		return goodsPage;
	}
	
	@Override
	public List listByCat(Integer catid) {
		String sql = getListSql(0);
		if (catid.intValue() != 0) {
			Cat cat = this.goodsCatManager.getById(catid);
			sql += " and g.cat_id in(";
			sql += " select c.cat_id from " + this.getTableName("goods_cat")
					+ " c where c.cat_path like '" + cat.getCat_path()
					+ "%') and  ";
		}
		return this.daoSupport.queryForList(sql);
	}

	@Override
	public List listByCatWbl(Integer catid) {
		String sql = getListSql(0);
		if (catid.intValue() != 0) {
			Cat cat = this.goodsCatManager.getById(catid);
			sql += " and g.cat_id in(";
			sql += " select c.cat_id from " + this.getTableName("goods_cat")
					+ " c where c.cat_path like '" + cat.getCat_path()
					+ "%') and g.market_enable = 1 and g.disabled = 0 ";
		}
		return this.daoSupport.queryForList(sql);
	}
	
	@Override
	public List<Map> listBySellerId(String seller_id) {
		String sql = getListSql(0);
		sql += " and g.seller_id = " + seller_id;
		sql += " and g.market_enable = 1 and g.disabled = 0 ";
		return this.daoSupport.queryForList(sql);
	}
	
	@Override
	public List listByTag(Integer[] tagid) {
		String sql = getListSql(0);
		if (tagid != null && tagid.length > 0) {
			String tagidstr = StringUtil.arrayToString(tagid, ",");
			sql += " and g.goods_id in(select rel_id from "
					+ this.getTableName("tag_rel") + " where tag_id in("
					+ tagidstr + "))";
		}
		return this.daoSupport.queryForList(sql);
	}

	@Override
	public void incViewCount(Integer goods_id) {
		this.baseDaoSupport.execute("update goods set view_count = view_count + 1 where goods_id = ?", goods_id);
	}
	
	@Override
	public List listGoods(String catid,String tagid,String goodsnum){
		int num = 10;
		if(!StringUtil.isEmpty(goodsnum)){
			num = Integer.valueOf(goodsnum);
		}
		
		StringBuffer sql = new StringBuffer();
		sql.append("select g.* from " + this.getTableName("tag_rel") + " r LEFT JOIN " + this.getTableName("goods") + " g ON g.goods_id=r.rel_id where g.disabled=0 and g.market_enable=1");
		
		if(! StringUtil.isEmpty(catid) ){
			Cat cat  = this.goodsCatManager.getById(Integer.valueOf(catid));
			if(cat!=null){
				String cat_path  = cat.getCat_path();
				if (cat_path != null) {
					sql.append( " and  g.cat_id in(" ) ;
					sql.append("select c.cat_id from " + this.getTableName("goods_cat") + " ");
					sql.append(" c where c.cat_path like '" + cat_path + "%')");
				}
			}
		}
		
		if(!StringUtil.isEmpty(tagid)){
			sql.append(" AND r.tag_id="+tagid+"");
		}
		
		sql.append(" order by r.ordernum desc");
		//System.out.println(sql.toString());
		List list = this.daoSupport.queryForListPage(sql.toString(), 1,num);
		this.goodsDataFilterBundle.filterGoodsData(list);
		return list;
	}

	@Override
	public List goodsBuyer(int goods_id, int pageSize) {
		String sql = "select distinct m.* from es_order o left join es_member m " +
				"on o.member_id=m.member_id where order_id in (select order_id from es_order_items " +
				"where goods_id=?)";
		Page page = this.daoSupport.queryForPage(sql, 1, pageSize, goods_id);
		
		return (List)page.getResult();
	}
	
	@Override
	public Page searchGoods(Map goodsMap, int page, int pageSize, String other,String sort,String order) {
		String sql = creatTempSql(goodsMap, other);
		StringBuffer _sql = new StringBuffer(sql);
		this.goodsPluginBundle.onSearchFilter(_sql);
		_sql.append(" order by "+sort+" "+order);
		Page webpage = this.daoSupport.queryForPage(_sql.toString(), page,pageSize);
		return webpage;
	}

	@Override
	public List searchGoods(Map goodsMap) {
		String sql = creatTempSql(goodsMap, null);
		return this.daoSupport.queryForList(sql,Goods.class);
	}
	
	private String creatTempSql(Map goodsMap,String other){
		
		other = other==null?"":other;
		String sql = getListSql(0);
		Integer brandid = (Integer) goodsMap.get("brandid");
		Integer catid = (Integer) goodsMap.get("catid");
		String name = (String) goodsMap.get("name");
		String sn = (String) goodsMap.get("sn");
		Integer[]tagid = (Integer[]) goodsMap.get("tagid");
		Integer stype = (Integer) goodsMap.get("stype");
		String keyword = (String) goodsMap.get("keyword");
		String order = (String) goodsMap.get("order");
		
		AdminUser adminUser = this.adminUserManager.getCurrentUser();
		if(adminUser.getFounder() != 1){
			sql += " and g.seller_id in (select seller_id from es_seller where user_id = " + adminUser.getUserid() + " )";
		}
		
		if (brandid != null && brandid != 0) {
			sql += " and g.brand_id = " + brandid + " ";
		}
		
		if("1".equals(other)){
			//商品属性为不支持打折的商品
			sql += " and g.no_discount=1";
		}
		if("2".equals(other)){
			//特殊打折商品，即单独设置了会员价的商品
			sql += " and (select count(0) from " + this.getTableName("goods_lv_price") + " glp where glp.goodsid=g.goods_id) >0";
		}
		
		if(stype!=null && keyword!=null){			
			if(stype==0){
				sql+=" and ( g.name like '%"+keyword+"%'";
				sql+=" or g.sn like '%"+keyword+"%')";
			}
		}
		
		if (name != null && !name.equals("")) {
			name = name.trim();
			String[] keys = name.split("\\s");
			for (String key : keys) {
				sql += (" and g.name like '%");
				sql += (key);
				sql += ("%'");
			}
		}

		if (sn != null && !sn.equals("")) {
			sql += "   and g.sn like '%" + sn + "%'";
		}


		if (catid != null && catid!=0) {
			Cat cat = this.goodsCatManager.getById(catid);
			sql += " and  g.cat_id in(";
			sql += "select c.cat_id from " + this.getTableName("goods_cat")
					+ " c where c.cat_path like '" + cat.getCat_path()
					+ "%')  ";
		}

		if (tagid != null && tagid.length > 0) {
			String tagidstr = StringUtil.arrayToString(tagid, ",");
			sql += " and g.goods_id in(select rel_id from "
					+ this.getTableName("tag_rel") + " where tag_id in("
					+ tagidstr + "))";
		}
		//System.out.println(sql);
		return sql;
	}

	@Override
	public boolean isHave(int goods_id) {
		
		String sql = "SELECT count(goods_id) FROM es_goods WHERE goods_id = ?";
		
		int result = this.daoSupport.queryForInt(sql, goods_id);
		return result > 0 ? true : false;
	}

	@Override
	public List<Map> getGoodsByNavigation(String is_navigation) {

		StringBuffer sql = new StringBuffer();
		sql.append(" select eg.goods_id, eg.name, eg.thumbnail from es_goods eg ");
		sql.append(" where eg.market_enable = 1 and eg.disabled = 0 and eg.is_navigation = ? ");
		sql.append(" order by eg.sort desc, eg.goods_id desc ");
		
		List<Map> goodsList = this.baseDaoSupport.queryForList(sql.toString(), is_navigation);
		
		return goodsList;
	}
	
	@Override
	public void setGoodsPriceExcel(String goods_sn, int store, double price){

		StringBuffer goods_sql = new StringBuffer();
		goods_sql.append(" update es_goods set price = ?, store = ?, enable_store = ? where sn = ? ");
		
		StringBuffer product_sql = new StringBuffer();
		product_sql.append(" update es_product set price = ?, store = ?, enable_store = ? where sn = ? ");
		
		this.baseDaoSupport.execute(goods_sql.toString(), price, store, store, goods_sn);
		this.baseDaoSupport.execute(product_sql.toString(), price, store, store, goods_sn);
		
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void setSpecStore() {
		
		HttpServletRequest httpRequest = ThreadContextHolder.getHttpRequest();
		
		String goods_id = httpRequest.getParameter("goods_id");
		
		Goods goods = this.getGoods(Integer.parseInt(goods_id));
		
		if(goods.getHave_spec() == 1){
		
			String[] specidsAr = httpRequest.getParameterValues("specids"); //规格id数组
			String[] specvidsAr = httpRequest.getParameterValues("specvids");//规格值id数组
			
			String[] productids = httpRequest.getParameterValues("productids"); //货品id数组
			String[] sns = httpRequest.getParameterValues("sns");
			String[] prices = httpRequest.getParameterValues("prices");
			String[] costs = httpRequest.getParameterValues("costs");
		
			String[] weights = httpRequest.getParameterValues("weights");
			
			String[] introduces = httpRequest.getParameterValues("introduces");//产品简介add by yexf 20161107
			
			List<Product> productList = new ArrayList<Product>();
			
			int i = 0;
			int snIndex = this.getSnsSize(sns);
			for (String sn : sns) {
				Integer productId = StringUtil.isEmpty(productids[i]) ? null : Integer.valueOf(productids[i]);
				if (sn == null || sn.equals("")) {
					sn = goods.getSn() + "-" + (snIndex + 1);
					snIndex++;
				}
			
				/*
				 * 组合商品、货品、规格值、规格对应关系
				 */
				List<SpecValue> valueList = new ArrayList<SpecValue>();
				int j = 0;
				String[] specids = specidsAr[i].split(","); // 此货品的规格
				String[] specvids = specvidsAr[i].split(","); // 此货品的规格值
				
				//此货品的规格值list
				for (String specid : specids) {
					SpecValue specvalue = new SpecValue();
					specvalue.setSpec_value_id(Integer.valueOf(specvids[j].trim()));
					specvalue.setSpec_id(Integer.valueOf(specid.trim()));
					valueList.add(specvalue);
					j++;
				}
				
				// 生成货品对象
				Product product = new Product();
				product.setGoods_id(Integer.parseInt(goods_id));
				product.setSpecList(valueList);// 设置此货品的规格list
				product.setName(goods.getName());
				product.setSn(sn);
				product.setProduct_id(productId); // 2010-1-12新增写入货品id，如果是新货品，则会是null
	
				String[] specvalues = httpRequest.getParameterValues("specvalue_" + i);
				product.setSpecs(StringUtil.arrayToString(specvalues, "、"));
				// 价格
				if (null == prices[i] || "".equals(prices[i])){
					product.setPrice(0D);
				}else{
					product.setPrice(Double.valueOf(prices[i]));
				}
				/*if (!"yes".equals(httpRequest.getParameter("isedit"))) { // 添加时默认为0，修改时不处理
					product.setStore(0);
				}*/
				
				// 成本价
				if (null == costs[i] || "".equals(costs[i])){
					product.setCost(0D);
				}else{
					product.setCost(Double.valueOf(costs[i]));
				}
				// 重量
				if (null == weights[i] || "".equals(weights[i])){
					product.setWeight(0D);
				}else{
					product.setWeight(Double.valueOf(weights[i]));
				}
				// 产品简介
				if (StringUtil.isEmpty(introduces[i])){
					product.setIntroduce("");
				}else{
					product.setIntroduce(introduces[i]);
				}
				
				// 20101123新增会员价格相应逻辑
				// 规格为：name为加_index
				//String[] lvPriceStr = httpRequest.getParameterValues("lvPrice_" + i);
				//String[] lvidStr = httpRequest.getParameterValues("lvid_" + i);
				
				// 生成会员价list
				//if (lvidStr != null && lvidStr.length > 0) { // lzf add line 20110114
				//	List<GoodsLvPrice> goodsLvPrices = this.createGoodsLvPrices(lvPriceStr, lvidStr, Integer.parseInt(goods_id));
				//	product.setGoodsLvPrices(goodsLvPrices);
				//} // lzf add line 20110114
				
				productList.add(product);
				i++;
			} 
			this.productManager.add(productList);
		}else{
			
			String[] sns = httpRequest.getParameterValues("sns");
			String[] prices = httpRequest.getParameterValues("prices");
			String[] costs = httpRequest.getParameterValues("costs");
		
			String[] weights = httpRequest.getParameterValues("weights");
			
			String[] introduces = httpRequest.getParameterValues("introduces");//产品简介add by yexf 20161107
			
			Product product = this.productManager.getByGoodsId(Integer.parseInt(goods_id));
			if (product == null) {
				product = new Product();
			}
			
			product.setGoods_id(Integer.parseInt(goods_id));
			product.setCost(Double.parseDouble(costs[0]));
			product.setPrice(Double.parseDouble(prices[0]));
			product.setSn(goods.getSn());
			product.setWeight(Double.parseDouble(weights[0]));
			product.setName(goods.getName());
			product.setIntroduce(introduces[0]);
			
			// 20101123新增会员价格相应逻辑
			//String[] lvPriceStr = httpRequest.getParameterValues("lvPrice");
			//String[] lvidStr = httpRequest.getParameterValues("lvid");
			
			// 生成会员价list
			//if (lvidStr != null && lvidStr.length > 0) { // lzf add line 20110114
			//	List<GoodsLvPrice> goodsLvPrices = this.createGoodsLvPrices(lvPriceStr, lvidStr, goodsId);
			//	product.setGoodsLvPrices(goodsLvPrices);
			//} // lzf add line 20110114
			
			List<Product> productList = new ArrayList<Product>();
			productList.add(product);
			this.productManager.add(productList);
			
			goods.setPrice(Double.parseDouble(prices[0]));
			goods.setWeight(Double.parseDouble(weights[0]));
			goods.setCost(Double.parseDouble(costs[0]));
			goods.setIntroduce(introduces[0]);
			
			this.updateSetGoods(goods);
			
		}
	}
	
	private void updateSetGoods(Goods goods) {

		this.baseDaoSupport.update("es_goods", goods, "goods_id = "+ goods.getGoods_id());
		
	}

	/**
	 * 获取已有的货号数量
	 * @param sns
	 * @return
	 */
	private int getSnsSize(String[] sns) {
		int i = 0;
		for (String sn : sns) {
			if (!StringUtil.isEmpty(sn)) {
				i++;
			}
		}
		return i;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void uploadExcel4Goods (File goodsexcel, String goodsexcelFileName) throws IOException {
		
		InputStream in = new FileInputStream(goodsexcel);
		Workbook workbook = null; 
		String ext = FileUtil.getFileExt(goodsexcelFileName);
		
		if("xls".equals(ext)){//
			workbook = new HSSFWorkbook(in);
		}else if("xlsx".equals(ext)){
			workbook = new XSSFWorkbook(in);
		}else{
			throw new RuntimeException("文件格式错误");
		}
		
		for (int numSheet = 0; numSheet < workbook.getNumberOfSheets(); numSheet++) {
		     Sheet sheet = workbook.getSheetAt(numSheet);
		     if (sheet == null) {
		         continue;
		     }
		     final List list = new ArrayList();
		     // Read the Row
		     for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
		         Row row = sheet.getRow(rowNum);
		         if (row != null) {
		             
		        	 String goods_sn = getValue(row.getCell(0));
		        	 String store = getValue(row.getCell(4));
		        	 String price = getValue(row.getCell(5));
		        	 System.out.println(goods_sn + "-" + store + "-" + price);
		        	 if(StringUtil.isEmpty(goods_sn)){
		        		 continue;
		        	 }
		        	 int last_ = goods_sn.lastIndexOf(".0");
		        	 if(last_ > 0){
		        		 goods_sn = goods_sn.substring(0, goods_sn.length() - 2);
		        	 }
		        	 store = StringUtil.isEmpty(store) ? "0" : store;
		        	 price = StringUtil.isEmpty(price) ? "0" : price;
		        	 
		        	 boolean is_double = StringUtil.isDouble(price);
		        	 boolean is_int = StringUtil.isDouble(store);
		        	 //boolean is_num = StringUtil.isNumber(goods_sn);
		        	 
		        	 if(!is_double || !is_int){
		        		 continue;
		        	 }
		        	 
		        	 this.updateStorePrice(goods_sn, (int)Double.parseDouble(store), Double.parseDouble(price));
		         }
		     }
		 }
		
	}
	
	/**
	 * 修改商品的价格和库存 depot_id = 4
	 * @author yexf
	 * 2016-12-4
	 * @param goods_sn
	 * @param store
	 * @param price
	 */
	private void updateStorePrice(String goods_sn, int store, double price) {
		
		this.goodsStoreManager.saveStoreExcel(goods_sn, store, price);
		this.goodsManager.setGoodsPriceExcel(goods_sn, store, price);
		
	}

	private String getValue(Cell cell) {
		if(cell == null){
			return "";
		}
	    if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
	        return String.valueOf(cell.getBooleanCellValue());
	    } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
	        return String.valueOf(cell.getNumericCellValue());
	    } else {
	        return String.valueOf(cell.getStringCellValue());
	    }
	}
	
	public GoodsDataFilterBundle getGoodsDataFilterBundle() {
		return goodsDataFilterBundle;
	}

	public void setGoodsDataFilterBundle(GoodsDataFilterBundle goodsDataFilterBundle) {
		this.goodsDataFilterBundle = goodsDataFilterBundle;
	}

	public IMemberPriceManager getMemberPriceManager() {
		return memberPriceManager;
	}

	public ITagManager getTagManager() {
		return tagManager;
	}

	public void setTagManager(ITagManager tagManager) {
		this.tagManager = tagManager;
	}

	public void setMemberPriceManager(IMemberPriceManager memberPriceManager) {
		this.memberPriceManager = memberPriceManager;
	}

	public IMemberLvManager getMemberLvManager() {
		return memberLvManager;
	}

	public void setMemberLvManager(IMemberLvManager memberLvManager) {
		this.memberLvManager = memberLvManager;
	}
	
	public IDepotMonitorManager getDepotMonitorManager() {
		return depotMonitorManager;
	}

	public void setDepotMonitorManager(IDepotMonitorManager depotMonitorManager) {
		this.depotMonitorManager = depotMonitorManager;
	}

	public IProductManager getProductManager() {
		return productManager;
	}

	public void setProductManager(IProductManager productManager) {
		this.productManager = productManager;
	}

	public IGoodsStoreManager getGoodsStoreManager() {
		return goodsStoreManager;
	}

	public void setGoodsStoreManager(IGoodsStoreManager goodsStoreManager) {
		this.goodsStoreManager = goodsStoreManager;
	}

	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}

	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}

	public SimpleJdbcTemplate getSimpleJdbcTemplate() {
		return simpleJdbcTemplate;
	}

	public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
		this.simpleJdbcTemplate = simpleJdbcTemplate;
	}

	public IAdvManager getAdvManager() {
		return advManager;
	}

	public void setAdvManager(IAdvManager advManager) {
		this.advManager = advManager;
	}

	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}

	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}

	


	
	
}
