package com.enation.app.shop.core.action.backend;

/**
 * @author LiFenLong 4.0版本改造  2014-4-1
 */
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.component.spec.service.IGoodsSpecStoreManager;
import com.enation.app.shop.component.spec.service.ISpecManager;
import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.model.GoodsSpecImg;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.model.Specification;
import com.enation.app.shop.core.model.Tag;
import com.enation.app.shop.core.model.support.GoodsEditDTO;
import com.enation.app.shop.core.plugin.goods.GoodsPluginBundle;
import com.enation.app.shop.core.service.IBrandManager;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.app.shop.core.service.IGoodsCatManager;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IGoodsSpecImgManager;
import com.enation.app.shop.core.service.IGoodsStoreManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.app.shop.core.service.ITagManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.StringUtil;
@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("goods")
@Results({
	@Result(name="list", type="freemarker", location="/shop/admin/goods/goods_list.html"),
	@Result(name="cat_tree", type="freemarker", location="/shop/admin/cat/select.html"),
	@Result(name="trash_list", type="freemarker", location="/shop/admin/goods/goods_trash.html"),
	@Result(name="input", type="freemarker", location="/shop/admin/goods/goods_input.html"),
	@Result(name="select_cat", type="freemarker", location="/shop/admin/goods/select_cat.html"),
	@Result(name="spec_store", type="freemarker", location="/shop/admin/goods/spec_store.html")
})
public class GoodsAction extends WWAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String name;
	protected String sn;
	protected String order;
	private Integer catid;
	protected Integer[] goods_id;
	protected List brandList;
	protected Integer brand_id;
	protected Integer is_market;
	protected Goods goods;
	protected Map goodsView;
	protected Integer goodsId;
	protected List catList; // 所有商品分类
	protected IGoodsCatManager goodsCatManager;
	protected IBrandManager brandManager;
	protected IGoodsManager goodsManager;
	private ICartManager cartManager;

	private IOrderManager orderManager;
	protected Boolean is_edit;
	protected String actionName;
	protected Integer market_enable;
	private Integer[] tagids;

	private GoodsPluginBundle goodsPluginBundle;

	private ITagManager tagManager;

	protected Map<Integer, String> pluginTabs;
	protected Map<Integer, String> pluginHtmls;

	private List<Tag> tagList;
	
	private String is_other;
	private Integer stype;
	private String keyword;
	private Map goodsMap;
	private String optype="no";
	
	private Map params;
	
	private IProductManager productManager;
	private IGoodsSpecImgManager goodsSpecImgManager;
	private ISpecManager specManager;
	private IGoodsStoreManager goodsStoreManager;
	private IGoodsSpecStoreManager goodsSpecStoreManager;
	
	private List<String> specNameList;
	private List<Product> productList;
	private List<Map> productMap;
	private List<GoodsSpecImg> specImgList;
	private List<Specification> specList;
	private List<Map> depotList;
	
	private File goodsexcel;         
	private String goodsexcelFileName;
	
	public String uploadExcel4Goods(){
		try{
			
			if(goodsexcel == null){
				this.showErrorJson("请选择excel文件");
			}else{
				this.goodsManager.uploadExcel4Goods(goodsexcel, goodsexcelFileName);
				this.showSuccessJson("操作成功");
			}
		}catch(Exception e){
			e.printStackTrace();
			this.showErrorJson("服务器出错");
		}
		/*if (goodsexcel != null) {
			String file = UploadUtil.upload(goodsexcel, goodsexcelFileName, "goodsexcel");
		}*/
		
		return JSON_MESSAGE;
	}
	
	/**
	 * 获取规格和库存
	 * @return
	 */
	public String getSpecStore(){
		HttpServletRequest requst = ThreadContextHolder.getHttpRequest();
		String goods_id = requst.getParameter("goods_id");
		
		if(!StringUtil.isEmpty(goods_id)){
			specNameList = this.productManager.listSpecName(Integer.parseInt(goods_id));
			productList = this.productManager.list(Integer.parseInt(goods_id));
			String depot_id = "4";
			productMap = this.productManager.listAndStoreId(goods_id, depot_id);
			
			specImgList = this.goodsSpecImgManager.list(Integer.parseInt(goods_id));
			specList = this.specManager.listSpecAndValue();
			
		}

		//List lvList = this.memberLvManager.list();
		//freeMarkerPaser.putData("lvList", lvList);
		
		return "spec_store";
	}
	
	/**
	 * 设置规格和库存
	 * @return
	 */
	public String setSpecStore(){
		HttpServletRequest requst = ThreadContextHolder.getHttpRequest();
		String goods_id = requst.getParameter("goods_id");
		
		try{
			if(StringUtil.isEmpty(goods_id)){
				this.showErrorJson("商品不存在");
			}else{
				this.goodsStoreManager.saveStore(Integer.parseInt(goods_id));
				this.goodsManager.setSpecStore();
				this.showSuccessJson("成功");
			}
		}catch(Exception e){
			this.showErrorJson("服务器出错");
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * 跳转选择添加商品的分类
	 * @return 选择添加商品的分类页面
	 */
	public String selectCat() {
		  return "select_cat";
	}
	
	/**
	 * 商品搜索
	 * @author xulipeng 2014年5月14日16:22:13
	 * @param stype 搜索类型,Integer
	 * @param keyword 关键字,String
	 * @param name 商品名称,String
	 * @param sn 商品编号,String
	 * @param catid 商品分类Id,Integer
	 * @param page 页码,Integer
	 * @param PageSize 每页显示数量,Integer
	 * @param sort 排序,String
	 * @return 商品搜索json
	 */
	public String searchGoods() {
		Map goodsMap = new HashMap();
		if(stype!=null){
			if(stype==0){
				goodsMap.put("stype", stype);
				goodsMap.put("keyword", keyword);
			}else if(stype==1){
				goodsMap.put("stype", stype);
				goodsMap.put("name", name);
				goodsMap.put("sn", sn);
				goodsMap.put("catid", catid);
			}
		}
		this.webpage =this.goodsManager.searchGoods(goodsMap, this.getPage(), this.getPageSize(), null,this.getSort(),this.getOrder());
		String s=  JSONArray.fromObject(webpage.getResult()).toString();
		this.json=s;
		return JSON_MESSAGE;
	}

	/**
	 * 商品列表
	 * @param brand_id 品牌Id,Integer
	 * @param catid 商品分类Id,Integer
	 * @param name 商品名称,String
	 * @param sn 商品编号,String 
	 * @param tagids 商品标签Id,Integer[]
	 * @return 商品列表页
	 */
	public String list() {
		goodsMap = new HashMap();
		if (name != null) {
			String encoding = EopSetting.ENCODING;
			if (!StringUtil.isEmpty(encoding)) {
				name = StringUtil.to(name, encoding);
			}
		}

		goodsMap.put("brand_id", brand_id);
		goodsMap.put("is_market", is_market);
		goodsMap.put("catid", catid);
		goodsMap.put("name", name);
		goodsMap.put("sn", sn);
		goodsMap.put("tagids", tagids);
		
		this.brandList=this.brandManager.list();
		tagList = this.tagManager.list();

		this.webpage = goodsManager.searchGoods(goodsMap, this.getPage(),this.getPageSize(), is_other,"goods_id","desc");
		return "list";
	}

	/**
	 * @author LiFenLong
	 * 2014年5月14日18:09  xulipeng  修改
	 * @param stype 搜索类型,Integer
	 * @param keyword 搜索关键字,String
	 * @param catid 商品分类Id,Integer
	 * @param name 商品名称,String
	 * @param sn 商品编号,String 
	 * @return 商品列表页json
	 */
	public String listJson() {
		
		if (name != null) {
			String encoding = EopSetting.ENCODING;
			if (!StringUtil.isEmpty(encoding)) {
				name = StringUtil.to(name, encoding);
			}
		}
		
		goodsMap = new HashMap();
		if(stype!=null){
			if(stype==0){
				goodsMap.put("stype", stype);
				goodsMap.put("keyword", keyword);
			}else if(stype==1){
				goodsMap.put("stype", stype);
				goodsMap.put("name", name);
				goodsMap.put("sn", sn);
				goodsMap.put("catid", catid);
			}
		}
		
		this.webpage =this.goodsManager.searchGoods(goodsMap, this.getPage(), this.getPageSize(), null,this.getSort(),this.getOrder());
		this.showGridJson(this.webpage);
		
		return JSON_MESSAGE;
	}

	/**
	 * 所有的商品分类
	 * @param catList 商品分类列表,List
	 * @return 商品分类页面
	 */
	public String getCatTree() {
		this.catList = this.goodsCatManager.listAllChildren(0);
		return "cat_tree";
	}

	/**
	 * 跳转至商品回收站列表
	 * @return 商品回收站列表
	 */
	public String trash_list() {
		return "trash_list";
	}
	
	/**
	 * 获取商品回收站列表json
	 * @param name 商品名称,String
	 * @param sn 商品编号,String
	 * @param order 排序,String
	 * @return 商品回收站列表json
	 */
	public String trash_listJson(){
		this.webpage = this.goodsManager.pageTrash(name, sn, order,
				this.getPage(), this.getPageSize());
		this.showGridJson(webpage);
		return WWAction.JSON_MESSAGE;
	}

	/**
	 * 删除商品
	 * @author LiFenLong
	 * @param goods_id 商品Id数组,Integer[]
	 * @return json
	 * result 1.操作成功。0.操作失败
	 */
	public String delete() {
		if(EopSetting.IS_DEMO_SITE){
			for(Integer gid : goods_id){
				if(gid<=261){
					this.showErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
					return JSON_MESSAGE;
				}
			}
		}
		try {
			if (goods_id != null)
				for (Integer goodsid : goods_id) {
					if (cartManager.checkGoodsInCart(goodsid)) {
						this.showErrorJson("删除失败，此商品已加入购物车");
						return WWAction.JSON_MESSAGE;
					}
					if (orderManager.checkGoodsInOrder(goodsid)) {
						this.showErrorJson("删除失败，此商品已经下单");
						return WWAction.JSON_MESSAGE;
					}
				}
			this.goodsManager.delete(goods_id);
			this.showSuccessJson("删除成功");
		} catch (RuntimeException e) {
			this.showErrorJson("删除失败");
			logger.error("商品删除失败", e);
		}
		return WWAction.JSON_MESSAGE;
	}

	/**
	 * 还原商品
	 * @param goods_id 商品Id数组,Integer[]
	 * @return json
	 * result 1.操作成功。0.操作失败
	 */
	public String revert() {
		try {
			this.goodsManager.revert(goods_id);
			this.showSuccessJson("还原成功");
		} catch (RuntimeException e) {
			this.showErrorJson("还原失败");
			logger.error("商品还原失败", e);
		}
		return WWAction.JSON_MESSAGE;
	}

	/**
	 * 清除商品
	 * @param goods_id 商品Id数组,Integer[]
	 * @return json
	 * result 1.操作成功。0.操作失败
	 */
	public String clean() {
		try {
			this.goodsManager.clean(goods_id);
			this.showSuccessJson("清除成功");
		} catch (RuntimeException e) {
			this.showErrorJson("清除失败");
			logger.error("商品清除失败",e);
		}
		return WWAction.JSON_MESSAGE;
	}

	/**
	 * 跳转到商品添加页
	 * @param actionName 添加商品方法,String
	 * @param is_edit 是否为修改商品,boolean
	 * @param pluginTabs 商品tab标题List,List
	 * @param pluginHtmls 商品添加内容List,List
	 * @return 商品添加页
	 */
	public String add() {
		actionName = "goods!saveAdd.do";
		is_edit = false;

		this.pluginTabs = this.goodsPluginBundle.getTabList();
		this.pluginHtmls = this.goodsPluginBundle.onFillAddInputData();

		return com.opensymphony.xwork2.Action.INPUT;
	}


	/**
	 * 跳转到商品修改页
	 * @param catList 商品分类列表,List
	 * @param actionName 修改商品方法,String
	 * @param is_edit 是否为修改商品,boolean
	 * @param goodsView 商品信息,Map
	 * @param pluginTabs 商品tab标题List,List
	 * @param pluginHtmls 商品添加内容List,List
	 * @return 商品编辑页
	 */
	public String edit() {
		actionName = "goods!saveEdit.do";
		is_edit = true;

		catList = goodsCatManager.listAllChildren(0);
		GoodsEditDTO editDTO = this.goodsManager.getGoodsEditData(goodsId);
		goodsView = editDTO.getGoods();

		this.pluginTabs = this.goodsPluginBundle.getTabList();
		this.pluginHtmls = editDTO.getHtmlMap();

		return com.opensymphony.xwork2.Action.INPUT;
	}

	/**
	 * 保存商品添加
	 * @param goods 商品,Goods
	 * @return json
	 * result 1.操作成功。0.操作失败
	 */
	public String saveAdd() {
		try {
			goodsManager.add(goods);
			JsonMessageUtil.getNumberJson("goodsid", goods.getGoods_id());
			this.showSuccessJson("商品添加成功");

		} catch (RuntimeException e) {
			this.logger.error("添加商品出错", e);
			this.showErrorJson("添加商品出错" + e.getMessage());
		}

		return WWAction.JSON_MESSAGE;
	}
	/**
	 * 保存商品修改
	 * @param goods 商品,Goods
	 * @return json
	 * result 1.操作成功。0.操作失败
	 */
	public String saveEdit() {
		try {
			goodsManager.edit(goods);
			this.showSuccessJson("商品修改成功");

		} catch (RuntimeException e) {
			this.logger.error("修改商品出错", e);
			showErrorJson("修改商品出错" + e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}

	/**
	 * 更新商品上架状态
	 * @param goodsId 商品Id,Integer
	 * @return json
	 * result 1.操作成功。0.操作失败
	 */
	public String updateMarketEnable() {
		try {
			this.goodsManager.updateField("market_enable", 1, goodsId);
			this.showSuccessJson("更新上架状态成功");
		} catch (RuntimeException e) {
			this.showErrorJson("更新上架状态失败");
			logger.error("商品更新上架失败", e);
		}
		return WWAction.JSON_MESSAGE;
	}
	// set get
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	@Override
	public String getOrder() {
		return order;
	}
	@Override
	public void setOrder(String order) {
		this.order = order;
	}
	public Integer getCatid() {
		return catid;
	}
	public void setCatid(Integer catid) {
		this.catid = catid;
	}
	public Integer[] getGoods_id() {
		return goods_id;
	}
	public void setGoods_id(Integer[] goods_id) {
		this.goods_id = goods_id;
	}
	public List getBrandList() {
		return brandList;
	}
	public void setBrandList(List brandList) {
		this.brandList = brandList;
	}
	public Integer getBrand_id() {
		return brand_id;
	}
	public void setBrand_id(Integer brand_id) {
		this.brand_id = brand_id;
	}
	public Integer getIs_market() {
		return is_market;
	}
	public void setIs_market(Integer is_market) {
		this.is_market = is_market;
	}
	public Goods getGoods() {
		return goods;
	}
	public void setGoods(Goods goods) {
		this.goods = goods;
	}
	public Map getGoodsView() {
		return goodsView;
	}
	public void setGoodsView(Map goodsView) {
		this.goodsView = goodsView;
	}
	public Integer getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(Integer goodsId) {
		this.goodsId = goodsId;
	}
	public List getCatList() {
		return catList;
	}
	public void setCatList(List catList) {
		this.catList = catList;
	}
	public IGoodsCatManager getGoodsCatManager() {
		return goodsCatManager;
	}
	public void setGoodsCatManager(IGoodsCatManager goodsCatManager) {
		this.goodsCatManager = goodsCatManager;
	}
	public IBrandManager getBrandManager() {
		return brandManager;
	}
	public void setBrandManager(IBrandManager brandManager) {
		this.brandManager = brandManager;
	}
	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}
	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}
	public ICartManager getCartManager() {
		return cartManager;
	}
	public void setCartManager(ICartManager cartManager) {
		this.cartManager = cartManager;
	}
	public IOrderManager getOrderManager() {
		return orderManager;
	}
	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}
	public Boolean getIs_edit() {
		return is_edit;
	}
	public void setIs_edit(Boolean is_edit) {
		this.is_edit = is_edit;
	}
	public String getActionName() {
		return actionName;
	}
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	public Integer getMarket_enable() {
		return market_enable;
	}
	public void setMarket_enable(Integer market_enable) {
		this.market_enable = market_enable;
	}
	public Integer[] getTagids() {
		return tagids;
	}
	public void setTagids(Integer[] tagids) {
		this.tagids = tagids;
	}
	public GoodsPluginBundle getGoodsPluginBundle() {
		return goodsPluginBundle;
	}
	public void setGoodsPluginBundle(GoodsPluginBundle goodsPluginBundle) {
		this.goodsPluginBundle = goodsPluginBundle;
	}
	public ITagManager getTagManager() {
		return tagManager;
	}
	public void setTagManager(ITagManager tagManager) {
		this.tagManager = tagManager;
	}
	public Map<Integer, String> getPluginTabs() {
		return pluginTabs;
	}
	public void setPluginTabs(Map<Integer, String> pluginTabs) {
		this.pluginTabs = pluginTabs;
	}
	public Map<Integer, String> getPluginHtmls() {
		return pluginHtmls;
	}
	public void setPluginHtmls(Map<Integer, String> pluginHtmls) {
		this.pluginHtmls = pluginHtmls;
	}
	public List<Tag> getTagList() {
		return tagList;
	}
	public void setTagList(List<Tag> tagList) {
		this.tagList = tagList;
	}
	public String getIs_other() {
		return is_other;
	}
	public void setIs_other(String is_other) {
		this.is_other = is_other;
	}
	public Integer getStype() {
		return stype;
	}
	public void setStype(Integer stype) {
		this.stype = stype;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public Map getGoodsMap() {
		return goodsMap;
	}
	public void setGoodsMap(Map goodsMap) {
		this.goodsMap = goodsMap;
	}
	public String getOptype() {
		return optype;
	}
	public void setOptype(String optype) {
		this.optype = optype;
	}

	public Map getParams() {
		return params;
	}

	public void setParams(Map params) {
		this.params = params;
	}

	public IProductManager getProductManager() {
		return productManager;
	}

	public void setProductManager(IProductManager productManager) {
		this.productManager = productManager;
	}

	public IGoodsSpecImgManager getGoodsSpecImgManager() {
		return goodsSpecImgManager;
	}

	public void setGoodsSpecImgManager(IGoodsSpecImgManager goodsSpecImgManager) {
		this.goodsSpecImgManager = goodsSpecImgManager;
	}

	public ISpecManager getSpecManager() {
		return specManager;
	}

	public void setSpecManager(ISpecManager specManager) {
		this.specManager = specManager;
	}

	public List<String> getSpecNameList() {
		return specNameList;
	}

	public void setSpecNameList(List<String> specNameList) {
		this.specNameList = specNameList;
	}

	public List<Product> getProductList() {
		return productList;
	}

	public void setProductList(List<Product> productList) {
		this.productList = productList;
	}

	public List<GoodsSpecImg> getSpecImgList() {
		return specImgList;
	}

	public void setSpecImgList(List<GoodsSpecImg> specImgList) {
		this.specImgList = specImgList;
	}

	public List<Specification> getSpecList() {
		return specList;
	}

	public void setSpecList(List<Specification> specList) {
		this.specList = specList;
	}

	public IGoodsStoreManager getGoodsStoreManager() {
		return goodsStoreManager;
	}

	public void setGoodsStoreManager(IGoodsStoreManager goodsStoreManager) {
		this.goodsStoreManager = goodsStoreManager;
	}

	public IGoodsSpecStoreManager getGoodsSpecStoreManager() {
		return goodsSpecStoreManager;
	}

	public void setGoodsSpecStoreManager(
			IGoodsSpecStoreManager goodsSpecStoreManager) {
		this.goodsSpecStoreManager = goodsSpecStoreManager;
	}

	public List<Map> getDepotList() {
		return depotList;
	}

	public void setDepotList(List<Map> depotList) {
		this.depotList = depotList;
	}

	public List<Map> getProductMap() {
		return productMap;
	}

	public void setProductMap(List<Map> productMap) {
		this.productMap = productMap;
	}

	public File getGoodsexcel() {
		return goodsexcel;
	}

	public void setGoodsexcel(File goodsexcel) {
		this.goodsexcel = goodsexcel;
	}

	public String getGoodsexcelFileName() {
		return goodsexcelFileName;
	}

	public void setGoodsexcelFileName(String goodsexcelFileName) {
		this.goodsexcelFileName = goodsexcelFileName;
	}
	
	
}
