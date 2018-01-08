package com.enation.app.shop.core.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MemberLv;
import com.enation.app.shop.core.model.GoodsLvPrice;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.model.SpecValue;
import com.enation.app.shop.core.model.Specification;
import com.enation.app.shop.core.service.IGoodsSpecImgManager;
import com.enation.app.shop.core.service.IMemberLvManager;
import com.enation.app.shop.core.service.IMemberPriceManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.database.Page;
import com.enation.framework.database.StringMapper;
import com.enation.framework.util.StringUtil;

/**
 * 货品管理
 * @author kingapex
 *2010-3-9下午06:27:48
 */
public class ProductManager extends BaseSupport<Product> implements IProductManager {
	
	private IMemberPriceManager memberPriceManager;
	private IMemberLvManager memberLvManager;
	private IGoodsSpecImgManager goodsSpecImgManager;
	
	/**
	 * 由productList的id生成,号隔开的字串
	 * @param productList
	 * @return
	 */
	private String getProductidStr(List<Product> productList){
		StringBuffer str = new StringBuffer();
		for(Product pro:productList){
			
			Integer productid = pro.getProduct_id();
			if(productid!=null){
				if(str.length()!=0){
					str.append(",");	
				}
				str.append(pro.getProduct_id());
			}
		}
		
		return str.toString();
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)  
	public void add(List<Product> productList) {
		 if(productList.size()>0){
			 Integer goodsid  =  productList.get(0).getGoods_id();
			 //清除规格信息
			 this.baseDaoSupport.execute("delete from  goods_spec  where goods_id=?",goodsid);
			 this.baseDaoSupport.execute("delete from  goods_lv_price  where goodsid=?", goodsid);
			 
			 String proidstr = this.getProductidStr(productList);
			 //清除删除的规格数据
			 String sql  ="delete from product where goods_id=? ";
			 if(!StringUtil.isEmpty( proidstr)){
				 sql+=" and  product_id  not in("+proidstr+")";
			 }
			 this.baseDaoSupport.execute(sql,goodsid);
			 
			 //清除删除掉的库存数据
			 sql="delete from product_store where goodsid=? ";
			 if(!StringUtil.isEmpty( proidstr)){
				 sql+=" and  productid  not in("+proidstr+")";
			 }
			 this.baseDaoSupport.execute(sql, goodsid);
			 
		 }
		 
		 for(Product product:productList){
			 
			 //如果货号为空则插入新货品，如果货号存在则更新货品
			 Integer product_id  =  product.getProduct_id();
			 if( product_id==null ){
				 this.baseDaoSupport.insert("product", product);
				 product_id= this.baseDaoSupport.getLastId("product");
				 product.setProduct_id(product_id);
			 }else{
				 this.baseDaoSupport.update("product", product, "product_id="+ product_id);
			 }
			 
			 //货品对应的规格组合
			 List<SpecValue> specList = product.getSpecList();
			 
			 for(SpecValue specvalue:specList){
				 this.daoSupport.execute(
						 "insert into "+ this.getTableName("goods_spec")+"(spec_id,spec_value_id,goods_id,product_id)values(?,?,?,?)", 
						 specvalue.getSpec_id(),specvalue.getSpec_value_id(),product.getGoods_id(),product_id);
			 }
			 
			 //添加会员价格数据
			 List<GoodsLvPrice> lvPriceList  =  product.getGoodsLvPrices();
			 if(lvPriceList!=null){
				 for(GoodsLvPrice lvPrice  : lvPriceList){
					 lvPrice.setProductid(product_id);
					 this.baseDaoSupport.insert("goods_lv_price", lvPrice);
				 }
				 
			 }
		 }
		 
		 if(productList.size()>0){
			 Integer goodsid  =  productList.get(0).getGoods_id();
			 //更新商品的specs字段
			 this.baseDaoSupport.execute("update goods set specs=? where goods_id=?", JSONArray.fromObject(productList).toString(),goodsid);
		 } 
	}
	
	@Override
	public Product get(Integer productid) {
		String sql = " select * from product where product_id = ? ";
		return this.baseDaoSupport.queryForObject(sql, Product.class, productid);
	}
	
	@Override
	public List<String> listSpecName(int goodsid){

		StringBuffer sql = new StringBuffer();
		sql.append("select distinct s.spec_name ");
		sql.append(" from ");
		
		sql.append(this.getTableName("specification"));
		sql.append(" s,");
		
		sql.append(this.getTableName("goods_spec"));
		sql.append(" gs ");
		
		sql.append("where s.spec_id = gs.spec_id and gs.goods_id=?");
		List  list  =this.daoSupport.queryForList(sql.toString(),new StringMapper(), goodsid);
		return list;
	}
	
	/**
	 * 形成list里实体(spec)放入子(specvlaue)list的效果
	 */
	@Override
	public List<Specification> listSpecs(Integer goodsId) {
 
		StringBuffer sql = new StringBuffer();
		sql.append("select distinct s.spec_id,s.spec_type,s.spec_name,sv.spec_value_id,sv.spec_value,sv.spec_image ,gs.goods_id as goods_id ");
		sql.append(" from ");
		
		sql.append(this.getTableName("specification"));
		sql.append(" s,");
		
		sql.append(this.getTableName("spec_values"));
		sql.append(" sv,");
		
		sql.append(this.getTableName("goods_spec"));
		sql.append(" gs ");
		
		sql.append("where s.spec_id = sv.spec_id  and gs.spec_value_id = sv.spec_value_id and gs.goods_id=?  ORDER BY s.spec_id");
		List<Map> list = this.daoSupport.queryForList(sql.toString(), goodsId);
			
		List<Specification> specList = new ArrayList<Specification>();
		Integer spec_id =0;
		Specification spec = null;
		for(Map map: list){
			Integer dbspecid =Integer.valueOf( map.get("spec_id").toString() );
			List<SpecValue> valueList ;
		
			if( spec_id.intValue() != dbspecid.intValue() ){
				spec_id = dbspecid;
				valueList = new ArrayList<SpecValue>();
				 
				spec  = new Specification();
				spec.setSpec_id( dbspecid);
				spec.setSpec_name(map.get("spec_name").toString());
				spec.setSpec_type(Integer.parseInt(map.get("spec_type").toString()));
				
				specList.add(spec);
				
				spec.setValueList(valueList);
			}else{
				valueList = spec.getValueList();
			}
			
			SpecValue value  = new SpecValue();
			value.setSpec_value(map.get("spec_value").toString());
			value.setSpec_value_id(Integer.valueOf( map.get("spec_value_id").toString() ));
			String spec_img  = (String)map.get("spec_image");
			
			//将本地中径替换为静态资源服务器地址
			if(spec_img != null){
				spec_img = UploadUtil.replacePath(spec_img); 
			}
			value.setSpec_image(spec_img);
			
			/*if((Integer)map.get("spec_type") == 1){
				// 将本地中径替换为静态资源服务器地址
				if (spec_img != null) {
					if(spec_img.startsWith("..")){
						//spec_img = EopSetting.IMG_SERVER_DOMAIN.substring(0, EopSetting.IMG_SERVER_DOMAIN.length() - 8) + spec_img.substring(2, spec_img.length());
						spec_img = "";
					}else{
						spec_img = UploadUtil.replacePath(spec_img);
					}
				}
	
				GoodsSpecImg goodsSpecImg = this.goodsSpecImgManager.get(goodsId,
						Integer.valueOf(map.get("spec_value_id").toString()));
				if (goodsSpecImg != null) {
					if(!StringUtil.isEmpty(goodsSpecImg.getSpec_image())){
						value.setSpec_image(goodsSpecImg.getSpec_image());
					}else{
						value.setSpec_image(spec_img);
					}
				} else {
					value.setSpec_image(spec_img);
				}
			}else{
				value.setSpec_image("");
			}*/
			
			valueList.add(value);
		}
		
		return specList ;
	}
	
	/**
	 * 读取某个商品的货品列表
	 */
	@Override
	public List<Product> list(Integer goodsId) {
		
		String sql = " select * from product where goods_id = ? order by product_id asc ";
		List<Product> prolist = baseDaoSupport.queryForList(sql, Product.class, goodsId);
		
		sql = " select sv.*, gs.product_id product_id from " + this.getTableName("goods_spec") + " gs inner join " + this.getTableName("spec_values") + " sv on gs.spec_value_id = sv.spec_value_id where gs.goods_id = ? order by gs.id desc ";
		 
		//sql="select * from "+ this.getTableName("goods_spec")+" gs,"+this.getTableName("spec_values")+" sv where gs.spec_value_id = sv.spec_value_id and  gs.goods_id=? ";
		
		List<Map> gsList = this.daoSupport.queryForList(sql, goodsId);
		
		List<GoodsLvPrice> memPriceList = new ArrayList<GoodsLvPrice>();
		
		Member member = UserServiceFactory.getUserService().getCurrentMember();
		double discount = 1; //默认是原价,防止无会员级别时出错
		if(member != null){
			memPriceList = this.memberPriceManager.listPriceByGid(goodsId);
			MemberLv lv = this.memberLvManager.get(member.getLv_id());
			if(lv != null)
			discount = lv.getDiscount()/100.00;
			//System.out.println(discount);
		}
		
		for(Product pro : prolist){
			
			if(member != null){
				Double price = pro.getPrice();
				if(memPriceList != null && memPriceList.size() > 0)
				price = this.getMemberPrice(pro.getProduct_id(), member.getLv_id(), price, memPriceList, discount);
				pro.setPrice(price);
			}
			int size = gsList.size() - 1;
			for(int i = size; i >= 0; i--){
				Map gs = gsList.get(i);
				Integer productid;
				productid = ((Integer)gs.get("product_id")).intValue();
				
				//是这个货品的规格
				//则压入到这个货品的规格中
				//用到了spec_value_id
				if(pro.getProduct_id().intValue() == productid){ 
					SpecValue spec = new SpecValue();
					spec.setSpec_value_id((Integer)gs.get("spec_value_id"));
					spec.setSpec_id((Integer)gs.get("spec_id"));
					String spec_img = (String)gs.get("spec_image");
					
					//将本地中径替换为静态资源服务器地址
					if(spec_img != null){
						spec_img = UploadUtil.replacePath(spec_img); 
					}
					spec.setSpec_image(spec_img);
					spec.setSpec_value((String)gs.get("spec_value"));
					spec.setSpec_type(Integer.parseInt(gs.get("spec_type").toString()));
					pro.addSpec(spec);
				}
				
			}
		}
		return prolist;
	}
	
	/**
	 * 获取某货品的会员价格
	 * @param price 销售价
	 * @param memPriceList 会员价列表
	 * @param discount 此会员级别的折扣
	 * @return
	 */
	private Double getMemberPrice(int productid,int lvid,Double price,List<GoodsLvPrice> memPriceList,double discount){
		Double memPrice  = price * discount; //默认是此会员级别的折扣价
		
		//然后由具体会员价格中寻找，看是否指定了具体的会员价格
		for( GoodsLvPrice  lvPrice  :memPriceList ){
			if(lvPrice.getProductid() == productid && lvPrice.getLvid() == lvid){ //找到此货品,此会员级别的价格
				memPrice = lvPrice.getPrice();
			}
		}
		return memPrice;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)  
	public void delete(Integer[] goodsid){
		String id_str = StringUtil.arrayToString(goodsid, ",");
		String sql ="delete from goods_spec where goods_id in ("+ id_str +")";
		this.baseDaoSupport.execute(sql);
		
		sql ="delete from goods_lv_price where goodsid in ("+ id_str +")";
		this.baseDaoSupport.execute(sql);
		
		sql ="delete from product where goods_id in ("+ id_str +")";
		this.baseDaoSupport.execute(sql);

	}
	
	@Override
	public Page list(String name,String sn,int pageNo, int pageSize, String order) {
		order = order == null ? "product_id asc" : order;
		StringBuffer sql = new StringBuffer();
		sql.append("select p.* from " + this.getTableName("product") + " p left join " + this.getTableName("goods") + " g on g.goods_id = p.goods_id ");
		sql.append(" where g.disabled=0");
		if(!StringUtil.isEmpty(name)){
			sql.append(" and g.name like '%");
			sql.append(name);
			sql.append("%'");
		}
		if(!StringUtil.isEmpty(sn)){
			sql.append(" and g.sn = '");
			sql.append(sn);
			sql.append("'");
		}
		
		sql.append(" order by " + order);
		return this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize);
	}
	
	@Override
	public List list(Integer[] productids) {
		if(productids==null || productids.length==0) return new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("select p.* from " + this.getTableName("product") + " p left join " + this.getTableName("goods") + " g on g.goods_id = p.goods_id ");
		sql.append(" where g.disabled=0");
		sql.append(" and p.product_id in(");
		sql.append(StringUtil.arrayToString(productids, ","));
		sql.append(")");
		
		return  this.daoSupport.queryForList(sql.toString());
	}

	@Override
	public Product getByGoodsId(Integer goodsid) {

		String sql ="select * from product where goods_id=?";
		List<Product> proList  =this.baseDaoSupport.queryForList(sql, Product.class, goodsid);
		if(proList==null || proList.isEmpty()){
		return null;
		}
		return proList.get(0);
	}

	@Override
	public List<Map> listSpecsByGoodsId(String goods_id) {
		
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT spec_id, spec_name, spec_type FROM es_specification WHERE ");
		sql.append(" spec_id IN (SELECT spec_id FROM es_goods_spec WHERE goods_id = ? GROUP BY spec_id) ");
		
		List<Map> list = this.baseDaoSupport.queryForList(sql.toString(), goods_id);
		
		return list;
	}
	
	@Override
	public List<Map> listSpecValuesByGoodsId(String goods_id, String spec_id) {
		
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT spec_value_id, spec_value, spec_image, spec_type FROM es_spec_values WHERE ");
		sql.append(" spec_value_id IN (SELECT spec_value_id FROM es_goods_spec WHERE goods_id = ? AND spec_id = ?) ");
		
		List<Map> list = this.baseDaoSupport.queryForList(sql.toString(), goods_id, spec_id);
		
		return list;
	}

	@Override
	public List<Map> listAndStoreId(String goods_id, String depot_id) {
		
		/**
		 * 首先取出这个商品的所有货品以及规格信息
		 */
		List<Product> prolist = this.list(Integer.parseInt(goods_id));
		
		String sql = " select * from product_store where goodsid = ? ";
		List<Map> storeList = this.baseDaoSupport.queryForList(sql, goods_id);
		
		List<Map> pList  = this.getProductList(Integer.parseInt(depot_id), prolist, storeList);
		return pList;
	}
	
	private List<Map> getProductList(int depotid, List<Product> productList, List<Map> storeList){
		
		List<Map> pList = new ArrayList<Map>();
		
		for(Product product : productList){
			Map pro = new HashMap();
			
			pro.put("specList",product.getSpecList());
			pro.put("sn", product.getSn());
			pro.put("name",product.getName());
			pro.put("product_id", product.getProduct_id());
			pro.put("cost", product.getCost());
			pro.put("weight", product.getWeight());
			pro.put("price", product.getPrice());
			pro.put("introduce", product.getIntroduce());
			pro.put("storeid", 0);
			pro.put("store", 0);
			for(Map store:storeList){
				//找到此仓库、此货品
				if(depotid == ((Integer)store.get("depotid")).intValue() 
						&& product.getProduct_id().intValue() == ((Integer)store.get("productid")).intValue()){
					pro.put("storeid",store.get("storeid"));
					pro.put("store", store.get("store"));
					pro.put("enable_store", store.get("enable_store"));
				} 
			}
			
			pList.add(pro);
		}
		
		return pList;
	}

	@Override
	public Map getProductBySn(String goods_sn) {

		StringBuffer sql = new StringBuffer();
		sql.append(" select product_id, goods_id from es_product where sn = ? ");
		
		List<Map> list = this.baseDaoSupport.queryForList(sql.toString(), goods_sn);
		
		if(list.size() != 0){
			Map m = list.get(0);
			return m;
		}else{
			return null;
		}
		
	}
	
	
	public IGoodsSpecImgManager getGoodsSpecImgManager() {
		return goodsSpecImgManager;
	}

	public void setGoodsSpecImgManager(IGoodsSpecImgManager goodsSpecImgManager) {
		this.goodsSpecImgManager = goodsSpecImgManager;
	}
	public IMemberPriceManager getMemberPriceManager() {
		return memberPriceManager;
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
	
	public static void main(String[] args){
		double discount = 90.00/100.00;
		//System.out.println(discount);
	}
}
