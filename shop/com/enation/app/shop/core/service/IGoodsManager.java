package com.enation.app.shop.core.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.model.GoodsStores;
import com.enation.app.shop.core.model.support.GoodsEditDTO;
import com.enation.framework.database.Page;

/**
 * 商品管理接口
 * @author kingapex
 *
 */
public interface IGoodsManager {

	public static final String plugin_type_berforeadd= "goods_add_berforeadd" ;
	public static final String plugin_type_afteradd= "goods_add_afteradd" ;
 
	/**
	 * 读取一个商品的详细
	 * @param Goods_id
	 * @return Map
	 */
	public Map get(Integer goods_id);
	/**
	 * 根据商品ID获取商品
	 * @param goods_id 商品Id
	 * @return Goods
	 */
	public Goods getGoods(Integer goods_id);
	
	public Goods getGoodsByServeId(String serve_id);
	
	/**
	 * 修改时获取数据
	 * @param goods_id
	 * @return
	 */
	public GoodsEditDTO getGoodsEditData(Integer goods_id);
	
	/**
	 * 添加商品
	 * @param goods
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void add(Goods goods);
	
	/**
	 * 修改商品
	 * @param goods
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void edit(Goods goods);

	/**
	 * 商品搜索
	 * @param goodsMap 搜索参数
	 * @param page 分页
	 * @param pageSize 分页每页数量
	 * @param other 其他
	 * @return Page
	 */
	public Page searchGoods(Map goodsMap,int page,int pageSize,String other,String sort,String order);
	/**
	 * 获取商品列表
	 * @param goodsMap 搜索参数
	 * @return List
	 */
	public List searchGoods(Map goodsMap);
	/**
	 * 后台搜索商品
	 * @param name 商品名称
	 * @param sn 商品编号
	 * @param order 排序
	 * @param page 分页
	 * @param pageSize 每页数量 
	 * @return Page
	 */
	public Page searchBindGoods(String name,String sn,String order,int page,int pageSize);
	
	/**
	 * 读取商品回收站列表
	 * @param name 商品名称
	 * @param sn 商品编号
	 * @param order 排序
	 * @param page 分页
	 * @param pageSize 每页数量
	 * @return Page
	 */
	public Page pageTrash(String name,String sn,String order,int page,int pageSize);
	
	/**
	 * 库存余量提醒分页列表
	 * @param warnTotal
	 * @param page 分页
	 * @param pageSize 每页数量
	 * @return List<GoodsStores>
	 */
	public List<GoodsStores> storeWarnGoods(int warnTotal,int page,int pageSize);//库存余量提醒分页列表
	
	/**
	 * 将商品加入回收站
	 * @param ids 商品Id数组
	 */
	public void delete(Integer[] ids);
	/**
	 * 商品还原
	 * @param ids 商品Id数组
	 */
	public void  revert(Integer[] ids);
	/**
	 * 清除商品
	 * @param ids 商品Id数组
	 */
	public void clean(Integer[] ids);
	
	/**
	 * 根据商品id数组读取商品列表
	 * @param ids
	 * @return
	 */
	public List list(Integer[] ids);
	
	/**
	 * 按分类id列表商品
	 * @param catid
	 * @return
	 */
	public List listByCat(Integer catid);
	
	/**
	 * 按分类id列表商品 上架的商品
	 * @author yexf
	 * @date 2016-11-23
	 */
	public List listByCatWbl(Integer catid);
	
	/**
	 * 按标签id列表商品
	 * 如果tagid为空则列表全部
	 * @param tagid
	 * @return
	 */
	public List listByTag(Integer[] tagid);
	
	/**
	 * 不分页、不分类别读取所有有效商品，包含捆绑商品
	 * @return
	 */
	public List<Map> list();
	
	/**
	 * 批量编辑商品
	 */
	public void batchEdit();
	
	/**
	 * 商品信息统计
	 * @return
	 */
	public Map census();

	/**
	 * 获取某个商品的位置信息
	 */
	public void getNavdata(Map goods);
	
	/**
	 * 更新某个商品的字段值
	 * @param filedname 字段名称
	 * @param value 字段值
	 * @param goodsid 商品id
	 */
	public void updateField(String filedname,Object value,Integer goodsid);
	
	/**
	 * 获取某个商品的推荐组合
	 * @param goods_id
	 * @param cat_id
	 * @param brand_id
	 * @param num
	 * @return
	 */
	public List getRecommentList(int goods_id, int cat_id, int brand_id, int num);
	
	/**
	 * 根据货物编号得到某个商品
	 * @param goodSn 货物编号
	 * @return
	 */
	public Goods getGoodBySn(String goodSn);
	
	/**
	 * 修改商品访问次数
	 * @param goods_id
	 */
	public void incViewCount(Integer goods_id);

	/**
	 * 商品列表
	 * @param catid 分类Id
	 * @param tagid 标签Id
	 * @param goodsnum 数量
	 * @return List
	 */
	public List listGoods(String catid,String tagid,String goodsnum);
	
	/**
	 * 购买过商品的会员
	 * @param goods_id 商品Id
	 * @param pageSize 显示数量
	 * @return List
	 */
	public List goodsBuyer(int goods_id, int pageSize);
	
	/**
	 * 判断该商品是否存在
	 * @param goods_id 商品id
	 * @return 是否
	 */
	public boolean isHave(int goods_id);
	
	/**
	 * 根据是否是导航商品 
	 * @author yexf
	 * 2016-10-20
	 * @param is_navigation 0：不是 1：是
	 * @return
	 */
	public List<Map> getGoodsByNavigation(String is_navigation);
	
	
	/**
	 * 根据cat_id查找商品
	 * @author yexf
	 * 2016-10-31
	 * @param cat_id
	 * @param page_size 
	 * @param page_no 
	 */
	public Page listByCatid(String cat_id, String page_no, String page_size);
	
	/**
	 * 设置库存和价格
	 * @author yexf
	 * 2016-12-1
	 */
	public void setSpecStore();
	
	/**
	 * 根据上传的excel修改库存和价格 
	 * @author yexf
	 * 2016-12-4
	 * @param goodsexcel
	 * @param goodsexcelFileName 
	 * @throws Exception 
	 */
	public void uploadExcel4Goods(File goodsexcel, String goodsexcelFileName) throws Exception;
	
	/**
	 * 根据上传的excel修改价格
	 * @author yexf
	 * 2016-12-4
	 */
	public void setGoodsPriceExcel(String goods_sn, int store, double price);
	
	/**
	 * 商品搜索列表和优惠特卖商品列表
	 * @param adv_id
	 * @param search_code
	 * @param page_no
	 * @param page_size
	 * @return
	 */
	public Page listByAdvidOrSearch(String adv_id, String search_code, String page_no, String page_size);
	
	/**
	 * 根据商家查找服务列表 
	 * 2017-7-10
	 * @param  
	 * @param seller_id
	 * @return 
	 */
	public List<Map> listBySellerId(String seller_id);
}