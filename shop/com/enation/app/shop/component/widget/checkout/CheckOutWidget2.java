package com.enation.app.shop.component.widget.checkout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MemberAddress;
import com.enation.app.shop.core.model.DlyType;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.model.support.OrderPrice;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.app.shop.core.service.IDlyTypeManager;
import com.enation.app.shop.core.service.IMemberAddressManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IPaymentManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.app.shop.core.service.IPromotionManager;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.eop.sdk.widget.AbstractWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.directive.ImageUrlDirectiveModel;
import com.enation.framework.util.StringUtil;
/**
 * 购物车checkout挂件
 * @author kingapex
 *2010-3-25下午06:05:19
 *
 *v2.0:在javashop3.0之时，重新优化代码-kingapex
 */
@Component
@Scope("prototype")
public class CheckOutWidget2 extends AbstractWidget {
	private HttpServletRequest  request;
	private IMemberAddressManager memberAddressManager;
	private IDlyTypeManager dlyTypeManager;
	protected ICartManager cartManager;
	private IPaymentManager paymentManager;
	private IOrderManager orderManager;
	private IPromotionManager promotionManager ;
	public IProductManager getProductManager() {
		return productManager;
	}



	public void setProductManager(IProductManager productManager) {
		this.productManager = productManager;
	}

	private IProductManager productManager ;
 
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
		Member member  = UserServiceFactory.getUserService().getCurrentMember();
		String sessionid =  request.getSession().getId();
	 
		if(action==null || action.equals("")){
			
			this.setPageName("checkout");
			
			if(member!=null){
				//读取此会员的收货地址列表
				List<MemberAddress> addressList =memberAddressManager.listAddress();
				MemberAddress defaultAddress = this.getDefaultAddress(addressList);
				
				
				this.putData("defaultAddress",defaultAddress);
				this.putData("addressList", addressList);
				this.putData("isLogin",true);
				
			}else{
				this.putData("addressList", new ArrayList());
				this.putData("isLogin",false);
			}
			
			
			//读取购物车中的商品列表
			List<CartItem> goodsItemList = cartManager.listGoods(sessionid);
			
			if(goodsItemList==null ||goodsItemList.isEmpty() ){
				
				this.showError("您的购物车中暂无商品", "首页", "index.html");
				return ;
			}else{
				Iterator<CartItem> it=goodsItemList.iterator();
				while(it.hasNext()){
					CartItem cartItem=it.next();
					Product product=productManager.get(cartItem.getProduct_id());
					if(product.getStore()==null || cartItem.getNum()>product.getStore()){
						this.showError("抱歉！您所选选择的货品("+product.getName()+")库存不足。");
						return ;
					}
				}
			}
			
			
			
			this.putData("goodsItemList", goodsItemList);
					
			this.putData("GoodsPic",new  ImageUrlDirectiveModel());
			 
			if(goodsItemList==null|| goodsItemList.isEmpty()){
				this.putData("hasGoods", false);
			}else{
				this.putData("hasGoods", true);
			}

		}
		
		
		if("showAddress".equals(action)){
			this.showAddress();
		}
		
		
		/**
		 * 获取支付列表的html
		 */
		if("shopPaymentList".equals(action)){
			this.showPaymentList();
		}
		
		
		//异步显示在配送范围的配送方式
		if("showDlyType".equals(action)){
		
			Double orderPrice = cartManager.countGoodsTotal(sessionid);
			Double weight = cartManager.countGoodsWeight(sessionid);
			
			List<DlyType> dlyTypeList = this.dlyTypeManager.list(weight, orderPrice, request.getParameter("regionid"));
			this.putData("dlyTypeList", dlyTypeList);
			this.setActionPageName("dlytype_list");
		}
		
		
		
		
		//显示订单价格信息
		if("showOrderTotal".equals(action)){
			this.showOrderTotal(sessionid);
		}
		
		
		
		//创建订单
		if("createOrder".equals(action)){
			try{
				Order order  = this.createOrder();
				this.showJson("{\"result\":1,\"ordersn\":"+order.getSn()+"}");
				
			}catch(RuntimeException e){
				this.showJson("{\"result\":0,\"message\":'"+e.getMessage()+"'}");
			}
		}
		
	}
	
	/**
	 * 获取某个收货地址的Json字串
	 */
	private void showAddress(){
		Integer addr_id = Integer.valueOf( request.getParameter("addr_id") );
		MemberAddress address = memberAddressManager.getAddress(addr_id);
		this.showJson(JSONObject.fromObject(address).toString());
	}
	
	
	
	
	
	/**
	 * 获取支付列表的html
	 */
	private void showPaymentList(){
		String iscod= request.getParameter("iscod"); //是否支持货到付款
		
		//读取支付方式列表
		List paymentList  = this.paymentManager.list();
		this.putData("paymentList", paymentList);
		
		this.setActionPageName("payment_list");
	}
	
	
	private  void showOrderTotal(String sessionid){
		Integer typeId = Integer.valueOf(request.getParameter("typeId") );
		String  regionId = request.getParameter("regionId");
		
		OrderPrice  orderPrice  = this.cartManager.countPrice(cartManager.listGoods(sessionid), typeId, regionId);
		
		this.putData("orderPrice",orderPrice);
		
		this.setActionPageName("checkout_total");		
	}
	
	
	
	
	/**
	 * 获取默认的收货地址
	 * @param addressList
	 * @return 
	 * 如果不存在默认收货地址返回第一个收货地址
	 * 如果不存在收货地址返回null
	 */
	private MemberAddress getDefaultAddress(List<MemberAddress> addressList){
		
		if(addressList!=null&& !addressList.isEmpty()){
			for(MemberAddress address : addressList){
				if( address.getDef_addr()!=null && address.getDef_addr().intValue()==1)
				{
					address.setDef_addr(1);
					return address;
				}
			}
			
			MemberAddress defAddress =addressList.get(0);
			defAddress.setDef_addr(1);
			return defAddress;
		}
			
		return null;
	}
	
	private Order createOrder(){
		////System.out.println("abc...");
		Integer shippingId = this.getIntParam("typeId");
		if(shippingId==null ) throw new RuntimeException("配送方式不能为空");
		
		Integer paymentId = this.getIntParam("paymentId");
		if(paymentId==null ) throw new RuntimeException("支付方式不能为空");
		
		Order order = new Order() ;
		order.setShipping_id(shippingId); //配送方式
		order.setPayment_id(paymentId);//支付方式
		Integer addressId = StringUtil.toInt(request.getParameter("addressId"), false);
		
		MemberAddress address = null;
	
		address = this.createAddress();	
 
		order.setShip_provinceid(address.getProvince_id());
		order.setShip_cityid(address.getCity_id());
		order.setShip_regionid(address.getRegion_id());
		
		order.setShip_addr(address.getAddr());
		order.setShip_mobile(address.getMobile());
		order.setShip_tel(address.getTel());
		order.setShip_zip(address.getZip());
		order.setShipping_area(address.getProvince()+"-"+ address.getCity()+"-"+ address.getRegion());
		order.setShip_name(address.getName());
		order.setRegionid(address.getRegion_id());
		
//		if (addressId.intValue()==0) {
		//新的逻辑：只要选中了“保存地址”，就会新增一条收货地址，即使数据完全没有修改
	 	if ("yes".equals(request.getParameter("saveAddress"))) {
			if (UserServiceFactory.getUserService().getCurrentMember() != null) {
					address.setAddr_id(null);
					this.memberAddressManager.addAddress(address);
			}
		 	}
//		}
 
		order.setShip_day(this.getStringParam("shipDay"));
		order.setShip_time(this.getStringParam("shipTime"));
		order.setRemark(this.getStringParam("remark"));
		return	this.orderManager.add(order, this.request.getSession().getId());
		
	}
	
	private MemberAddress createAddress(){
		
		MemberAddress address = new MemberAddress();
 

		String name = request.getParameter("shipName");
		address.setName(name);

		String tel = request.getParameter("shipTel");
		address.setTel(tel);

		String mobile = request.getParameter("shipMobile");
		address.setMobile(mobile);

		String province_id = request.getParameter("province_id");
		address.setProvince_id(Integer.valueOf(province_id));

		String city_id = request.getParameter("city_id");
		address.setCity_id(Integer.valueOf(city_id));

		String region_id = request.getParameter("region_id");
		address.setRegion_id(Integer.valueOf(region_id));

		String province = request.getParameter("province");
		address.setProvince(province);

		String city = request.getParameter("city");
		address.setCity(city);

		String region = request.getParameter("region");
		address.setRegion(region);

		String addr = request.getParameter("shipAddr");
		address.setAddr(addr);

		String zip = request.getParameter("shipZip");
		address.setZip(zip);
	
		return address;
	}
	
	private String getStringParam(String name){

		
		return request.getParameter(name);
	}
	
	private Integer getIntParam(String name){
		try{
		 return Integer.valueOf( request.getParameter(name) );
		}catch(RuntimeException e){
		 
			return null;
		}
	}

	public IMemberAddressManager getMemberAddressManager() {
		return memberAddressManager;
	}

	public void setMemberAddressManager(IMemberAddressManager memberAddressManager) {
		this.memberAddressManager = memberAddressManager;
	}

	public IDlyTypeManager getDlyTypeManager() {
		return dlyTypeManager;
	}

	public void setDlyTypeManager(IDlyTypeManager dlyTypeManager) {
		this.dlyTypeManager = dlyTypeManager;
	}

	public ICartManager getCartManager() {
		return cartManager;
	}

	public void setCartManager(ICartManager cartManager) {
		this.cartManager = cartManager;
	}

	public IPaymentManager getPaymentManager() {
		return paymentManager;
	}

	public void setPaymentManager(IPaymentManager paymentManager) {
		this.paymentManager = paymentManager;
	}

	public IOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}

	public IPromotionManager getPromotionManager() {
		return promotionManager;
	}

	public void setPromotionManager(IPromotionManager promotionManager) {
		this.promotionManager = promotionManager;
	}



	
}
