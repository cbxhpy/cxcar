package com.enation.app.shop.component.goodscore.widget.goods.detail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.MemberLv;
import com.enation.app.shop.core.model.GoodsLvPrice;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.service.IMemberLvManager;
import com.enation.app.shop.core.service.IMemberPriceManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.eop.sdk.widget.AbstractWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.StringUtil;

/**
 * 会员vip价格挂件
 * @author kingapex
 *
 */
@Component("vip_price")
@Scope("prototype")
public class VipPriceWidget extends AbstractWidget {

	private IMemberLvManager memberLvManager;
	
	private IMemberPriceManager memberPriceManager;
	private IProductManager productManager;
	
	@Override
	protected void display(Map<String, String> params) {
		Map goods  = (Map)ThreadContextHolder.getHttpRequest().getAttribute("goods");

		this.execute(params, goods);
	}
	
	
	
	public void execute(Map<String, String> params,Map goods) {
	 
		if("vipprice".equals(this.action)){
			try{
				this.showVipPrice(); 
			}catch (RuntimeException e) {
				this.showErrorJson(e.getMessage());
			}
			return;
		}
		
		double price =Double.valueOf(""+goods.get("price") );
		double vipprice= price; 
		if(price==0){
			goods.put("vipprice", 0);
		}
		
		List<MemberLv> memberLvList = memberLvManager.list();
		if(memberLvList != null && memberLvList.size() > 0){
			
			
			List<GoodsLvPrice> glpList = this.memberPriceManager.listPriceByGid(Integer.valueOf(goods.get("goods_id").toString()));
			if(glpList!=null&&glpList.size()>0){
				//设置了会员价格
				double discount = 1;
				for(MemberLv lv : memberLvList){
					double lvprice1=0;  //会员价格
					if(lv.getDiscount()!=null){
						discount= lv.getDiscount()/100.00;
						lvprice1  = CurrencyUtil.mul(price, discount);
					}
					double lvPrice = this.getMemberPrice(lv.getLv_id(), glpList); //定义的会员价格
					
					if(lvPrice==0){
						lv.setLvPrice(lvprice1);
						lvPrice=lvprice1;
					}else{
						lv.setLvPrice(lvPrice);
					}
					if(vipprice>lvPrice){
						vipprice=lvPrice;
					}
					
				}
				goods.put("vipprice", vipprice);
				this.putData("vipPriceList", memberLvList);
			}else{
			
				double discount = 1;
				for(MemberLv lv : memberLvList){
					
					if(lv.getDiscount()!=null){
						discount= lv.getDiscount()/100.00;
						double lvprice  =CurrencyUtil.mul(price, discount) ;
						lv.setLvPrice(lvprice);
						if(vipprice>lvprice){
							vipprice=lvprice;
						}
						
					}
					
				}
				
				goods.put("vipprice", vipprice);
				this.putData("vipPriceList", memberLvList);
			
			}
			
			//以下是美睛网的逻辑，暂时注释，在3.1.0中参考。
/*			Object istejia = goods.get("istejia"); //特价
			Object no_discount = goods.get("no_discount"); //是否打折
			if(istejia!=null && "1".equals(istejia.toString())){
				for(MemberLv lv : memberLvList){
					lv.setLvPrice(price);
				}
				goods.put("vipprice", price);
				this.putData("vipPriceList", memberLvList);
			}
			else if(no_discount!=null && "1".equals(no_discount.toString())){
				for(MemberLv lv : memberLvList){
					lv.setLvPrice(price);
				}
				goods.put("vipprice", price);
				this.putData("vipPriceList", memberLvList);
			}
			else{
				List<GoodsLvPrice> glpList = this.memberPriceManager.listPriceByGid(Integer.valueOf(goods.get("goods_id").toString()));
				if(glpList!=null&&glpList.size()>0){
					//设置了会员价格
					for(MemberLv lv : memberLvList){
						double lvPrice = this.getMemberPrice(lv.getLv_id(), glpList);
						if(lvPrice==0){
							lv.setLvPrice(price);
						}else{
							lv.setLvPrice(lvPrice);
						}
						if(lv.getLv_id()==4){
							goods.put("vipprice", lvPrice);
						}
					}
					this.putData("vipPriceList", memberLvList);
				}else{
					
					List list = this.memberLvManager.getHaveLvDiscountByCat(Integer.valueOf(goods.get("cat_id").toString()));
					if(list!=null && list.size()>0){
						//设置了类别折扣
						for(MemberLv lv : memberLvList){
							int lvdiscount = this.getLvDiscount(lv.getLv_id(), list);
							if(lvdiscount==0){
								lv.setLvPrice(price);
							}else{
								lv.setLvPrice(price*lvdiscount/100);
							}
							if(lv.getLv_id()==4){
								goods.put("vipprice", lv.getLvPrice());
							}
						}
						this.putData("vipPriceList", memberLvList);
					}else{
						double discount = 1;
						 
						//什么也没有设置
						for(MemberLv lv : memberLvList){
							
							if(lv.getDiscount()!=null){
								discount= lv.getDiscount()/100.00;
								double lvprice  = price*discount;
								lv.setLvPrice(lvprice);
								if(price>lvprice){
									price=lvprice;
								}
								
							}
							
						}
						
						goods.put("vipprice", price);
						this.putData("vipPriceList", memberLvList);
					}
					
				}
			}
			*/
			
			
		}		
	}
	
	
	private  void  showVipPrice(){
		
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		int productid  = StringUtil.toInt(request.getParameter("productid"), false);
		Product product  = this.productManager.get(productid);
		double price= product.getPrice(); //此货品的价格
		double vipprice= price; 
		
		List<MemberLv> memberLvList = memberLvManager.list();
		
		//读取此货品的会员价格
		List<GoodsLvPrice> glpList = this.memberPriceManager.listPriceByPid(productid);
		
		//设置了会员价格，读取出低的价格
		if(glpList!=null&&glpList.size()>0){
			//设置了会员价格
			double discount = 1;
			for(MemberLv lv : memberLvList){
				double lvprice1=0;  //会员价格
				if(lv.getDiscount()!=null){
					discount= lv.getDiscount()/100.00;
					lvprice1  = CurrencyUtil.mul(price, discount);
				}
				double lvPrice = this.getMemberPrice(lv.getLv_id(), glpList); //定义的会员价格
				
				if(lvPrice==0){
					lv.setLvPrice(lvprice1);
					lvPrice=lvprice1;
				}else{
					lv.setLvPrice(lvPrice);
				}
				if(vipprice>lvPrice){
					vipprice=lvPrice;
				}
				
			}
			
		 
		}else{
		
			double discount = 1;
			for(MemberLv lv : memberLvList){
				
				if(lv.getDiscount()!=null){
					discount= lv.getDiscount()/100.00;
					double lvprice  =CurrencyUtil.mul(price, discount) ;
					lv.setLvPrice(lvprice);
					if(vipprice>lvprice){
						vipprice=lvprice;
					}
					
				}
				
			}
		
		}
		
		Map vip = new HashMap(2);
		vip.put("vipprice", vipprice);
		vip.put("weight", product.getWeight());
		
		this.showJson( JsonMessageUtil.getObjectJson(vip) );
			
	}
	
	
	
	/**
	 * 根据级别获取 该级别在某分类下的折扣
	 * @param lv_id
	 * @param list
	 * @return
	 */
	private int getLvDiscount(int lv_id,List list){
		for (int i = 0; i < list.size(); i++) {
			Map map = (Map)list.get(i);
			Integer lvid = (Integer)map.get("lv_id");
			Integer discount  = (Integer)map.get("discount");
			if(lvid.intValue()==lv_id){
				return discount.intValue();
			}
		}
		return 0;
	}
	
	/**
	 * 根据级别获取 该级别某商品的价格
	 * @param lv_id
	 * @param memPriceList
	 * @return
	 */
	private double getMemberPrice(int lv_id,List<GoodsLvPrice> memPriceList){
		for(GoodsLvPrice lvPrice:memPriceList){
			if( lv_id ==  lvPrice.getLvid()){
				return lvPrice.getPrice();
			}
		}
		
		return 0;
	}

	@Override
	protected void config(Map<String, String> params) {
		
	}

	public void setMemberLvManager(IMemberLvManager memberLvManager) {
		this.memberLvManager = memberLvManager;
	}

	public IMemberPriceManager getMemberPriceManager() {
		return memberPriceManager;
	}

	public void setMemberPriceManager(IMemberPriceManager memberPriceManager) {
		this.memberPriceManager = memberPriceManager;
	}


	public IProductManager getProductManager() {
		return productManager;
	}


	public void setProductManager(IProductManager productManager) {
		this.productManager = productManager;
	}


	public IMemberLvManager getMemberLvManager() {
		return memberLvManager;
	}

	
}
