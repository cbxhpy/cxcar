package com.enation.app.shop.core.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.jdom.JDOMException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.base.core.service.auth.IPermissionManager;
import com.enation.app.base.core.service.auth.IRoleManager;
import com.enation.app.base.core.service.auth.impl.PermissionConfig;
import com.enation.app.shop.component.payment.plugin.weixin.config.WeixinConfig;
import com.enation.app.shop.component.payment.plugin.weixin.mppay.PayCommonUtil;
import com.enation.app.shop.component.payment.plugin.weixin.mppay.WeixinAuthUtil;
import com.enation.app.shop.component.payment.plugin.weixin.util.UtilCommon;
import com.enation.app.shop.component.payment.plugin.weixin.util.XMLUtil;
import com.enation.app.shop.core.model.DepotUser;
import com.enation.app.shop.core.model.DlyType;
import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.OrderItem;
import com.enation.app.shop.core.model.OrderLog;
import com.enation.app.shop.core.model.PayCfg;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.model.Promotion;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.model.support.OrderPrice;
import com.enation.app.shop.core.plugin.order.OrderPluginBundle;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.app.shop.core.service.IDepotManager;
import com.enation.app.shop.core.service.IDlyTypeManager;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.app.shop.core.service.IOrderAllocationManager;
import com.enation.app.shop.core.service.IOrderFlowManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IPaymentManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.app.shop.core.service.IPromotionManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.app.shop.core.utils.DetailUtil;
import com.enation.eop.processor.httpcache.HttpCacheManager;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.user.IUserService;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.eop.sdk.utils.DateUtil;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.DoubleMapper;
import com.enation.framework.database.Page;
import com.enation.framework.database.StringMapper;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.ExcelUtil;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.StringUtil;
import com.enation.framework.util.WxpubOAuth;

import net.sf.json.JSONObject;

/**
 * 订单管理
 * 
 * @author kingapex 2010-4-6上午11:16:01
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class OrderManager extends BaseSupport implements IOrderManager {
	
	/*private static String WX_GATEWAY_NEW = "https://api.mch.weixin.qq.com/pay/unifiedorder";// 请求地址
	private static String APP_SCRECT = "a60461bcc0c7c58570ef983d4644c446";// APP_SCRECT
	private static String APP_KEY = "quwei7575quwei7575quwei7575quwei";// APP_KEY
	private static String MCH_ID = "1337386301";// 微信支付商户号
	private static String APPID = "wxc7266bd6d5cae9d6";// APPID
*/
	/*private static String WX_GATEWAY_NEW = "https://api.mch.weixin.qq.com/pay/unifiedorder";// 请求地址
	private static String APP_SCRECT = "b9d0f6fcf359651efcda0a153d51b833";// APP_SCRECT b9d0f6fcb9d0f6fcb9d0f6fcb9d0f6fc
	private static String APP_KEY = "b9d0f6fcb9d0f6fcb9d0f6fcb9d0f6fc";// APP_KEY b9d0f6fcb9d0f6fcb9d0f6fcb9d0f6fc
	private static String MCH_ID = "1412289302";// 微信支付商户号 1412289302
	private static String APPID = "wx82ef11456975364b";// APPID wx82ef11456975364b
*/
	
	private static String WX_GATEWAY_NEW = "https://api.mch.weixin.qq.com/pay/unifiedorder";// 请求地址
	private static String APP_SCRECT = "3e564676df0b22d8dbe7bd1d8e7cb92d";// APP_SCRECT b9d0f6fcb9d0f6fcb9d0f6fcb9d0f6fc
	private static String APP_KEY = "3qvpgwqd5qktvv0kjdd5g1chpiywm3qm";// APP_KEY b9d0f6fcb9d0f6fcb9d0f6fcb9d0f6fc
	private static String MCH_ID = "1486511892";// 微信支付商户号 1486511892 
	private static String APPID = "wxf8f7c89d3b50620d";// APPID wx82ef11456975364b

	private static String WX_REFUNd = "https://api.mch.weixin.qq.com/secapi/pay/refund";//退款地址

	private ICartManager cartManager;
	private IDlyTypeManager dlyTypeManager;
	private IPaymentManager paymentManager;
	private IPromotionManager promotionManager;
	private OrderPluginBundle orderPluginBundle;
	private IPermissionManager permissionManager;
	private IAdminUserManager adminUserManager;
	private IRoleManager roleManager;
	private IGoodsManager goodsManager;
	private IOrderAllocationManager orderAllocationManager;
	private IDepotManager depotManager;
	private IProductManager ProductManager;
	private IMemberManager memberManager;
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void savePrice(double price, int orderid) {
		Order order = this.get(orderid);
		double amount = order.getOrder_amount();
		// double discount= amount-price;
		double discount = CurrencyUtil.sub(amount, price);
		this.baseDaoSupport.execute(
				"update order set order_amount=?,need_pay_money=? where order_id=?", price,price,
				orderid);
		//修改收款单价格 
		String sql="update es_payment_logs set money=? where order_id=?";
		this.daoSupport.execute(sql, price,orderid);
		
		this.baseDaoSupport.execute(
				"update order set discount=discount+? where order_id=?",
				discount, orderid);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public double saveShipmoney(double shipmoney, int orderid) {
		Order order = this.get(orderid);
		double currshipamount = order.getShipping_amount();
		// double discount= amount-price;
		double  shortship = CurrencyUtil.sub(shipmoney, currshipamount);
		double discount = CurrencyUtil.sub(currshipamount, shipmoney);
		//2014-9-18 配送费用修改 @author LiFenLong
		this.baseDaoSupport.execute(
				"update order set order_amount=order_amount+?,need_pay_money=need_pay_money+?,shipping_amount=?,discount=discount+? where order_id=?", shortship,shortship,shipmoney,discount,
				orderid);
		//2014-9-12 LiFenLong 修改配送金额同时修改收款单
		this.daoSupport.execute("update es_payment_logs set money=money+? where order_id=?",shortship,orderid);
		return this.get(orderid).getOrder_amount();
	}
	
	/**
	 * 记录订单操作日志
	 * 
	 * @param order_id
	 * @param message
	 * @param op_id
	 * @param op_name
	 */
	@Override
	public void log(Integer order_id, String message, Integer op_id,
			String op_name) {
		OrderLog orderLog = new OrderLog();
		orderLog.setMessage(message);
		orderLog.setOp_id(op_id);
		orderLog.setOp_name(op_name);
		orderLog.setOp_time(com.enation.framework.util.DateUtil.getDatelineLong());
		orderLog.setOrder_id(order_id);
		this.baseDaoSupport.insert("order_log", orderLog);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public Order appAddOrder(Order order, String serve_id, String num) {
		
		String opname = "游客";

		if(order == null){
			throw new RuntimeException("订单不存在");
		}
		
		/************************** 用户信息 ****************************/
		Member member = this.memberManager.get(order.getMember_id());
		// 非匿名购买
		if (member != null) {
			order.setMember_id(member.getMember_id());
			opname = member.getUname();
		}

		long time_now = System.currentTimeMillis();
		
		Goods goods = this.goodsManager.getGoodsByServeId(serve_id);
		Product product = this.ProductManager.getByGoodsId(Integer.parseInt(serve_id));
		
		if(goods == null || product ==null){
			throw new RuntimeException("服务不存在");
		}
		
		OrderPrice orderPrice = new OrderPrice();
		Double goods_amount = CurrencyUtil.mul(goods.getPrice(), Double.valueOf(num));
		
		order.setGoods_amount(goods_amount);
		order.setDiscount(0.0);
		order.setOrder_amount(goods_amount);
		order.setProtect_price(0.0);
		order.setShipping_amount(0.0);
		order.setGainedpoint(0);
		//2014年0417新增订单还需要支付多少钱的字段-LiFenLong
		order.setNeed_pay_money(goods_amount);
		
		order.setSeller_id(goods.getSeller_id());
		/**************************计算价格、重量、积分****************************/
		// 配送方式名称
		order.setShipping_type("门店服务");

		/************ 支付方式价格及名称 ************************/
		order.setPaymoney(this.paymentManager.countPayPrice(order.getOrder_id()));
		order.setPayment_name(DetailUtil.getPayType(order.getPayment_id()));
		
		/************ 创建订单 ************************/
		order.setCreate_time(time_now/1000);
		order.setSn(this.createSn(order.getMember_id().toString()));
		order.setStatus(OrderStatus.ORDER_NOT_PAY);
		order.setDisabled(0);
		order.setPay_status(OrderStatus.PAY_NO);
		order.setShip_status(OrderStatus.SHIP_NO);
		order.setOrderStatus("订单已生效");
		
		/************ 写入订单货物列表 ************************/
		//this.orderPluginBundle.onBeforeCreate(order,itemList, sessionid);
		this.baseDaoSupport.insert("order", order);

		Integer orderId = this.baseDaoSupport.getLastId("order");

		order.setOrder_id(orderId);
		this.saveServeItem(goods, product, order, num);

		/**
		 * 应用订单优惠，送出优惠劵及赠品，并记录订单优惠方案
		 */
		/*if (member != null) {
			this.promotionManager.applyOrderPmt(orderId,
					orderPrice.getOrderPrice(), member.getLv_id());
			List<Promotion> pmtList = promotionManager.list(
					orderPrice.getOrderPrice(), member.getLv_id());
			for (Promotion pmt : pmtList) {
				String sql = "insert into order_pmt(pmt_id,order_id,pmt_describe)values(?,?,?)";
				this.baseDaoSupport.execute(sql, pmt.getPmt_id(), orderId,
						pmt.getPmt_describe());
			}

		}*/

		/************ 写入订单日志 ************************/
		OrderLog log = new OrderLog();
		log.setMessage("订单创建");
		log.setOp_name(opname);
		log.setOrder_id(orderId);
		this.addLog(log);
		order.setOrder_id(orderId);
		order.setOrderprice(orderPrice);
		/*try {
			this.orderPluginBundle.onAfterCreate(order,itemList, sessionid);
		} catch (Exception e) {
			System.out.println(e);
		}*/
		
		//因为在orderFlowManager中已经注入了orderManager，不能在这里直接注入
		//将来更好的办法是将订单创建移到orderFlowManager中
		//下单则自动改为已确认
		IOrderFlowManager flowManager = SpringContextHolder.getBean("orderFlowManager");
		flowManager.confirmOrder(orderId);
	
		return order;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Order appAdd(Order order, String cart_ids) {
		
		String opname = "游客";

		if (order == null)
			throw new RuntimeException("订单不存在");

		/************************** 用户信息 ****************************/
		IUserService userService = UserServiceFactory.getUserService();
		Member member = userService.getCurrentMember();
		// 非匿名购买
		if (member != null) {
			order.setMember_id(member.getMember_id());
			opname = member.getUname();
		}

		long time_now = System.currentTimeMillis();
		
		/**************************计算价格、重量、积分****************************/
		List<CartItem> cartItemList = this.cartManager.listCartByCartIdsPrice(cart_ids);
		OrderPrice orderPrice = new OrderPrice();
		if(cartItemList != null && cartItemList.size() != 0){
			orderPrice = this.cartManager.appCountPrice(cart_ids, null, null, null);
		}else{
			throw new RuntimeException("请选择商品");
		}
		
		int num_all = 0;
		for(CartItem cartItem : cartItemList){
			int num = cartItem.getNum();
			Product product = this.ProductManager.get(cartItem.getProduct_id());
			if(product.getEnable_store() < num){
				throw new RuntimeException(product.getName()+"库存不足");
			}
			num_all += num;
		}
		if(num_all < 20){
			throw new RuntimeException("亲，您的购买数量还达不到最低起订要求~请凑够20扎哦~");
		}
		
		order.setGoods_amount(orderPrice.getGoodsPrice());
		order.setWeight(orderPrice.getWeight());		

		order.setDiscount(orderPrice.getDiscountPrice());
		order.setOrder_amount(orderPrice.getOrderPrice());
		order.setProtect_price(orderPrice.getProtectPrice());
		order.setShipping_amount(orderPrice.getShippingPrice());
		order.setGainedpoint(orderPrice.getPoint());
		//2014年0417新增订单还需要支付多少钱的字段-LiFenLong
		order.setNeed_pay_money(orderPrice.getNeedPayMoney());
		
		// 配送方式名称
		order.setShipping_type("商家承担");

		/************ 支付方式价格及名称 ************************/
		order.setPaymoney(this.paymentManager.countPayPrice(order.getOrder_id()));
		order.setPayment_name(DetailUtil.getPayType(order.getPayment_id()));
		
		/************ 创建订单 ************************/
		order.setCreate_time(time_now/1000);
		order.setSn(this.createSn(order.getMember_id().toString()));
		order.setStatus(OrderStatus.ORDER_NOT_CONFIRM);
		order.setDisabled(0);
		order.setPay_status(OrderStatus.PAY_NO);
		order.setShip_status(OrderStatus.SHIP_NO);
		order.setOrderStatus("订单已生效");
		
		//给订单添加仓库 ------仓库为默认仓库
		Integer depotId = this.baseDaoSupport.queryForInt("select id from depot where choose=1");
		order.setDepotid(depotId);
		
		/************ 写入订单货物列表 ************************/
		List<CartItem> itemList = cartItemList;
		
		//this.orderPluginBundle.onBeforeCreate(order,itemList, sessionid);
		this.baseDaoSupport.insert("order", order);

		this.cartManager.clean(cart_ids);
		
		if(itemList.isEmpty()){
			throw new RuntimeException("创建订单失败，购物车为空");
		}

		Integer orderId = this.baseDaoSupport.getLastId("order");

		order.setOrder_id(orderId);
		this.saveGoodsItem(itemList, order);

		/**
		 * 应用订单优惠，送出优惠劵及赠品，并记录订单优惠方案
		 */
		/*if (member != null) {
			this.promotionManager.applyOrderPmt(orderId,
					orderPrice.getOrderPrice(), member.getLv_id());
			List<Promotion> pmtList = promotionManager.list(
					orderPrice.getOrderPrice(), member.getLv_id());
			for (Promotion pmt : pmtList) {
				String sql = "insert into order_pmt(pmt_id,order_id,pmt_describe)values(?,?,?)";
				this.baseDaoSupport.execute(sql, pmt.getPmt_id(), orderId,
						pmt.getPmt_describe());
			}

		}*/

		/************ 写入订单日志 ************************/
		OrderLog log = new OrderLog();
		log.setMessage("订单创建");
		log.setOp_name(opname);
		log.setOrder_id(orderId);
		this.addLog(log);
		order.setOrder_id(orderId);
		order.setOrderprice(orderPrice);
		/*try {
			this.orderPluginBundle.onAfterCreate(order,itemList, sessionid);
		} catch (Exception e) {
			System.out.println(e);
		}*/
		
		//因为在orderFlowManager中已经注入了orderManager，不能在这里直接注入
		//将来更好的办法是将订单创建移到orderFlowManager中
		//下单则自动改为已确认
		IOrderFlowManager flowManager = SpringContextHolder.getBean("orderFlowManager");
		flowManager.confirmOrder(orderId);
	
		return order;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Order add(Order order, String sessionid) {
		String opname = "游客";

		if (order == null)
			throw new RuntimeException("error: order is null");

		/************************** 用户信息 ****************************/
		IUserService userService = UserServiceFactory.getUserService();
		Member member = userService.getCurrentMember();
		// 非匿名购买
		if (member != null) {
			order.setMember_id(member.getMember_id());
			opname = member.getUname();
		}

		
		/**************************计算价格、重量、积分****************************/
		List<CartItem> cartItemList=cartManager.listGoods(sessionid);
		OrderPrice orderPrice = cartManager.countPrice(cartItemList, order.getShipping_id(),""+order.getRegionid());
		
		order.setGoods_amount( orderPrice.getGoodsPrice());
		order.setWeight(orderPrice.getWeight());		

		order.setDiscount(orderPrice.getDiscountPrice());
		order.setOrder_amount(orderPrice.getOrderPrice());
		order.setProtect_price(orderPrice.getProtectPrice());
		order.setShipping_amount(orderPrice.getShippingPrice());
		order.setGainedpoint(orderPrice.getPoint());
		//2014年0417新增订单还需要支付多少钱的字段-LiFenLong
		order.setNeed_pay_money(orderPrice.getNeedPayMoney());
		// 配送方式名称
		DlyType dlyType = new DlyType();
		if (dlyType != null && order.getShipping_id()!=0){
			dlyType = dlyTypeManager.getDlyTypeById(order.getShipping_id());
		}else{
			dlyType.setName("卖家承担运费！");
		}
		/*order.setShipping_type(dlyType.getName());
		order.setShipping_id(dly_typeid);
		DlyType dlyType = dlyTypeManager.getDlyTypeById(order.getShipping_id());
		if (dlyType == null)
			throw new RuntimeException("shipping not found count error");*/
		order.setShipping_type(dlyType.getName());

		/************ 支付方式价格及名称 ************************/
		PayCfg payCfg = this.paymentManager.get(order.getPayment_id());
		order.setPaymoney(this.paymentManager.countPayPrice(order.getOrder_id()));
		order.setPayment_name(payCfg.getName());
		
		order.setPayment_type(payCfg.getType());

		/************ 创建订单 ************************/
		order.setCreate_time(com.enation.framework.util.DateUtil.getDatelineLong());
		
		order.setSn(this.createSn());
		order.setStatus(OrderStatus.ORDER_NOT_CONFIRM);
		order.setDisabled(0);
		order.setPay_status(OrderStatus.PAY_NO);
		order.setShip_status(OrderStatus.SHIP_NO);
		order.setOrderStatus("订单已生效");
		
		//给订单添加仓库 ------仓库为默认仓库
		Integer depotId= this.baseDaoSupport.queryForInt("select id from depot where choose=1");
		order.setDepotid(depotId);
		/************ 写入订单货物列表 ************************/
		List<CartItem> itemList = this.cartManager.listGoods(sessionid);
		
		this.orderPluginBundle.onBeforeCreate(order,itemList, sessionid);
		this.baseDaoSupport.insert("order", order);

		if (itemList.isEmpty()){
			throw new RuntimeException("创建订单失败，购物车为空");
		}

		Integer orderId = this.baseDaoSupport.getLastId("order");

		order.setOrder_id(orderId);
		this.saveGoodsItem(itemList, order);

		/**
		 * 应用订单优惠，送出优惠劵及赠品，并记录订单优惠方案
		 */
		if (member != null) {
			this.promotionManager.applyOrderPmt(orderId,
					orderPrice.getOrderPrice(), member.getLv_id());
			List<Promotion> pmtList = promotionManager.list(
					orderPrice.getOrderPrice(), member.getLv_id());
			for (Promotion pmt : pmtList) {
				String sql = "insert into order_pmt(pmt_id,order_id,pmt_describe)values(?,?,?)";
				this.baseDaoSupport.execute(sql, pmt.getPmt_id(), orderId,
						pmt.getPmt_describe());
			}

		}

		/************ 写入订单日志 ************************/
		OrderLog log = new OrderLog();
		log.setMessage("订单创建");
		log.setOp_name(opname);
		log.setOrder_id(orderId);
		this.addLog(log);
		order.setOrder_id(orderId);
		order.setOrderprice(orderPrice);
		try {
			this.orderPluginBundle.onAfterCreate(order,itemList, sessionid);
		} catch (Exception e) {
			System.out.println(e);
		}
		
		//因为在orderFlowManager中已经注入了orderManager，不能在这里直接注入
		//将来更好的办法是将订单创建移到orderFlowManager中
		//下单则自动改为已确认
		IOrderFlowManager flowManager = SpringContextHolder.getBean("orderFlowManager");
		flowManager.confirmOrder(orderId);
		cartManager.clean(sessionid);
		HttpCacheManager.sessionChange();
	
		return order;
	}

	/**
	 * 添加订单日志
	 * @param log
	 */
	private void addLog(OrderLog log) {
		log.setOp_time(com.enation.framework.util.DateUtil.getDatelineLong());
		this.baseDaoSupport.insert("order_log", log);
	}

	/**
	 * 保存服务订单项
	 * @param goods
	 * @param product
	 * @param order
	 */
	private void saveServeItem(Goods goods, Product product, Order order, String num) {
		Integer order_id = order.getOrder_id();

		OrderItem orderItem = new OrderItem();

		orderItem.setPrice(goods.getPrice());
		orderItem.setName(goods.getName());
		orderItem.setNum(Integer.parseInt(num));

		orderItem.setGoods_id(goods.getGoods_id());
		orderItem.setShip_num(0);
		orderItem.setProduct_id(product.getProduct_id());
		orderItem.setOrder_id(order_id);
		orderItem.setGainedpoint(0);
		//orderItem.setAddon("");
		
		//3.0新增的三个字段
		orderItem.setSn(goods.getSn());
		orderItem.setImage(goods.getThumbnail());
		orderItem.setCat_id(goods.getCat_id());
		
		//orderItem.setUnit(cartItem.getUnit());
		
		this.baseDaoSupport.insert("order_items", orderItem);
		int itemid = this.baseDaoSupport.getLastId("order_items");
		//orderItem.setItem_id(itemid);
		
		//this.orderPluginBundle.onItemSave(order, orderItem);
	}
	
	/**
	 * 保存商品订单项
	 * @param itemList
	 * @param order_id
	 */
	private void saveGoodsItem(List<CartItem> itemList, Order order) {
		Integer order_id = order.getOrder_id();
		for (int i = 0; i < itemList.size(); i++) {

			OrderItem orderItem = new OrderItem();

			CartItem cartItem = itemList.get(i);
			orderItem.setPrice(cartItem.getCoupPrice());
			orderItem.setName(cartItem.getName());
			orderItem.setNum(cartItem.getNum());

			orderItem.setGoods_id(cartItem.getGoods_id());
			orderItem.setShip_num(0);
			orderItem.setProduct_id(cartItem.getProduct_id());
			orderItem.setOrder_id(order_id);
			orderItem.setGainedpoint(cartItem.getPoint());
			orderItem.setAddon(cartItem.getAddon());
			
			//3.0新增的三个字段
			orderItem.setSn(cartItem.getSn());
			orderItem.setImage(cartItem.getImage_default());
			orderItem.setCat_id(cartItem.getCatid());
			
			orderItem.setUnit(cartItem.getUnit());
			
			this.baseDaoSupport.insert("order_items", orderItem);
			int itemid = this.baseDaoSupport.getLastId("order_items");
			orderItem.setItem_id(itemid);
			this.orderPluginBundle.onItemSave(order, orderItem);
		}
	}

	/**
	 * 保存赠品项
	 * @param itemList
	 * @param orderid
	 * @throws IllegalStateException
	 * 会员尚未登录,不能兑换赠品!
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	private void saveGiftItem(List<CartItem> itemList, Integer orderid) {
		Member member = UserServiceFactory.getUserService().getCurrentMember();
		if (member == null) {
			throw new IllegalStateException("会员尚未登录,不能兑换赠品!");
		}

		int point = 0;
		for (CartItem item : itemList) {
			point += item.getSubtotal().intValue();
			this.baseDaoSupport
					.execute(
							"insert into order_gift(order_id,gift_id,gift_name,point,num,shipnum,getmethod)values(?,?,?,?,?,?,?)",
							orderid, item.getProduct_id(), item.getName(),
							item.getPoint(), item.getNum(), 0, "exchange");
		}
		if (member.getPoint().intValue() < point) {
			throw new IllegalStateException("会员积分不足,不能兑换赠品!");
		}
		member.setPoint(member.getPoint() - point); // 更新session中的会员积分
		this.baseDaoSupport.execute(
				"update member set point=? where member_id=? ",
				member.getPoint(), member.getMember_id());

	}

	@Override
	public Page listbyshipid(int pageNo, int pageSize, int status,
			int shipping_id,String sort, String order) {
		order = " ORDER BY "+sort+" "+order;
		String sql = "select * from order where disabled=0 and status="
				+ status + " and shipping_id= " + shipping_id;
		sql += " order by " + order;
		Page page = this.baseDaoSupport.queryForPage(sql, pageNo, pageSize,
				Order.class);
		return page;
	}

	@Override
	public Page listConfirmPay(int pageNo, int pageSize,String sort, String order) {
		order = " order_id";
		String sql = "select * from order where disabled=0 and ((status = "
				+ OrderStatus.ORDER_SHIP + " and payment_type = 'cod') or status= "
				+ OrderStatus.ORDER_PAY + "  )";
		sql += " order by " + order;
		//System.out.println(sql);
		Page page = this.baseDaoSupport.queryForPage(sql, pageNo, pageSize,Order.class);
		return page;
	}

	@Override
	public Order get(Integer orderId) {
		String sql = "select * from order where order_id=?";
		Order order = (Order) this.baseDaoSupport.queryForObject(sql,
				Order.class, orderId);
		return order;
	}
	
	@Override
	public Order get(String ordersn) {
		String sql  ="select * from es_order where sn='"+ordersn+"'";
		Order order  = (Order)this.baseDaoSupport.queryForObject(sql, Order.class);
		return order;
		 
	}
	
	@Override
	public List<OrderItem> listGoodsItems(Integer orderId) {

		String sql = "select * from " + this.getTableName("order_items");
		sql += " where order_id = ?";
		List<OrderItem > itemList = this.daoSupport.queryForList(sql, OrderItem.class, orderId);
		this.orderPluginBundle.onFilter(orderId, itemList);
		return itemList;
	}
	
	public List listGiftItems(Integer orderId) {
		String sql = "select * from order_gift where order_id=?";
		return this.baseDaoSupport.queryForList(sql, orderId);
	} 

	/**
	 * 读取订单日志
	 */
	@Override
	public List listLogs(Integer orderId) {
		String sql = "select * from order_log where order_id=?";
		return this.baseDaoSupport.queryForList(sql, orderId);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void clean(Integer[] orderId) {
		String ids = StringUtil.arrayToString(orderId, ",");
		String sql = "delete from order where order_id in (" + ids + ")";
		this.baseDaoSupport.execute(sql);

		sql = "delete from order_items where order_id in (" + ids + ")";
		this.baseDaoSupport.execute(sql);

		sql = "delete from order_log where order_id in (" + ids + ")";
		this.baseDaoSupport.execute(sql);

		sql = "delete from payment_logs where order_id in (" + ids + ")";
		this.baseDaoSupport.execute(sql);

		sql = "delete from " + this.getTableName("delivery_item")
				+ " where delivery_id in (select delivery_id from "
				+ this.getTableName("delivery") + " where order_id in (" + ids
				+ "))";
		this.daoSupport.execute(sql);

		sql = "delete from delivery where order_id in (" + ids + ")";
		this.baseDaoSupport.execute(sql);
		
		orderAllocationManager.clean(orderId);
		
		/**
		 * -------------------
		 * 激发订单的删除事件
		 * -------------------
		 */
		this.orderPluginBundle.onDelete(orderId);

	}
	
	private boolean exec(Integer[] orderId, int disabled) {
		if(cheack(orderId)){
			String ids = StringUtil.arrayToString(orderId, ",");
			String sql = "update order set disabled = ? where order_id in (" + ids
					+ ")";
			this.baseDaoSupport.execute(sql, disabled);
			return true;
		}else{
			return false;
		}
	}
	
	private boolean cheack(Integer[] orderId){
		boolean i=true;
		for (int j = 0; j < orderId.length; j++) {
			if(this.baseDaoSupport.queryForInt("select status from es_order where order_id=?",orderId[j])!=OrderStatus.ORDER_CANCELLATION){
				i=false;
			}
		}
		return i;
	}
	
	@Override
	public boolean delete(Integer[] orderId) {
		return exec(orderId, 1);

	}

	@Override
	public void revert(Integer[] orderId) {
		exec(orderId, 0);
	}

	public String createSn(String member_id) {
		/*Date now = new Date();
		String sn = com.enation.framework.util.DateUtil.toString(now,
				"yyyyMMddHHmmssSSS");
		sn += member_id;
		return sn;*/
		Date now = new Date();
		String str_4 = ((int)(Math.random()*(9999-1000+1))+1000)+"";
		String sn = com.enation.framework.util.DateUtil.toString(now,
				"yyyyMMddHHmmssSSS")+str_4;
		return sn;
	}
	
	@Override
	public String createSn() {
		String orderNum="00000"+(getOrderNumByMonth()+1);
		Date now = new Date();
		/**2014-9-16 @author LiFenLong 更改订单号为按月份订单数排序 **/
		String sn = com.enation.framework.util.DateUtil.toString(now,
				"yyyyMMdd"+orderNum.substring(orderNum.length()-6, orderNum.length()));
		return sn;
	}
	
	/**
	 * 获取当月的订单数
	 * @author LiFenLong
	 * @return 当月订单数
	 */
	private Integer getOrderNumByMonth(){
		String[] dateString =com.enation.framework.util.DateUtil.getCurrentMonth();
		String sql="select count(sn) from es_order where create_time>? and create_time<?";
		return  this.daoSupport.queryForInt(sql,com.enation.framework.util.DateUtil.getDatelineLong(dateString[0]) ,com.enation.framework.util.DateUtil.getDatelineLong(dateString[1]));
	}

	public ICartManager getCartManager() {
		return cartManager;
	}

	public void setCartManager(ICartManager cartManager) {
		this.cartManager = cartManager;
	}

	public IDlyTypeManager getDlyTypeManager() {
		return dlyTypeManager;
	}

	public void setDlyTypeManager(IDlyTypeManager dlyTypeManager) {
		this.dlyTypeManager = dlyTypeManager;
	}

	public IPaymentManager getPaymentManager() {
		return paymentManager;
	}

	public void setPaymentManager(IPaymentManager paymentManager) {
		this.paymentManager = paymentManager;
	}

	@Override
	public Page listOrderByMemberId(int page_no, int page_size, Integer member_id) {
		String sql = "select * from order where member_id = ? order by create_time desc";
		Page page = this.baseDaoSupport.queryForPage(sql, page_no, page_size, Order.class, member_id);
		return page;
	}

	@Override
	public Map mapOrderByMemberId(int memberId) {
		Integer buyTimes = this.baseDaoSupport.queryForInt(
				"select count(0) from order where member_id = ?", memberId);
		Double buyAmount = (Double) this.baseDaoSupport.queryForObject(
				"select sum(paymoney) from order where member_id = ?",
				new DoubleMapper(), memberId);
		Map map = new HashMap();
		map.put("buyTimes", buyTimes);
		map.put("buyAmount", buyAmount);
		return map;
	}

	public IPromotionManager getPromotionManager() {
		return promotionManager;
	}

	public void setPromotionManager(IPromotionManager promotionManager) {
		this.promotionManager = promotionManager;
	}

	@Override
	public void edit(Order order) {
		this.baseDaoSupport.update("order", order,
				"order_id = " + order.getOrder_id());

	}

	@Override
	public List<Map> listAdjItem(Integer orderid) {
		String sql = "select * from order_items where order_id=? and addon!=''";
		return this.baseDaoSupport.queryForList(sql, orderid);
	}

	/**
	 * 统计订单状态
	 */
	@Override
	public Map censusState() {

		// 构造一个返回值Map，并将其初始化：各种订单状态的值皆为0
		Map<String, Integer> stateMap = new HashMap<String, Integer>(7);
		String[] states = { "cancel_ship", "cancel_pay",  "pay","ship", "complete", "allocation_yes" };
		for (String s : states) {
			stateMap.put(s, 0);
		}

		// 分组查询、统计订单状态
		String sql = "select count(0) num,status  from "
				+ this.getTableName("order")
				+ " where disabled = 0 group by status";
		List<Map<String, Integer>> list = this.daoSupport.queryForList(sql,
				new RowMapper() {

					@Override
					public Object mapRow(ResultSet rs, int arg1) throws SQLException {
						Map<String, Integer> map = new HashMap<String, Integer>();
						map.put("status", rs.getInt("status"));
						map.put("num", rs.getInt("num"));
						return map;
					}
				});
//
//		// 将list转为map
		for (Map<String, Integer> state : list) {
			stateMap.put(this.getStateString(state.get("status")),state.get("num"));
		}
		
		sql = "select count(0) num  from " + this.getTableName("order") + " where disabled = 0  and status=0 ";
		int count=this.daoSupport.queryForInt(sql);
		stateMap.put("wait", 0);
		
		sql = "select count(0) num  from " + this.getTableName("order") + " where disabled = 0  ";
		sql+=" and ( ( payment_type!='cod' and  status="+OrderStatus.ORDER_NOT_PAY +") ";//非货到付款的，未付款状态的可以结算
		sql+=" or ( payment_id=8 and status!="+OrderStatus.ORDER_NOT_PAY+"  and  pay_status!="+OrderStatus.PAY_CONFIRM +")" ; 
		sql+=" or ( payment_type='cod' and  (status="+OrderStatus.ORDER_SHIP +" or status="+OrderStatus.ORDER_ROG+" )  ) )";//货到付款的要发货或收货后才能结算
		count=this.daoSupport.queryForInt(sql);
		stateMap.put("not_pay", count);
		
		sql="select count(0) from es_order where disabled=0  and ( ( payment_type!='cod' and payment_id!=8  and  status=2)  or ( payment_type='cod' and  status=0))";
		count=this.baseDaoSupport.queryForInt(sql);
		stateMap.put("allocation_yes", count);
		
		return stateMap;
	}

	/**
	 * 根据订单状态值获取状态字串，如果状态值不在范围内反回null。
	 * 
	 * @param state
	 * @return
	 */
	private String getStateString(Integer state) {
		String str = null;
		switch (state.intValue()) {
		case -2:
			str = "cancel_ship";
			break;
		case -1:
			str = "cancel_pay";
			break;
		case 1:
			str = "pay";
			break;
		case 2:
			str = "ship";
			break;
		case 4:
			str = "allocation_yes";
			break;
		case 7:
			str = "complete";
			break;
		default:
			str = null;
			break;
		}
		return str;
	}


	public OrderPluginBundle getOrderPluginBundle() {
		return orderPluginBundle;
	}

	public void setOrderPluginBundle(OrderPluginBundle orderPluginBundle) {
		this.orderPluginBundle = orderPluginBundle;
	}

	@Override
	public String export(Date start, Date end) {
		String sql  ="select * from order where disabled=0 ";
		if(start!=null){
			sql+=" and create_time>"+ start.getTime();
		}
		
		if(end!=null){
			sql+="  and create_timecreate_time<"+ end.getTime();
		}
		
		List<Order> orderList  = this.baseDaoSupport.queryForList(sql,Order.class);
		
		
		//使用excel导出流量报表
		ExcelUtil excelUtil = new ExcelUtil(); 
		
		//流量报表excel模板在类包中，转为流给excelutil
		InputStream in =FileUtil.getResourceAsStream("com/enation/app/shop/core/service/impl/order.xls");
		
		excelUtil.openModal( in );
		int i=1;
		for(Order order :orderList){
			
			excelUtil.writeStringToCell(i, 0,order.getSn()); //订单号
			excelUtil.writeStringToCell(i, 1,DateUtil.toString(new Date(order.getCreate_time()), "yyyy-MM-dd HH:mm:ss")  ); //下单时间
			excelUtil.writeStringToCell(i, 2,order.getOrderStatus() ); //订单状态
			excelUtil.writeStringToCell(i, 3,""+order.getOrder_amount() ); //订单总额
			excelUtil.writeStringToCell(i, 4,order.getShip_name() ); //收货人
			excelUtil.writeStringToCell(i, 5,order.getPayStatus()); //付款状态
			excelUtil.writeStringToCell(i, 6,order.getShipStatus()); //发货状态
			excelUtil.writeStringToCell(i, 7,order.getShipping_type()); //配送方式
			excelUtil.writeStringToCell(i, 8,order.getPayment_name()); //支付方式
			i++;
		}
		//String target= EopSetting.IMG_SERVER_PATH;
		//saas 版导出目录用户上下文目录access文件夹
		String filename= "";
		if("2".equals( EopSetting.RUNMODE )){
			EopSite site = EopContext.getContext().getCurrentSite();
			filename ="/user/"+site.getUserid()+"/"+site.getId()+"/order";
		}else{
			filename ="/order";
		}
		File file  = new File(EopSetting.IMG_SERVER_PATH + filename);
		if(!file.exists())file.mkdirs();
		
		filename =filename+ "/order"+com.enation.framework.util.DateUtil.getDatelineLong()+".xls";
		excelUtil.writeToFile(EopSetting.IMG_SERVER_PATH+filename);
		
		return EopSetting.IMG_SERVER_DOMAIN +filename ;
	}


	@Override
	public OrderItem getItem(int itemid) {

		String sql = "select items.*,p.store as store from "
				+ this.getTableName("order_items") + " items ";
		sql += " left join " + this.getTableName("product")
				+ " p on p.product_id = items.product_id ";
		sql += " where items.item_id = ?";

		OrderItem item = (OrderItem) this.daoSupport.queryForObject(sql,
				OrderItem.class, itemid);

		return item;
	}

	/**
	 * 取某一会员未付款的订单数
	 */
	@Override
	public int getMemberOrderNum(int member_id, int payStatus) {
		return this.baseDaoSupport
				.queryForInt(
						"SELECT COUNT(0) FROM order WHERE member_id=? AND pay_status=?",
						member_id, payStatus);
	}

	/**
	 * by dable
	 */
	@Override
	public List<Map> getItemsByOrderid(Integer order_id) {
		String sql = "select * from order_items where order_id=?";
		return this.baseDaoSupport.queryForList(sql, order_id);
	}

	@Override
	public void refuseReturn(String orderSn) {
		this.baseDaoSupport.execute("update order set state = -5 where sn = ?",
				orderSn);
	}

	/**
	 * 更新订单价格
	 */
	@Override
	public void updateOrderPrice(double price, int orderid) {
		this.baseDaoSupport
				.execute(
						"update order set order_amount = order_amount-?,goods_amount = goods_amount- ? where order_id = ?",
						price, price, orderid);
	}

	/**
	 * 根据id查询物流公司
	 */
	@Override
	public String queryLogiNameById(Integer logi_id) {
		return (String) this.baseDaoSupport.queryForObject(
				"select name from logi_company where id=?", new StringMapper(),
				logi_id);
	}

	/**
	 * 游客订单查询
	 */
	@Override
	public Page searchForGuest(int pageNo, int pageSize, String ship_name,
			String ship_tel) {
		String sql = "select * from order where ship_name=? AND (ship_mobile=? OR ship_tel=?) and member_id is null ORDER BY order_id DESC";
		Page page = this.baseDaoSupport.queryForPage(sql.toString(), pageNo,pageSize, Order.class, ship_name, ship_tel, ship_tel);
		return page;
	}

	@Override
	public Page listByStatus(int pageNo, int pageSize, int status, int memberid) {
		String filedname = "status";
		if (status == 0) {
			// 等待付款的订单 按付款状态查询
			filedname = " status!=" + OrderStatus.ORDER_CANCELLATION
					+ " AND pay_status";
		}
		String sql = "select * from order where " + filedname
				+ "=? AND member_id=? ORDER BY order_id DESC";
		Page page = this.baseDaoSupport.queryForPage(sql.toString(), pageNo,
				pageSize, Order.class, status, memberid);
		return page;
	}
	
	@Override
	public List<Order> listByStatus(int status,int memberid){
		String filedname = "status";
		if (status == 0) {
			// 等待付款的订单 按付款状态查询
			filedname = " status!=" + OrderStatus.ORDER_CANCELLATION
					+ " AND pay_status";
		}
		String sql = "select * from order where " + filedname
				+ "=? AND member_id=? ORDER BY order_id DESC";
		
		return this.baseDaoSupport.queryForList(sql,status,memberid);
		
	}

	@Override
	public int getMemberOrderNum(int member_id) {
		return this.baseDaoSupport.queryForInt(
				"SELECT COUNT(0) FROM order WHERE member_id=?", member_id);
	}
	
	@Override
	public Page search(int pageNO, int pageSize, int disabled, String sn,
			String logi_no, String uname, String ship_name, int status,Integer paystatus){

		StringBuffer sql = new StringBuffer(
				"select * from " + this.getTableName("order") + " where disabled=?  ");
		if (status != -100) {
			if(status==-99){
				/*
				 * 查询未处理订单
				 * */
			sql.append(" and ((payment_type='cod' and status=0 )  or (payment_type!='cod' and status=1 )) ");
			}
			else
				sql.append(" and status = " + status + " ");
			
		}
		if (paystatus!=null && paystatus != -100) {
				sql.append(" and pay_status = " + paystatus + " ");
		}
		
		if (!StringUtil.isEmpty(sn)) {
			sql.append(" and sn = '" + sn + "' ");
		}
		if (!StringUtil.isEmpty(uname)) {
			sql.append(" and member_id  in ( SELECT  member_id FROM " + this.getTableName("member") + " where uname = '"
					+ uname + "' )  ");
		}
		if (!StringUtil.isEmpty(ship_name)) {
			sql.append(" and  ship_name = '" + ship_name + "' ");
		}
		if (!StringUtil.isEmpty(logi_no)) {
			sql.append(" and order_id in (SELECT order_id FROM " + this.getTableName("delivery") + " where logi_no = '"
					+ logi_no + "') ");
		}
		sql.append(" order by create_time desc ");
		Page page = this.daoSupport.queryForPage(sql.toString(), pageNO,
				pageSize, Order.class, disabled);
		return page;
	
	}
	
	@Override
	public Page search(int pageNO, int pageSize, int disabled, String sn,
			String logi_no, String uname, String ship_name, int status) {
		return search( pageNO,  pageSize,  disabled,  sn,
			 logi_no,  uname,  ship_name,  status,null);
	}

	@Override
	public Order getNext(String next, Integer orderId, Integer status,
			int disabled, String sn, String logi_no, String uname,
			String ship_name) {
		StringBuffer sql = new StringBuffer(
				"select * from " + this.getTableName("order") + " where  1=1  ");
		
		StringBuffer depotsql = new StringBuffer("  ");
		AdminUser adminUser = this.adminUserManager.getCurrentUser();
		if (adminUser.getFounder() != 1) { // 非超级管理员加过滤条件
			
			boolean isShiper = permissionManager.checkHaveAuth(PermissionConfig.getAuthId("depot_ship")); //检测是否是发货员
			
			
			boolean haveAllo = this.permissionManager
					.checkHaveAuth(PermissionConfig.getAuthId("allocation")); // 配货下达权限
			boolean haveOrder = this.permissionManager
					.checkHaveAuth(PermissionConfig.getAuthId("order"));// 订单管理员权限
			if (isShiper && !haveAllo && !haveOrder) {
				DepotUser depotUser = (DepotUser) adminUser;
				int depotid = depotUser.getDepotid();
				depotsql.append(" and depotid=" + depotid+"  ");
			}
		}
		
		StringBuilder sbsql = new StringBuilder("  ");
		if (status != null && status != -100) {
			sbsql.append(" and status = " + status + " ");
		}
//		if (!StringUtil.isEmpty(sn)) {
//			sbsql.append(" and sn = '" + sn.trim() + "' ");
//		}
		if (!StringUtil.isEmpty(uname)&&!uname.equals("undefined")) {
			sbsql.append(" and member_id  in ( SELECT  member_id FROM " + this.getTableName("member") + " where uname = '"
					+ uname + "' )  ");
		}
		if (!StringUtil.isEmpty(ship_name)) {
			sbsql.append("  and  ship_name = '" + ship_name.trim() + "'  ");
		}
		if (!StringUtil.isEmpty(logi_no)&&!logi_no.equals("undefined")) {
			sbsql.append("  and order_id in (SELECT order_id FROM " + this.getTableName("delivery") + " where logi_no = '"	+ logi_no +"')  ");
		}
		if (next.equals("previous")) {
			sql.append("  and order_id IN (SELECT CASE WHEN SIGN(order_id - "
					+ orderId
					+ ") < 0 THEN MAX(order_id)  END AS order_id FROM " + this.getTableName("order") + " WHERE order_id <> "
					+ orderId + depotsql.toString()+" and disabled=? "+sbsql.toString()+" GROUP BY SIGN(order_id - " + orderId
					+ ") ORDER BY SIGN(order_id - " + orderId + "))   ");
			//TODO MAX 及SIGN 函数经试验均可在mysql及oracle中通过，但mssql未验证
		} else if (next.equals("next")) {
			sql.append("  and  order_id in (SELECT CASE WHEN SIGN(order_id - "
					+ orderId
					+ ") > 0 THEN MIN(order_id) END AS order_id FROM " + this.getTableName("order") + " WHERE order_id <> "
					+ orderId + depotsql.toString()+ " and disabled=? "+sbsql.toString()+" GROUP BY SIGN(order_id - " + orderId
					+ ") ORDER BY SIGN(order_id - " + orderId + "))   ");
		} else {
			return null;
		}
		sql.append(" order by create_time desc ");
		////System.out.println(sql);
		Order order = (Order) this.daoSupport.queryForObject(sql.toString(),
				Order.class,disabled);
		return order;
	}

	/**
	 * 获取订单中商品的总价格
	 * 
	 * @param sessionid
	 * @return
	 */
	private double getOrderTotal(String sessionid) {
		List goodsItemList = cartManager.listGoods(sessionid);
		double orderTotal = 0d;
		if (goodsItemList != null && goodsItemList.size() > 0) {
			for (int i = 0; i < goodsItemList.size(); i++) {
				CartItem cartItem = (CartItem) goodsItemList.get(i);
				orderTotal += cartItem.getCoupPrice() * cartItem.getNum();
			}
		}
		return orderTotal;
	}


	private OrderItem getOrderItem(Integer itemid){
		return (OrderItem)this.baseDaoSupport.queryForObject("select * from order_items where item_id = ?", OrderItem.class, itemid);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean delItem(Integer itemid,Integer itemnum) {//删除订单货物
		OrderItem item = this.getOrderItem(itemid);
		Order order =  this.get(item.getOrder_id());
		boolean flag  = false;
		int paymentid = order.getPayment_id();
		int status = order.getStatus();
		if((paymentid == 1 ||paymentid == 3||paymentid == 4 ||paymentid ==5)&& (status == 0 ||status == 1 ||status == 2 ||status == 3 ||status == 4   )){
			flag=true;
		}
		if((paymentid == 2)&& (status == 0 ||status == 9 ||status == 3 ||status == 4  )){
			flag=true;
		}
		if(flag){
			try {
				if(itemnum.intValue() <= item.getNum().intValue()){
					Goods goods = goodsManager.getGoods(item.getGoods_id());
					double order_amount =  order.getOrder_amount();
					double itemprice =item.getPrice().doubleValue() * itemnum.intValue();
					double leftprice = CurrencyUtil.sub(order_amount, itemprice);
					int difpoint = (int)Math.floor(leftprice);
					Double[] dlyprice = this.dlyTypeManager.countPrice(order.getShipping_id(), order.getWeight()-(goods.getWeight().doubleValue() * itemnum.intValue() ), leftprice, order.getShip_regionid().toString());
					double sumdlyprice = dlyprice[0];
					this.baseDaoSupport.execute("update order set goods_amount = goods_amount- ?,shipping_amount = ?,order_amount =  ?,weight =  weight - ?,gainedpoint =  ? where order_id = ?"
							, itemprice,sumdlyprice,leftprice,(goods.getWeight().doubleValue() * itemnum.intValue() ),difpoint,order.getOrder_id());
					this.baseDaoSupport.execute("update freeze_point set mp =?,point =?  where orderid = ? and type = ?", difpoint,difpoint,order.getOrder_id(),"buygoods");
					if(itemnum.intValue() == item.getNum().intValue()){
						this.baseDaoSupport.execute("delete from order_items where item_id = ?", itemid);
					}else{
						this.baseDaoSupport.execute("update order_items set num = num - ? where item_id = ?", itemnum.intValue() ,itemid);
					}
					
				}else{
					return false;
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}

		}
		return flag;
	}

	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean saveAddrDetail(String addr, int orderid) {
		if(addr ==null || StringUtil.isEmpty(addr)){
			return false;
		}else{
			this.baseDaoSupport.execute("update order set ship_addr=?  where order_id=?", addr,	orderid);
			return true;
		}
	}
	
	@Override
	public boolean saveShipInfo(String remark,String ship_day, String ship_name,
			String ship_tel, String ship_mobile, String ship_zip, int orderid) {
		Order order = this.get(orderid);
		try {
			if(ship_day !=null && !StringUtil.isEmpty(ship_day)){
				this.baseDaoSupport.execute("update order set ship_day=?  where order_id=?", ship_day,	orderid);
				if(remark !=null && !StringUtil.isEmpty(remark)&&!remark.equals("undefined")){
					StringBuilder sb = new StringBuilder("");
					sb.append("【配送时间：");
					sb.append(remark.trim());
					sb.append("】");
					this.baseDaoSupport.execute("update order set remark= concat(remark,'"+sb.toString()+"')   where order_id=?",	orderid);
				}
					return true;
			}
			if(ship_name !=null && !StringUtil.isEmpty(ship_name)){
				this.baseDaoSupport.execute("update order set ship_name=?  where order_id=?", ship_name,	orderid);
					return true;
			}
			if(ship_tel !=null && !StringUtil.isEmpty(ship_tel)){
				this.baseDaoSupport.execute("update order set ship_tel=?  where order_id=?", ship_tel,	orderid);
					return true;
			}
			if(ship_mobile !=null && !StringUtil.isEmpty(ship_mobile)){
				this.baseDaoSupport.execute("update order set ship_mobile=?  where order_id=?", ship_mobile,	orderid);
					return true;
			}
			if(ship_zip !=null && !StringUtil.isEmpty(ship_zip)){
				this.baseDaoSupport.execute("update order set ship_zip=?  where order_id=?", ship_zip,	orderid);
					return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	@Override
	public void updatePayMethod(int orderid, int payid, String paytype,String payname) {
		
		this.baseDaoSupport.execute("update order set payment_id=?,payment_type=?,payment_name=? where order_id=?",payid,paytype,payname,orderid);
		
	}
	
	@Override
	public boolean checkProInOrder(int productid) {
		String sql ="select count(0) from order_items where product_id=?";
		return this.baseDaoSupport.queryForInt(sql, productid)>0;
	}
	
	@Override
	public boolean checkGoodsInOrder(int goodsid) {
		String sql ="select count(0) from order_items where goods_id=?";
		return this.baseDaoSupport.queryForInt(sql, goodsid)>0;
	}
	
	@Override
	public List listByOrderIds(Integer[] orderids,String order) {
		try {
			StringBuffer sql = new StringBuffer("select * from es_order where disabled=0 ");
			
			if(orderids!=null && orderids.length>0)
				sql.append(" and order_id in ("+StringUtil.arrayToString(orderids, ",")+")");
			
			if(StringUtil.isEmpty(order)){
				order="create_time desc";
			}
			sql.append(" order by  "+order);
			return  this.daoSupport.queryForList(sql.toString(), Order.class);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@Override
	public Page list(int pageNO, int pageSize, int disabled,  String order) {

		StringBuffer sql = new StringBuffer( "select * from order where disabled=? ");
		
		AdminUser adminUser = this.adminUserManager.getCurrentUser();
		if (adminUser.getFounder() != 1) { // 非超级管理员加过滤条件
			boolean isShiper = permissionManager.checkHaveAuth(PermissionConfig.getAuthId("depot_ship")); //检测是否是发货员
			boolean haveAllo = this.permissionManager
					.checkHaveAuth(PermissionConfig.getAuthId("allocation")); // 配货下达权限
			boolean haveOrder = this.permissionManager
					.checkHaveAuth(PermissionConfig.getAuthId("order"));// 订单管理员权限
			if (isShiper && !haveAllo && !haveOrder) {
				DepotUser depotUser = (DepotUser) adminUser;
				int depotid = depotUser.getDepotid();
				sql.append(" and depotid=" + depotid);
			}
		}

		order = StringUtil.isEmpty(order) ? "order_id desc" : order;
		sql.append(" order by " + order);
		return this.baseDaoSupport.queryForPage(sql.toString(), pageNO,pageSize, Order.class, disabled);
	}

	@Override
	public Page list(int pageNo, int pageSize, int status, int depotid,
			String order) {
		order = StringUtil.isEmpty(order) ? "order_id desc" : order;
		String sql = "select * from order where disabled=0 and status="
				+ status;
		if (depotid > 0) {
			sql += " and depotid=" + depotid;
		}
		sql += " order by " + order;
		Page page = this.baseDaoSupport.queryForPage(sql, pageNo, pageSize,Order.class);
		return page;
	}
	
	@Override
	public Page listOrder(Map map, int page, int pageSize, String other,String order) {
		
		String sql = createTempSql(map, other,order);
		Page webPage = this.daoSupport.queryForPage(sql, page, pageSize);
		return webPage;
	}
	
	@SuppressWarnings("unused")
	private String  createTempSql(Map map,String other,String order){
		
		Integer stype = (Integer) map.get("stype");
		String keyword = (String) map.get("keyword");
		String orderstate =  (String) map.get("order_state");//订单状态
		String start_time = (String) map.get("start_time");
		String end_time = (String) map.get("end_time");
		Integer status = (Integer) map.get("status");
		String sn = (String) map.get("sn");
		String ship_name = (String) map.get("ship_name");
		Integer paystatus = (Integer) map.get("paystatus");
		Integer shipstatus = (Integer) map.get("shipstatus");
		Integer shipping_type = (Integer) map.get("shipping_type");
		Integer payment_id = (Integer) map.get("payment_id");
		Integer depotid = (Integer) map.get("depotid");
		String complete = (String) map.get("complete");
		
		StringBuffer sql =new StringBuffer();
		sql.append(" select o.*, m.uname from es_order o left join es_member m on o.member_id = m.member_id where o.disabled = 0 ");
		
		AdminUser adminUser = this.adminUserManager.getCurrentUser();
		if(adminUser.getFounder() != 1){
			sql.append(" and o.seller_id in (select seller_id from es_seller where user_id = ").append(adminUser.getUserid()).append(" )");
		}
		
		if(stype!=null && keyword!=null){			
			if(stype==0){
				sql.append(" and (o.sn like '%"+keyword+"%'");
				sql.append(" or o.ship_name like '%"+keyword+"%')");
			}
		}
		
		if(status!=null){
			sql.append("and o.status="+status);
		}
		
		if(sn!=null && !StringUtil.isEmpty(sn)){
			sql.append(" and o.sn like '%"+sn+"%'");
		}
		
		if(ship_name!=null && !StringUtil.isEmpty(ship_name)){
			sql.append(" and o.ship_name like '"+ship_name+"'");
		}
		
		if(paystatus!=null){
			sql.append(" and o.pay_status="+paystatus);
		}
		
		if(shipstatus!=null){
			sql.append(" and o.ship_status="+shipstatus);
		}
		
		if(shipping_type!=null){
			sql.append(" and o.shipping_id="+shipping_type);
		}
		
		if(payment_id!=null){
			sql.append(" and o.payment_id="+payment_id);
		}
		
		if (depotid!=null && depotid > 0) {
			sql.append(" and o.depotid=" + depotid);
		}
		
		if(start_time!=null&&!StringUtil.isEmpty(start_time)){			
			long stime = com.enation.framework.util.DateUtil.getDateline(start_time+" 00:00:00","yyyy-MM-dd HH:mm:ss");
			sql.append(" and o.create_time>"+stime);
		}
		if(end_time!=null&&!StringUtil.isEmpty(end_time)){			
			long etime = com.enation.framework.util.DateUtil.getDateline(end_time +" 23:59:59", "yyyy-MM-dd HH:mm:ss");
			sql.append(" and o.create_time<"+(etime));
		}
		if( !StringUtil.isEmpty(orderstate)){
			if(orderstate.equals("wait_ship") ){ //对待发货的处理
				/*sql.append(" and ( ( o.payment_type!='cod' and o.payment_id!=8  and  o.status="+OrderStatus.ORDER_PAY_CONFIRM +") ");//非货到付款的，要已结算才能发货
				sql.append(" or ( o.payment_type='cod' and  o.status="+OrderStatus.ORDER_NOT_PAY +")) ");//货到付款的，新订单（已确认的）就可以发货*/
				sql.append(" and (o.payment_id!=8  and  o.status="+OrderStatus.ORDER_PAY_CONFIRM +") ");//非货到付款的，要已结算才能发货
			}else if(orderstate.equals("wait_pay") ){
				/*sql.append(" and ( ( o.payment_type!='cod' and  o.status="+OrderStatus.ORDER_NOT_PAY +") ");//非货到付款的，未付款状态的可以结算
				sql.append(" or ( o.payment_id=8 and o.status!="+OrderStatus.ORDER_NOT_PAY+"  and  o.pay_status!="+OrderStatus.PAY_CONFIRM +")" ); 
				sql.append(" or ( o.payment_type='cod' and  (o.status="+OrderStatus.ORDER_SHIP +" or o.status="+OrderStatus.ORDER_ROG+" )  ) )");//货到付款的要发货或收货后才能结算*/
				sql.append(" and ( (o.status="+OrderStatus.ORDER_NOT_PAY +") ");//非货到付款的，未付款状态的可以结算
				sql.append(" or ( o.payment_id=8 and o.status!="+OrderStatus.ORDER_NOT_PAY+"  and  o.pay_status!="+OrderStatus.PAY_CONFIRM +"))" ); 
			}else if(orderstate.equals("wait_rog") ){ 
				sql.append(" and o.status="+OrderStatus.ORDER_SHIP  ); 
			}else{
				sql.append(" and o.status="+orderstate);
			}
		
		}
		
		if(!StringUtil.isEmpty(complete)){
			sql.append(" and o.status="+OrderStatus.ORDER_COMPLETE);
		}
		
		sql.append(" ORDER BY o."+other+" "+order);
		
	//	System.out.println(sql.toString());
		return sql.toString();
	}
	
	@Override
	public void saveDepot(int orderid, int depotid) {
		this.orderPluginBundle.onOrderChangeDepot(this.get(orderid), depotid,this.listGoodsItems(orderid));
		this.daoSupport.execute("update es_order set depotid=?  where order_id=?", depotid,orderid);
	}
	
	@Override
	public void savePayType(int orderid, int paytypeid) {
		PayCfg cfg  =  this.paymentManager.get(paytypeid);
		String typename =cfg.getName();
		String paytype= cfg.getType();
		this.daoSupport.execute("update es_order set payment_id=?,payment_name=?,payment_type=? where order_id=?", paytypeid,typename,paytype,orderid);
	}
	
	@Override
	public void saveShipType(int orderid, int shiptypeid) {
		String typename = this.dlyTypeManager.getDlyTypeById(shiptypeid).getName();
		this.daoSupport.execute("update es_order set shipping_id=?,shipping_type=? where order_id=?", shiptypeid,typename,orderid);
	}
	
	@Override
	public void add(Order order) {
		this.baseDaoSupport.insert("es_order", order);
	}
	
	@Override
	public void saveAddr(int orderId,int ship_provinceid,int ship_cityid,int ship_regionid,String Attr){
		this.daoSupport.execute("update es_order set ship_provinceid=?,ship_cityid=?,ship_regionid=?,shipping_area=? where order_id=?",ship_provinceid,ship_cityid,ship_regionid,Attr,orderId);
	}
	
	@Override
	public Integer getOrderGoodsNum(int order_id) {
		String sql = "select count(0) from order_items where order_id =?";
		return this.baseDaoSupport.queryForInt(sql, order_id);
	}

	/**
	 * 微信获取APP回调信息方法
	 * @return
	 */
	@Override
	public Map getWxpayParam(String sn, Double price, String notify_url){
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		JSONObject jsonData = new JSONObject();
		
//		notify_url = "http://192.168.1.210:8089/quwei/pay_wxcallback_wxMobilePayPlugin.html";//本地
//		notify_url = "http://120.24.240.4:8080/quwei/pay_wxcallback_wxMobilePayPlugin.html";//后台服务器
//		notify_url = "http://120.24.244.234:8089/quwei_test/pay_wxcallback_wxMobilePayPlugin.html";//预生产环境（放在前台）
//		notify_url = "http://120.24.240.4:8089/quwei_test/pay_wxcallback_wxMobilePayPlugin.html";//预生产环境（放在后台）
//		notify_url = "http://m.gou8go.com/quwei/pay_wxcallback_wxMobilePayPlugin.html";//正式环境
//		notify_url = "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/pay_wxcallback_wxMobilePayPlugin.html";//正式环境

		//更新订单的付款方式
		JSONObject temp = new JSONObject();
		Map map = this.getPrepayParm(sn, price, notify_url);
		
		return map;
		
	}
	
	@Override
	public Map getMppayParam(String sn, Double price, String notify_url, Integer memberId) {
		/*Map map = this.getPrepayParm2(sn, price, notify_url, memberId);
		return map;*/
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();
		Member member=this.memberManager.get(memberId);
        SortedMap<String, Object> parameterMap = new TreeMap<String, Object>();  
        parameterMap.put("appid", WeixinConfig.APPID);  
        parameterMap.put("mch_id", WeixinConfig.MCH_ID);  
        parameterMap.put("nonce_str", PayCommonUtil.getRandomString(32));  
        parameterMap.put("body", "巢享共享洗车");
        parameterMap.put("out_trade_no", sn);
        parameterMap.put("fee_type", "CNY");  
        Double d = CurrencyUtil.mul(price, 100);  
        parameterMap.put("total_fee", d.intValue() + "");  
        parameterMap.put("spbill_create_ip", request.getLocalAddr());  
        parameterMap.put("notify_url", notify_url);
        parameterMap.put("trade_type", "JSAPI");
        //trade_type为JSAPI是 openid为必填项
        try {
        	String openid=StringUtils.isNotBlank(member.getOpenid()) ? member.getOpenid(): WeixinAuthUtil.getOpenid(request, response);
			parameterMap.put("openid", openid);
			if(StringUtils.isBlank(member.getOpenid())){
				member.setOpenid(openid);
				this.memberManager.edit(member);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
        String sign = PayCommonUtil.createSign("UTF-8", parameterMap); 
        parameterMap.put("sign", sign);  
        System.out.println("公众号支付requestMap："+parameterMap);
        String requestXML = PayCommonUtil.getRequestXml(parameterMap);
        System.out.println("公众号支付requestXML："+requestXML);
        String result = PayCommonUtil.httpsRequest(  
                "https://api.mch.weixin.qq.com/pay/unifiedorder", "POST",  
                requestXML);  
        System.out.println("公众号支付responseXml："+result); 
        Map<String, String> map = null;  
        try {  
            map = PayCommonUtil.doXMLParse(result);  
        } catch (JDOMException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        SortedMap<String, Object> sortedMap = new TreeMap<String, Object>();  
        sortedMap.put("appId", WeixinConfig.APPID);
        sortedMap.put("timeStamp", DateUtil.getDateline());  
        sortedMap.put("nonceStr", map.get("nonce_str"));  
        sortedMap.put("package", "prepay_id="+map.get("prepay_id"));  
        sortedMap.put("signType", "MD5");  
        sortedMap.put("paySign", PayCommonUtil.createSign("UTF-8", sortedMap)); 
        return sortedMap;
    }
	
	public Map getPrepayParm2(String sn, Double price, String notify_url, Integer memberId){
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();
		String appId = WeixinConfig.APPID;
		String mchid = WeixinConfig.MCH_ID;
		String key = WeixinConfig.KEY;
		SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
        parameters.put("appid", appId);
        parameters.put("body", "巢享共享洗车");// 购买支付信息
        parameters.put("mch_id", mchid);
        parameters.put("nonce_str", new Date().getTime() + "");
        parameters.put("notify_url", notify_url);
        parameters.put("out_trade_no", sn);// 订单号
        parameters.put("spbill_create_ip", request.getLocalAddr());
        //Double d = price * 100;
        Double d = CurrencyUtil.mul(price, 100);
        System.out.println("price*100="+d);
        parameters.put("total_fee", d.intValue() + "");// 总金额单位为分
        parameters.put("trade_type", "JSAPI");
        try {
        	Member member=this.memberManager.get(memberId);
        	String openid=StringUtils.isNotBlank(member.getOpenid()) ? member.getOpenid(): WeixinAuthUtil.getOpenid(request, response);
        	parameters.put("openid", openid);
			if(StringUtils.isBlank(member.getOpenid())){
				member.setOpenid(openid);
				this.memberManager.edit(member);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
        String sign = WxpubOAuth.createSign("UTF-8", parameters, key);
        parameters.put("sign", sign);
        String requestXML = WxpubOAuth.getRequestXml(parameters);
        System.out.println(requestXML);
        String result = UtilCommon.httpsRequest2(WX_GATEWAY_NEW, "POST", requestXML);
        System.out.println(result);
        System.out.println("获取微信交易号返回结果");
        
        Map<String, String> map = new HashMap<String, String>();
		try {
			map = XMLUtil.doXMLParse(result);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		SortedMap<Object, Object> params = new TreeMap<Object, Object>();//用于装载返回页面的map
		if(map == null || map.size() == 0 || "FAIL".equals(map.get("return_code").toString())){
			params.put("error", "服务器出错");
		}else{
			long timeStamp = DateUtil.getDateline();
	        params.put("appid", appId);
	        params.put("mch_id", mchid);
	        params.put("prepay_id",	map.get("prepay_id"));
	        params.put("timestamp", timeStamp);
	        params.put("nonce_str", map.get("nonce_str"));
	        String paySign = WxpubOAuth.createSign("UTF-8", params, WeixinConfig.KEY);
	        params.put("sign", paySign); // paySign的生成规则和Sign的生成规则一致
	        String json = JSONObject.fromObject(params).toString();
	        System.out.println(json);
		}
		return params;
	}
	
	/**
	 * 获取微信openid
	 * @return
	 */
	public String getOpenid(){
		return "";
	}
	
	/**
	 * 获取微信支付的交易号
	 * @param sn
	 * @param price
	 * @param notify_url
	 * @return
	 */
	public Map getPrepayParm(String sn, Double price, String notify_url){
		
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		String appId = APPID;
		String mchid = MCH_ID;
		String appSecret = APP_KEY;
		SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
        parameters.put("appid", appId);
        parameters.put("body", "巢享共享洗车");// 购买支付信息
        parameters.put("mch_id", mchid);
        parameters.put("nonce_str", new Date().getTime() + "");
        parameters.put("notify_url", notify_url);
        parameters.put("out_trade_no", sn);// 订单号
        parameters.put("spbill_create_ip", request.getLocalAddr());
        //Double d = price * 100;
        Double d = CurrencyUtil.mul(price, 100);
        System.out.println("price*100="+d);
        parameters.put("total_fee", d.intValue() + "");// 总金额单位为分
        parameters.put("trade_type", "APP");
        String sign = WxpubOAuth.createSign("UTF-8", parameters, appSecret);
        parameters.put("sign", sign);
        String requestXML = WxpubOAuth.getRequestXml(parameters);
        System.out.println(requestXML);
        String result = UtilCommon.httpsRequest2(WX_GATEWAY_NEW, "POST", requestXML);
        System.out.println(result);
        System.out.println("获取微信交易号返回结果");
        
        Map<String, String> map = new HashMap<String, String>();
		try {
			map = XMLUtil.doXMLParse(result);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		SortedMap<Object, Object> params = new TreeMap<Object, Object>();//用于装载返回页面的map
		if(map == null || map.size() == 0 || "FAIL".equals(map.get("return_code").toString())){
			params.put("error", "服务器出错");
		}else{
			long timeStamp = DateUtil.getDateline();
	        params.put("appid", appId);
	        params.put("partnerid", mchid);
	        params.put("prepayid",	map.get("prepay_id"));
	        params.put("package", "Sign=WXPay"); // 这里用packageValue是预防package是关键字在js获取值出错
	        params.put("timestamp", timeStamp);
	        params.put("noncestr", map.get("nonce_str"));
	        String paySign = WxpubOAuth.createSign("UTF-8", params, APP_KEY);
	        params.put("sign", paySign); // paySign的生成规则和Sign的生成规则一致
	        String json = JSONObject.fromObject(params).toString();
	        System.out.println(json);
		}
        
		return params;
		
	}
	
	@Override
	public void updatePaymentId(String order_id, String payment_id) {
		
		StringBuffer sql = new StringBuffer();
		sql.append(" update es_order set payment_id = ? where order_id = ? ");
		
		this.baseDaoSupport.execute(sql.toString(), payment_id, order_id);
		
	}
	
	@Override
	public int getOrderListBySelectTime(double select_time) {

		long time = System.currentTimeMillis()/1000;
		StringBuffer sql = new StringBuffer();
		sql.append(" select order_id from es_order where pay_time > ? and status = 2 ");
		
		List<Map> list = this.baseDaoSupport.queryForList(sql.toString(), time-select_time);
		
		return list.size();
	}

	@Override
	public int isHasTehui(String cart_ids) {

		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT tag_id FROM es_tag_rel WHERE rel_id IN ( SELECT goods_id FROM es_cart WHERE cart_id in (").append(cart_ids).append("))");
		
		List<Map> list = this.baseDaoSupport.queryForList(sql.toString());
		if(list.size() != 0){
			return 1;
		}else{
			return 0;
		}
	}
	
	public IOrderAllocationManager getOrderAllocationManager() {
		return orderAllocationManager;
	}
	
	public void setOrderAllocationManager(
			IOrderAllocationManager orderAllocationManager) {
		this.orderAllocationManager = orderAllocationManager;
	}
	
	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}
	
	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}
	
	public IDepotManager getDepotManager() {
		return depotManager;
	}
	
	public void setDepotManager(IDepotManager depotManager) {
		this.depotManager = depotManager;
	}
	
	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}

	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}

	public IPermissionManager getPermissionManager() {
		return permissionManager;
	}

	public void setPermissionManager(IPermissionManager permissionManager) {
		this.permissionManager = permissionManager;
	}

	public IRoleManager getRoleManager() {
		return roleManager;
	}

	public void setRoleManager(IRoleManager roleManager) {
		this.roleManager = roleManager;
	}

	public IProductManager getProductManager() {
		return ProductManager;
	}

	public void setProductManager(IProductManager productManager) {
		ProductManager = productManager;
	}
	
	public IMemberManager getMemberManager() {
		return memberManager;
	}

	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}


	
	
}
	
	
