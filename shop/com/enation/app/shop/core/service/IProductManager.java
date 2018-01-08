package com.enation.app.shop.core.service;

import java.util.List;
import java.util.Map;

import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.model.Specification;
import com.enation.framework.database.Page;

/**
 * 货品管理
 * 
 * @author kingapex 2010-3-9下午05:55:50
 */
public interface IProductManager {

	/**
	 * 批量添加货品
	 * @param productList
	 */
	public void add(List<Product> productList);
	
	/**
	 * 读取货品详细
	 * @param productid
	 * @return
	 */
	public Product get(Integer productid);
	
	/**
	 * 读取某个商品的货品，一般用于无规格商品或捆绑商品
	 * @param goodsid
	 * @return
	 */
	public Product getByGoodsId(Integer goodsid);
	
	/**
	 * 查询某商品的规格名称
	 * @param goodsid
	 * @return
	 */
	public List<String> listSpecName(int goodsid);
	
	/**
	 * 查询某个商品的规格
	 * 
	 * @param goods_id
	 * @return
	 */
	public List<Specification> listSpecs(Integer goods_id);

	/**
	 * 分页列出全部货品<br/>
	 * lzf add
	 * 
	 * @return
	 */
	public Page list(String name,String sn,int pageNo, int pageSize, String order);
	
	/**
	 * 根据一批货品id读取货品列表
	 * @param productids 货品id数组，如果为空返回 空list
	 * @return 货品列表
	 */
	public List list(Integer[] productids);
	
	/**
	 * 查询某个商品的货品
	 * 
	 * @param goods_id
	 * @return
	 */
	public List<Product> list(Integer goods_id);
	
	/**
	 * 删除某商品的所有货品
	 * @param goodsid
	 */
	public void delete(Integer[] goodsid);

	/**
	 * 获取商品的规格类型
	 * @author yexf
	 * 2016-11-1
	 * @param goods_id
	 * @return
	 */
	public List<Map> listSpecsByGoodsId(String goods_id);

	/**
	 * 获取商品的规格值
	 * @author yexf
	 * 2016-11-1
	 * @param goods_id
	 * @return
	 */
	public List<Map> listSpecValuesByGoodsId(String goods_id, String spec_id);

	/**
	 * 获取产品（store_id）列表
	 * @author yexf
	 * 2016-12-1
	 * @param goods_id
	 * @param depot_id 
	 * @return
	 */
	public List<Map> listAndStoreId(String goods_id, String depot_id);

	/**
	 * 根据sn获取product
	 * @author yexf
	 * 2016-12-4
	 * @param goods_sn
	 * @return
	 */
	public Map getProductBySn(String goods_sn);
	
	
	
}
