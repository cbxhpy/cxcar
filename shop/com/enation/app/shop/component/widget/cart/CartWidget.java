package com.enation.app.shop.component.widget.cart;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.Cart;
import com.enation.app.shop.core.model.FreeOffer;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.model.support.OrderPrice;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.app.shop.core.service.IFreeOfferManager;
import com.enation.app.shop.core.service.IGnotifyManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.app.shop.core.service.IPromotionManager;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.eop.sdk.widget.AbstractMemberWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.directive.ImageUrlDirectiveModel;
import com.enation.framework.util.StringUtil;

/**
 * 购物车挂件
 * @author kingapex
 *2010-3-23下午03:18:58
 */
@Component("shop_cart")
@Scope("prototype")
public class CartWidget extends AbstractMemberWidget {
	private  HttpServletRequest  request;
	private ICartManager cartManager;
	private IPromotionManager promotionManager ;
	private IFreeOfferManager freeOfferManager;
	private IProductManager productManager ;

	private IGnotifyManager gnotifyManager;
	
	
	@Override
	protected void config(Map<String, String> params) {

	}

	
	@Override
	public boolean cacheAble() {
		return false;
	}


	@Override
	protected void display(Map<String, String> params) {
		request  = ThreadContextHolder.getHttpRequest();
		String step = request.getParameter("step");
		if(step==null|| "".equals(step)){
			this.execute();
		} 
		 
	}
	
	
	private void execute() {
		 if("add".equals(action)){
			 this.add();
		 } 
		 if("delete".equals(action)){
			 this.delete();
		 }
		 if("clean".equals(action)){
			 this.clean();
		 } 		 
		 if("update".equals(action)){
			 this.updateNum();
		 } 		 
		 if("getTotal".equals(action)){
			 this.getTotal();
		 }
 
		 
		 if(action==null || action.equals("")){
			 this.enableCustomPage();
			 this.list();
		 } 

	 
	}
	

	private void selfAdd(){
		String productid= request.getParameter("productid");
		String itemtype= request.getParameter("itemtype");
		String sessionid = request.getSession().getId();
		itemtype =itemtype == null?"0":itemtype;
		
		String num = request.getParameter("num");
		num=num==null|| num.equals("")?"1":num;
		if(productid==null || "".equals(productid)) throw new RuntimeException("productid is null");
		
		//添加赠品
		if(itemtype.equals("2")) {
			boolean r =this.addGiftToCart(Integer.valueOf( productid ), Integer.valueOf(num), sessionid);
			if(!r)return;
			
		}/*else if(itemtype.equals("3")){
			this.addGoupbuyToCart(Integer.valueOf( productid ), Integer.valueOf(num), sessionid);
			
		}*/else{//添加商品或捆绑促销
			this.addGoodsToCart(Integer.valueOf( productid ), Integer.valueOf(num), sessionid, Integer.valueOf(itemtype));
		}
		
		ThreadContextHolder.getHttpRequest().getSession().setAttribute("site_key", EopContext.getContext().getCurrentSite());
	}
	
	
	
	/**
	 * 向购物车中添加货品
	 */
	private void add(){
		
		String ajax = request.getParameter("ajax");
		if("yes".equals(ajax)){
			try{
				this.selfAdd();
				this.showSuccessJson("商品成功添加至购物车");
				return ;
			}catch(RuntimeException e){
				this.showErrorJson(e.getMessage());
				return ;
			}
		}else{
			try{
			this.selfAdd();
			this.list();
			}catch(RuntimeException e){
				this.showError(e.getMessage());
				return ;
			}
		}
	}
	
	
	/**
	 * 将商品或捆绑商品添加到购物
	 * @param productid
	 * @param num
	 * @param sessionid
	 */
	private void addGoodsToCart(Integer productid,Integer num,String sessionid,Integer itemtype){
		
		Product product =null;
		if( itemtype.intValue()==0){
			product  =this.productManager.get(productid);
		}
		
		if(itemtype.intValue()==1){
			product = productManager.getByGoodsId(productid);
		}
		
		if(itemtype.intValue()==4){
			product = productManager.getByGoodsId(productid);
			itemtype=0;
		}
		
		if(product!=null){
			
			if(product.getStore()==null || product.getStore()<num){
				throw new RuntimeException("抱歉！您所选选择的货品库存不足。");
			}
			Cart cart = new Cart();
			cart.setGoods_id(product.getGoods_id());
			cart.setProduct_id(product.getProduct_id());
			cart.setSession_id(sessionid);
			cart.setNum(num);
			cart.setItemtype(itemtype);
			cart.setWeight(product.getWeight());
			cart.setPrice( product.getPrice() );
			cart.setName(product.getName());
		 
			this.cartManager.add(cart);
			
		}
	}

	
	 
	
	/**
	 * 添加赠品至购物车
	 * @param giftid
	 * @param num
	 * @param sessionid
	 */
	private boolean addGiftToCart(Integer giftid,Integer num,String sessionid ){
		Member member = UserServiceFactory.getUserService().getCurrentMember();
		
		 if(member==null){
				try {
					this.showError("您尚未登录不能兑换赠品","点击此处到登录页面", "member_login.html?forward="+URLEncoder.encode("cart.html?action=add&productid="+giftid+"&itemtype=2","UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				return false;
			 }else{
				 FreeOffer freeOffer = this.freeOfferManager.get( Integer.valueOf(giftid ));
				if(  freeOffer.getLv_ids()==null || member.getLv_id()==null){
					 
					 this.showError("您的会员等级不能兑换此赠品","赠品列表","giftlist.html");
					 return false;
				}else{
					 String[] lvAr = freeOffer.getLv_ids().split(",");
					 if(!StringUtil.isInArray(member.getLv_id(), lvAr)){
						 this.showError("您的会员等级不能兑换此赠品","赠品列表","giftlist.html");
						 return false ;
					 }
				}
				if(member.getPoint().intValue()< freeOffer.getScore().intValue() ){
					 this.showError("您的积分不足，不能兑换此赠品","赠品列表","giftlist.html");
					 return false;
				 }
				Cart cart = new Cart();
				cart.setProduct_id(giftid);
				cart.setSession_id(sessionid);
				cart.setNum(num);
				cart.setItemtype(2);
				cart.setWeight(freeOffer.getWeight());
				cart.setName( freeOffer.getFo_name() );
				cart.setPrice(Double.valueOf( freeOffer.getScore()));
				this.cartManager.add(cart);
			 }
		 
		 	return true;

	 
	}
	
	/**
	 * 添加团购至购物车
 
	private boolean addGoupbuyToCart(Integer groupid,Integer num,String sessionid ){
		GroupBuy groupBuy = this.groupBuyManager.get(groupid);
		Cart cart = new Cart();
		cart.setProduct_id(groupid);
		cart.setSession_id(sessionid);
		cart.setNum(num);
		cart.setItemtype(3);
		cart.setWeight(groupBuy.getGoods().getWeight());
		cart.setName( groupBuy.getTitle() );
		cart.setPrice(  groupBuy.getPrice() );
		this.cartManager.add(cart);
		return true;
	}
	 */
	
	
	/**
	 * 读取购物车中货品列表
	 * v3.0:去掉了赠品、配件、捆绑、团购、限时抢购的读取
	 */
	private void list(){
		String sessionid = request.getSession().getId();
		List goodsList = cartManager.listGoods( sessionid ); //商品列表
 
		
		this.setPageName("cart");
		this.putData("goodsItemList", goodsList);
		
		this.putData("GoodsPic",new  ImageUrlDirectiveModel());
		
		Member member = UserServiceFactory.getUserService().getCurrentMember();
		if(member==null){
			this.putData("isLogin", false);
		}else{
			this.putData("isLogin", true);
		}
		
	 
	}
	
	/**
	 * 删除购物车中一项货品
	 */
	public void delete(){
		try{
			String cartid= request.getParameter("cartid");
			cartManager.delete(request.getSession().getId(), Integer.valueOf(cartid));
			this.showSuccessJson("删除成功");
		}catch(RuntimeException e){
			this.logger.error("删除购物项失败",e);
			this.showErrorJson("删除购物项失败");
		}
	 
	}
	
	public void clean(){	
		try{
			cartManager.clean(request.getSession().getId());
			this.showSuccessJson("清空购物车成功");
		}catch(RuntimeException e){
			 this.logger.error("清空购物车",e);
			 this.showErrorJson(e.getMessage());
		}
		 
	}
	
	/**
	 * 更新某个货品的数量
	 */
	public void updateNum(){
		String json;
		try{
			String cartid= request.getParameter("cartid");
			String num= request.getParameter("num");
			num = StringUtil.isEmpty(num)?"1":num;//lzf add 20110113
			String productid= request.getParameter("productid");
			Product product=productManager.get(Integer.valueOf(productid));
			Integer store=product.getStore();
			if(store==null)
				store=0;
			if(store >=Integer.valueOf(num)){
			cartManager.updateNum(request.getSession().getId(),  Integer.valueOf(cartid),  Integer.valueOf(num));
			}
			json="{\"result\":0,\"store\":"+store+"}";
		}catch(RuntimeException e){
			e.printStackTrace();
			json="{\"result\":1}";
		}
		this.putData("json", json);
		this.setPageName("cartJson");
	}
	
	/**
	 * 购物车的价格、优惠总计信息
	 */
	public void getTotal(){
		String sessionid  = request.getSession().getId();
	/*	Integer point  = this.cartManager.countPoint(sessionid);	
		//商品原始总价
		Double originalTotal  =cartManager.countGoodsTotal(request.getSession().getId());
		//计算捆绑商品的总价，并加入订单总价中
		Double pgkTotal = this.cartManager.countPgkTotal(sessionid);
		//计算团购总价
		Double groupBuyTotal = this.cartManager.countGroupBuyTotal(sessionid);
		
		
		//应用了商品优惠的优惠价格
		Double actualTotal = cartManager.countGoodsDiscountTotal(sessionid);
		
		Member member  = UserServiceFactory.getUserService().getCurrentMember();
		if(member!=null){
			List pmtList =promotionManager.list(actualTotal, member.getLv_id());
			this.putData("pmtList", pmtList);
			//应用的订单优惠的优惠价
			actualTotal= this.promotionManager.applyOrderPmt(actualTotal, 0D, point,member.getLv_id()).getOrderPrice();
		}
		
		
		
		originalTotal = CurrencyUtil.add(originalTotal,pgkTotal);	
		originalTotal = CurrencyUtil.add(originalTotal,groupBuyTotal);	
		
		actualTotal = CurrencyUtil.add(actualTotal,pgkTotal);	
		actualTotal = CurrencyUtil.add(actualTotal,groupBuyTotal);	
		
		this.putData("originalTotal", originalTotal);
		this.putData("actualTotal", actualTotal);*/
		
		OrderPrice orderprice  =this.cartManager.countPrice(cartManager.listGoods(sessionid), null, null);
		Member member  = UserServiceFactory.getUserService().getCurrentMember();
		if(member!=null){
			List pmtList =promotionManager.list(orderprice.getGoodsPrice(), member.getLv_id());
			this.putData("pmtList", pmtList);
		}
		 
		this.putData("orderPrice",orderprice) ;
		this.setActionPageName("cartTotal");
	}
	
	 
	public ICartManager getCartManager() {
		return cartManager;
	}

	public void setCartManager(ICartManager cartManager) {
		this.cartManager = cartManager;
	}

 
	
	public IPromotionManager getPromotionManager() {
		return promotionManager;
	}

	public void setPromotionManager(IPromotionManager promotionManager) {
		this.promotionManager = promotionManager;
	}

	
	
	public IFreeOfferManager getFreeOfferManager() {
		return freeOfferManager;
	}

	public void setFreeOfferManager(IFreeOfferManager freeOfferManager) {
		this.freeOfferManager = freeOfferManager;
	}

	public static void main(String args[]){

		  NumberFormat format = new DecimalFormat("#0.00"); 
		  double a = 1.09; double b = 0.03; 
		  double d = Double.valueOf(format.format(a + b));


	    }

	public IProductManager getProductManager() {
		return productManager;
	}

	public void setProductManager(IProductManager productManager) {
		this.productManager = productManager;
	}



	public IGnotifyManager getGnotifyManager() {
		return gnotifyManager;
	}


	public void setGnotifyManager(IGnotifyManager gnotifyManager) {
		this.gnotifyManager = gnotifyManager;
	}


 

}
