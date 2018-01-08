package com.enation.app.shop.core.action.mobile;

import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.component.payment.plugin.alipay.mobile.util.app.PayUtils;
import com.enation.app.shop.component.payment.plugin.weixin.jssdk.PastUtil;
import com.enation.app.shop.core.constant.CXConstant;
import com.enation.app.shop.core.constant.ReturnMsg;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.model.Recharge;
import com.enation.app.shop.core.model.WashCard;
import com.enation.app.shop.core.model.WashMemberCard;
import com.enation.app.shop.core.plugin.payment.IPaymentEvent;
import com.enation.app.shop.core.service.IDictionaryManager;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.app.shop.core.service.IRechargeManager;
import com.enation.app.shop.core.service.IWashCardManager;
import com.enation.app.shop.core.service.IWashMemberCardManager;
import com.enation.app.shop.core.utils.DetailUtil;
import com.enation.app.shop.core.utils.ResponseUtils;
import com.enation.eop.resource.model.Dictionary;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.StringUtil;

/**
 * app支付接口
 * 
 * @author yexf 2016-10-16
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/shop")
@Action("mobileAppPay")
public class MobileAppPayAction extends WWAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Autowired
	private IOrderManager orderManager;
	@Autowired
	private IProductManager productManager;
	@Autowired
	private IRechargeManager rechargeManager;
	@Autowired
	private IWashMemberCardManager washMemberCardManager;
	@Autowired
	private IDictionaryManager dictionaryManager;
	@Autowired
	private IWashCardManager washCardManager;
	@Autowired
	private IMemberManager memberManager;

	/**
	 * 支付宝支付异步回调接口
	 * 
	 * @author yexf
	 * @date 2016-5-25
	 */
	public void onAlipayAppCallBack() {

		System.out.println("开始...");
		System.out.println("onAlipayAppCallBack()--start");
		HttpServletResponse response = ServletActionContext.getResponse();

		IPaymentEvent alipayAppPlugin = SpringContextHolder.getBean("alipayMobilePlugin");
		String str = new String();

		str = alipayAppPlugin.onCallBack("s");// s：标准订单
		System.out.println("onAlipayAppCallBack():" + str);
		try {
			response.setContentType("text/html");
			response.setCharacterEncoding("utf-8");
			PrintWriter out;
			out = response.getWriter();
			out.write(str);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("支付宝支付异步回调接口出错：" + e.getStackTrace());
		}
	}

	/**
	 * 微信获取APP支付信息 pay_id = 2
	 * 
	 * @author yexf 2016-10-17
	 * @return
	 */
	public String getWxpayParam1() {

		// JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		// JSONArray jsonArray = new JSONArray();

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		// HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		// String rh = RequestHeaderUtil.requestHeader(request, response);

		// String member_id = request.getHeader("u");
		// member_id = request.getParameter("member_id");
		// String token = request.getHeader("k");
		// String platform = request.getHeader("p");//系统
		// String version = request.getHeader("v");//版本号

		/*
		 * if(rh!=null && rh=="1"){ this.renderJson("-99", "用户未登录", ""); return
		 * WWAction.APPLICAXTION_JSON; }else if(rh!=null && rh=="2"){
		 * this.renderJson("-99", "您账号在其他设备登陆", ""); return
		 * WWAction.APPLICAXTION_JSON; }else if(rh!=null && rh=="3"){
		 * this.renderJson("1", "参数验证错误！", ""); return
		 * WWAction.APPLICAXTION_JSON; }else if(rh!=null && rh=="4"){
		 * this.renderJson("1", "请升级到最新版本", ""); return
		 * WWAction.APPLICAXTION_JSON; }
		 */

		try {

			String order_id = request.getParameter("order_id");

			String sn = "";
			Double price = 0.0;
			String notify_url = "";
			Order order = null;

			if (StringUtil.isEmpty(order_id)) {
				this.renderJson("1", "订单ID不能为空", "");
				return WWAction.APPLICAXTION_JSON;
			}

			order = this.orderManager.get(Integer.parseInt(order_id));
			if (order == null) {
				this.renderJson("1", "该订单不存在", "");
				return WWAction.APPLICAXTION_JSON;
			}
			if (order.getStatus() != 0) {
				this.renderJson("1", "订单状态不符合支付条件", "");
				return WWAction.APPLICAXTION_JSON;
			}

			List<Map> itemList = this.orderManager.getItemsByOrderid(Integer.parseInt(order_id));
			for (Map itemMap : itemList) {
				int num = Integer.parseInt(StringUtil.isNull(itemMap.get("num")));
				Product product = this.productManager
						.get(Integer.parseInt(StringUtil.isNull(itemMap.get("product_id"))));
				if (product.getEnable_store() < num) {
					this.renderJson("1", product.getName() + "库存不足", "");
					return WWAction.APPLICAXTION_JSON;
				}
			}

			sn = order.getSn();
			price = order.getNeedPayMoney();
			// price = 0.01;

			// notify_url =
			// "http://www.wx0598.com/api/shop/shicai_wxMobilePayPlugin_payment-callback.do";//回调地址
			notify_url = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath()
					+ "/api/shop/s_wxMobilePayPlugin_payment-callback.do";// 正式环境

			Map map = this.orderManager.getWxpayParam(sn, price, notify_url);

			/**
			 * 更新订单的付款方式
			 */
			String payment_id = "2";
			this.orderManager.updatePaymentId(order_id, payment_id);

			if (map.get("error") != null) {
				this.renderJson("1", "error", "");
				return WWAction.APPLICAXTION_JSON;
			} else {
				JSONObject pay_message = JSONObject.fromObject(map);
				jsonData.put("pay_message", pay_message);
				this.renderJson("0", "获取成功", jsonData.toString());
				return WWAction.APPLICAXTION_JSON;
			}

		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			if (StringUtil.isEmpty(message) || message.equals("null")) {
				message = "服务器出错，请重试";
			}
			logger.error("微信获取APP支付信息出错！错误信息：" + message);
			this.renderJson("1", message, "");
			return WWAction.APPLICAXTION_JSON;
		}
	}

	/**
	 * 微信获取APP支付信息 pay_id = 2 2017-5-15
	 * 
	 * @param
	 * @return
	 */
	public void getWxpayParam() {

		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		// JSONArray jsonArray = new JSONArray();

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		String type = request.getParameter("type");// 支付类型（1：充值 2：购买会员卡 3：支付订单）
		String sign_id = request.getParameter("sign_id");// 对应id（充值金额id，会员卡id，订单id）

		if (StringUtil.isEmpty(type) || StringUtil.isEmpty(sign_id)) {
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.PARAMETER_ERROR, "2",
					jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return;
		}

		try {

			/*
			 * if(validateLoginUtil.checkLogin(request, response)){ return; }
			 */

			String member_id = request.getHeader("member_id");

			long now_time = System.currentTimeMillis();

			String sn = "";
			Double price = 0.0;
			String notify_url = "";

			if ("1".equals(type)) {// 充值

				Recharge recharge = new Recharge();

				Dictionary dictionary = this.dictionaryManager.getAllDataList(Integer.parseInt(sign_id));

				String value = dictionary.getD_value();
				String[] values = value.split(",");
				String price_str = values[2];
				String balance_str = values[3];
				price = Double.parseDouble(price_str);
				Double balance = Double.parseDouble(balance_str);
				
				recharge.setMember_id(Integer.parseInt(member_id));
				recharge.setCreate_time(now_time);
				recharge.setPay_type(2);
				recharge.setPrice(price);
				recharge.setBalance(balance);
				recharge.setSn(this.createSn());

				this.rechargeManager.addRecharge(recharge);

				sn = recharge.getSn();
				// String total_fee = new String();

				// 付款金额
				// total_fee = String.valueOf(price);
				// total_fee = "0.01";

			} else if ("2".equals(type)) {// 购买会员卡

				WashMemberCard washMemberCard = new WashMemberCard();
				long create_time = System.currentTimeMillis();

				WashCard washCard = this.washCardManager.getWashCard(sign_id);

				washMemberCard.setCard_type(washCard.getType());
				washMemberCard.setCreate_time(create_time);
				washMemberCard.setEnd_time(0L);
				washMemberCard.setImage(washCard.getImage());
				washMemberCard.setMember_id(Integer.parseInt(member_id));
				washMemberCard.setPay_type(2);
				washMemberCard.setPrice(washCard.getPrice());
				washMemberCard.setSn(this.createSn());
				washMemberCard.setWash_card_id(washCard.getWash_card_id());
				washMemberCard.setWash_day(washCard.getWash_day());
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DAY_OF_MONTH, washCard.getWash_day());
				long end_time = calendar.getTimeInMillis();
				washMemberCard.setEnd_time(end_time);

				this.washMemberCardManager.addWashMemberCard(washMemberCard);

				sn = washMemberCard.getSn();
				// String total_fee = new String();
				price = washCard.getPrice();
				// 付款金额
				// total_fee = String.valueOf(washCard.getPrice());
				// total_fee = "0.01";

			} else if ("3".equals(type)) {// 支付订单
				Order order = this.orderManager.get(Integer.parseInt(sign_id));
				String total_fee = new String();

				sn = order.getSn();
				price = order.getNeedPayMoney();
				// 付款金额
				// total_fee = String.valueOf(order.getNeedPayMoney());
				// total_fee = "0.01";
			}

			// price = 0.01;

			// notify_url =
			// "http://www.wx0598.com/api/shop/shicai_wxMobilePayPlugin_payment-callback.do";//回调地址
			String order_type = DetailUtil.getOrderType(type);
			notify_url = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath()
					+ "/api/shop/" + order_type + "_wxMobilePayPlugin_payment-callback.do";// 正式环境
			Map map = this.orderManager.getWxpayParam(sn, price, notify_url);

			if (map.get("error") != null) {
				json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.SERVER_ERROR, "2",
						jsonData.toString());
				ResponseUtils.renderJson(response, json.toString());
			} else {

				JSONObject pay_message = JSONObject.fromObject(map);
				System.out.println(pay_message);

				jsonData.put("pay_message", pay_message);

				json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1",
						jsonData.toString());
				ResponseUtils.renderJson(response, json.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("微信获取APP支付信息出错！错误信息：" + StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}
	}

	/**
	 * 支付宝获取APP支付信息 pay_id = 1
	 * 
	 * @author yexf 2016-10-17
	 * @return
	 */
	public void getAlipayParam() {

		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		// JSONArray jsonArray = new JSONArray();

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		String type = request.getParameter("type");// 支付类型（1：充值 2：购买会员卡 3：支付订单）
		String sign_id = request.getParameter("sign_id");// 对应id（充值金额id，会员卡id，订单id）

		if (StringUtil.isEmpty(type) || StringUtil.isEmpty(sign_id)) {
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.PARAMETER_ERROR, "2",
					jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return;
		}

		try {

			/*
			 * if(validateLoginUtil.checkLogin(request, response)){ return; }
			 */

			String member_id = request.getHeader("member_id");
			Member member = this.memberManager.getMemberByMemberId(member_id);

			long now_time = System.currentTimeMillis();

			String[] params = new String[4];

			if ("1".equals(type)) {// 充值

				Recharge recharge = new Recharge();

				Dictionary dictionary = this.dictionaryManager.getAllDataList(Integer.parseInt(sign_id));

				String value = dictionary.getD_value();
				String[] values = value.split(",");
				String price_str = values[2];
				String balance_str = values[3];
				Double price = Double.parseDouble(price_str);
				Double balance = Double.parseDouble(balance_str);

				recharge.setMember_id(Integer.parseInt(member_id));
				recharge.setCreate_time(now_time);
				recharge.setPay_type(1);
				recharge.setPrice(price);
				recharge.setBalance(balance);
				recharge.setSn(this.createSn());

				this.rechargeManager.addRecharge(recharge);

				params[0] = recharge.getSn();
				params[1] = "CZ:" + recharge.getSn();
				params[2] = "recharge";
				String total_fee = new String();

				// 付款金额
				total_fee = String.valueOf(price);

				// total_fee = "0.01";
				params[3] = total_fee;
			} else if ("2".equals(type)) {// 购买会员卡

				WashMemberCard washMemberCard = new WashMemberCard();
				long create_time = System.currentTimeMillis();

				WashCard washCard = this.washCardManager.getWashCard(sign_id);

				washMemberCard.setCard_type(washCard.getType());
				washMemberCard.setCreate_time(create_time);
				washMemberCard.setEnd_time(0L);
				washMemberCard.setImage(washCard.getImage());
				washMemberCard.setMember_id(Integer.parseInt(member_id));
				washMemberCard.setPay_type(1);
				washMemberCard.setPrice(washCard.getPrice());
				washMemberCard.setSn(this.createSn());
				washMemberCard.setWash_card_id(washCard.getWash_card_id());
				washMemberCard.setWash_day(washCard.getWash_day());
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DAY_OF_MONTH, washCard.getWash_day());
				long end_time = calendar.getTimeInMillis();
				washMemberCard.setEnd_time(end_time);

				this.washMemberCardManager.addWashMemberCard(washMemberCard);

				params[0] = washMemberCard.getSn();
				params[1] = "HYK:" + washMemberCard.getSn();
				params[2] = "membercard";
				String total_fee = new String();

				// 付款金额
				total_fee = String.valueOf(washCard.getPrice());

				// total_fee = "0.01";
				params[3] = total_fee;

			} else if ("3".equals(type)) {// 支付订单
				Order order = this.orderManager.get(Integer.parseInt(sign_id));
				params[0] = order.getSn();
				params[1] = "S:" + order.getSn();
				params[2] = "order";
				String total_fee = new String();

				// 付款金额
				total_fee = String.valueOf(order.getNeedPayMoney());

				// total_fee = "0.01";
				params[3] = total_fee;
			}

			String pay_message = PayUtils.getSignOrderInfo(params, type);
			System.out.println(pay_message);

			jsonData.put("pay_message", pay_message);

			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1",
					jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());

		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("支付宝获取APP支付信息出错！错误信息：" + StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}
	}

	public String createSn() {
		Date now = new Date();
		String str_4 = ((int) (Math.random() * (9999 - 1000 + 1)) + 1000) + "";
		String sn = com.enation.framework.util.DateUtil.toString(now, "yyyyMMddHHmmssSSS") + str_4;
		return sn;
	}

	/**
	 * 支付宝获取APP支付信息 pay_id = 1
	 * 
	 * @author yexf 2016-10-17
	 * @return
	 */
	public String getAlipayParam1() {

		// JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		// JSONArray jsonArray = new JSONArray();

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		// HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		// String rh = RequestHeaderUtil.requestHeader(request, response);

		// String member_id = request.getHeader("u");
		// member_id = request.getParameter("member_id");
		// String token = request.getHeader("k");
		// String platform = request.getHeader("p");//系统
		// String version = request.getHeader("v");//版本号

		/*
		 * if(rh!=null && rh=="1"){ this.renderJson("-99", "用户未登录", ""); return
		 * WWAction.APPLICAXTION_JSON; }else if(rh!=null && rh=="2"){
		 * this.renderJson("-99", "您账号在其他设备登陆", ""); return
		 * WWAction.APPLICAXTION_JSON; }else if(rh!=null && rh=="3"){
		 * this.renderJson("1", "参数验证错误！", ""); return
		 * WWAction.APPLICAXTION_JSON; }else if(rh!=null && rh=="4"){
		 * this.renderJson("1", "请升级到最新版本", ""); return
		 * WWAction.APPLICAXTION_JSON; }
		 */

		try {

			String order_id = request.getParameter("order_id");

			Order order = null;

			if (StringUtil.isEmpty(order_id)) {
				this.renderJson("1", "订单ID不能为空", "");
				return WWAction.APPLICAXTION_JSON;
			}

			order = this.orderManager.get(Integer.parseInt(order_id));
			if (order == null) {
				this.renderJson("1", "该订单不存在", "");
				return WWAction.APPLICAXTION_JSON;
			}
			if (order.getStatus() != 0) {
				this.renderJson("1", "订单状态不符合支付条件", "");
				return WWAction.APPLICAXTION_JSON;
			}

			List<Map> itemList = this.orderManager.getItemsByOrderid(Integer.parseInt(order_id));
			for (Map itemMap : itemList) {
				int num = Integer.parseInt(StringUtil.isNull(itemMap.get("num")));
				Product product = this.productManager
						.get(Integer.parseInt(StringUtil.isNull(itemMap.get("product_id"))));
				if (product.getEnable_store() < num) {
					this.renderJson("1", product.getName() + "库存不足", "");
					return WWAction.APPLICAXTION_JSON;
				}
			}

			String[] params = new String[4];
			params[0] = order.getSn();
			params[1] = "SN:" + order.getSn();
			params[2] = "goods";
			String total_fee = new String();

			// 付款金额
			total_fee = String.valueOf(order.getNeedPayMoney());

			// total_fee = "0.01";
			params[3] = total_fee;

			String pay_message = PayUtils.getSignOrderInfo(params);
			System.out.println(pay_message);

			/**
			 * 更新订单的付款方式
			 */
			String payment_id = "1";
			this.orderManager.updatePaymentId(order_id, payment_id);

			jsonData.put("pay_message", pay_message);
			this.renderJson("0", "获取成功", jsonData.toString());
			return WWAction.APPLICAXTION_JSON;

		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			if (StringUtil.isEmpty(message) || message.equals("null")) {
				message = "服务器出错，请重试";
			}
			logger.error("支付宝获取APP支付信息出错！错误信息：" + message);
			this.renderJson("1", message, "");
			return WWAction.APPLICAXTION_JSON;
		}
	}

	/**
	 * 微信获取公众号支付信息
	 */
	public void getMppayParam() {
		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();
		String type = request.getParameter("type");// 支付类型（1：充值 2：购买会员卡 3：支付订单）
		String sign_id = request.getParameter("sign_id");// 对应id（充值金额id，会员卡id，订单id）
		if (StringUtil.isEmpty(type) || StringUtil.isEmpty(sign_id)) {
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.PARAMETER_ERROR, "2",
					jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return;
		}
		logger.info("type:"+type+",sign_id:"+sign_id);
		try {
			/*
			 * if(validateLoginUtil.checkLogin(request, response)){ return; }
			 */
			String member_id = request.getHeader("member_id");
			if (StringUtil.isEmpty(member_id)) {
				json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.PARAMETER_ERROR, "2",
						jsonData.toString());
				ResponseUtils.renderJson(response, json.toString());
				return;
			}
			long now_time = System.currentTimeMillis();
			String sn = "";
			Double price = 0.0;
			String notify_url = "";
			if ("1".equals(type)) {// 充值

				Recharge recharge = new Recharge();

				Dictionary dictionary = this.dictionaryManager.getAllDataList(Integer.parseInt(sign_id));

				String value = dictionary.getD_value();
				String[] values = value.split(",");
				String price_str = values[2];
				String balance_str = values[3];
				price = Double.parseDouble(price_str);
				Double balance = Double.parseDouble(balance_str);
				
				recharge.setMember_id(Integer.parseInt(member_id));
				recharge.setCreate_time(now_time);
				recharge.setPay_type(2);
				recharge.setPrice(price);
				recharge.setBalance(balance);
				recharge.setSn(this.createSn());

				this.rechargeManager.addRecharge(recharge);

				sn = recharge.getSn();
				// String total_fee = new String();

				// 付款金额
				// total_fee = String.valueOf(price);
				// total_fee = "0.01";

			} else if ("2".equals(type)) {// 购买会员卡

				WashMemberCard washMemberCard = new WashMemberCard();
				long create_time = System.currentTimeMillis();

				WashCard washCard = this.washCardManager.getWashCard(sign_id);

				washMemberCard.setCard_type(washCard.getType());
				washMemberCard.setCreate_time(create_time);
				washMemberCard.setEnd_time(0L);
				washMemberCard.setImage(washCard.getImage());
				washMemberCard.setMember_id(Integer.parseInt(member_id));
				washMemberCard.setPay_type(2);
				washMemberCard.setPrice(washCard.getPrice());
				washMemberCard.setSn(this.createSn());
				washMemberCard.setWash_card_id(washCard.getWash_card_id());
				washMemberCard.setWash_day(washCard.getWash_day());
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DAY_OF_MONTH, washCard.getWash_day());
				long end_time = calendar.getTimeInMillis();
				washMemberCard.setEnd_time(end_time);

				this.washMemberCardManager.addWashMemberCard(washMemberCard);

				sn = washMemberCard.getSn();
				// String total_fee = new String();
				price = washCard.getPrice();
				// 付款金额
				// total_fee = String.valueOf(washCard.getPrice());
				// total_fee = "0.01";

			} else if ("3".equals(type)) {// 支付订单
				Order order = this.orderManager.get(Integer.parseInt(sign_id));
				String total_fee = new String();

				sn = order.getSn();
				price = order.getNeedPayMoney();
				// 付款金额
				// total_fee = String.valueOf(order.getNeedPayMoney());
				// total_fee = "0.01";
			}
			// price = 0.01;
			// notify_url =
			// "http://www.wx0598.com/api/shop/shicai_wxMobilePayPlugin_payment-callback.do";//回调地址
			String order_type = DetailUtil.getOrderType(type);
			notify_url = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath()
					+ "/api/shop/" + order_type + "_mpMobilePayPlugin_payment-callback.do";// 正式环境
			Map map = orderManager.getMppayParam(sn, price, notify_url, Integer.valueOf(member_id));
			if (map.get("error") != null) {
				json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.SERVER_ERROR, "2",
						jsonData.toString());
				ResponseUtils.renderJson(response, json.toString());
			} else {
				System.out.println("公众号支付responseJson："+map);
				jsonData.put("pay_message", map);
				json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1",
						jsonData.toString());
				ResponseUtils.renderJson(response, json.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("微信获取APP支付信息出错！错误信息：" + StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}
	}

	/**
	 * 获取微信jssdk信息
	 */
	public void getJssdk() {
		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();
		String url = request.getParameter("url");// 地址
		if (StringUtil.isEmpty(url)) {
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.PARAMETER_ERROR, "2",
					jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return;
		}
		try {
			String receiveMsg = PastUtil.getParam(request, url);
			jsonData.put("receiveMsg", receiveMsg);
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1",
					jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("微信获取APP支付信息出错！错误信息：" + StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}
	}

}
