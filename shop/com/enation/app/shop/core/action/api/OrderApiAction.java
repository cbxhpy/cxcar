package com.enation.app.shop.core.action.api;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MemberAddress;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.service.IMemberAddressManager;
import com.enation.app.shop.core.service.IOrderFlowManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.eop.processor.core.RemoteRequest;
import com.enation.eop.processor.core.Request;
import com.enation.eop.processor.core.Response;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.StringUtil;

/**
 * 订单api
 * @author kingapex
 *2013-7-24下午9:27:47
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/shop")
@Action("order")
@Results({
	@Result(name="kuaidi", type="freemarker", location="/themes/default/member/order_kuaidi.html") 
})
public class OrderApiAction extends WWAction {
	
	private IOrderFlowManager orderFlowManager;
	private IOrderManager orderManager;
	private IMemberAddressManager memberAddressManager;
	private Map kuaidiResult;
	
	public String createWbl(){
		try{
			Order order  = this.createOrderWbl();
			
			this.json = JsonMessageUtil.getObjectJson(order,"order");
			
		}catch(RuntimeException e){
			e.printStackTrace();
			this.logger.error("创建订单出错", e);
			this.showErrorJson(e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}
	
	/**
	 * 提交订单，创建订单
	 * @author yexf
	 * 2017-3-6
	 * @return
	 */
	private Order createOrderWbl(){
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		
		Member member = UserServiceFactory.getUserService().getCurrentMember();
		
		if(member == null){
 			throw new RuntimeException("请先登录");
 		}
		
		Integer payment_id = StringUtil.toInt(request.getParameter("payment_id"),0);
 		if(payment_id == 0){
 			throw new RuntimeException("支付方式不能为空");
 		}
 		
 		String cart_ids = request.getParameter("cart_ids");
 		if(StringUtil.isEmpty(cart_ids)){
 			throw new RuntimeException("购买商品为空");
 		}
		
		Order order = new Order() ;
		order.setPayment_id(payment_id);//支付方式
		String address_id = request.getParameter("address_id");
 
		MemberAddress memberAddress = this.memberAddressManager.getAddress(Integer.parseInt(address_id));
		
		if(memberAddress != null){
			order.setShip_provinceid(memberAddress.getProvince_id());
			order.setShip_cityid(memberAddress.getCity_id());
			order.setShip_regionid(memberAddress.getRegion_id());
			order.setShip_addr(memberAddress.getAddr());
			order.setShip_mobile(memberAddress.getMobile());
			order.setShipping_area(memberAddress.getProvince()+"-"+ memberAddress.getCity()+"-"+ memberAddress.getRegion());
			order.setShip_name(memberAddress.getName());
			order.setRegionid(memberAddress.getRegion_id());
			order.setMemberAddress(memberAddress);
			order.setAddress_id(memberAddress.getAddr_id());
		}else{
			throw new RuntimeException("收货地址不存在");
		}
		String remark = request.getParameter("remark");
		
		order.setRemark(remark);
		order.setMember_id(member.getMember_id());
		
		return this.orderManager.appAdd(order, cart_ids);
		
	}
	
	/**
	 * 创建订单，需要购物车中有商品
	 * @param address_id:收货地址id.int型，必填项
	 * @param payment_id:支付方式id，int型，必填项
	 * @param shipping_id:配送方式id，int型，必填项
	 * @param shipDay：配送时间，String型 ，可选项
	 * @param shipTime，String型 ，可选项
	 * @param remark，String型 ，可选项
	 * 
	 * @return 返回json串
	 * result  为1表示添加成功0表示失败 ，int型
	 * message 为提示信息
	 * 
	 */
	public String create(){
		try{
			Order order  = this.createOrder();
			
			this.json = JsonMessageUtil.getObjectJson(order,"order");
			
		}catch(RuntimeException e){
			e.printStackTrace();
			this.logger.error("创建订单出错", e);
			this.showErrorJson(e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}
	
	/**
	 * 取消订单
	 * @param sn:订单序列号.String型，必填项
	 * 
	 * @return 返回json串
	 * result  为1表示添加成功0表示失败 ，int型
	 * message 为提示信息
	 */
	
	public String cancel() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		try {
			String sn = request.getParameter("sn");
			String reason = request.getParameter("reason");
			Member member = UserServiceFactory.getUserService().getCurrentMember();
			if (member == null) {
				this.showErrorJson("取消订单失败：登录超时");
			} else {
				this.orderFlowManager.cancel(sn, reason);
				this.showSuccessJson("取消订单成功");
			}
		} catch (RuntimeException re) {
			this.showErrorJson(re.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}
	
	/**
	 * 取消订单
	 * @author yexf
	 * 2017-3-6
	 * @return
	 */
	public String cancelWbl() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		try {
			String order_id = request.getParameter("order_id");
			Member member = UserServiceFactory.getUserService().getCurrentMember();
			if (member == null) {
				this.showErrorJson("取消订单失败：登录超时");
			} else {
				this.orderFlowManager.appCancel(order_id, member.getMember_id().toString());
				this.showSuccessJson("取消订单成功");
			}
		} catch (RuntimeException re) {
			this.showErrorJson(re.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}
	
	/**
	 * 删除订单
	 * @author yexf
	 * 2017-3-6
	 * @return
	 */
	public String deleteWbl() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		try {
			String order_id = request.getParameter("order_id");
			Member member = UserServiceFactory.getUserService().getCurrentMember();
			if (member == null) {
				this.showErrorJson("删除订单失败：登录超时");
			} else {
				this.orderFlowManager.deleteOrder(order_id, member.getMember_id().toString());
				this.showSuccessJson("删除订单成功");
			}
		} catch (RuntimeException re) {
			this.showErrorJson(re.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}
	
	/**
	 * 确认收货
	 * @param orderId:订单id.String型，必填项
	 * 
	 * @return 返回json串
	 * result  为1表示添加成功0表示失败 ，int型
	 * message 为提示信息
	 */
	public String rogConfirm() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		try {
			String orderId = request.getParameter("orderId");
			Member member = UserServiceFactory.getUserService().getCurrentMember();
			if(member==null){
				this.showErrorJson("取消订单失败：登录超时");
			} else {
				this.orderFlowManager.rogConfirm(Integer.parseInt(orderId), member.getMember_id(), member.getUname(), member.getUname(),DateUtil.getDatelineLong());
				this.showSuccessJson("确认收货成功");
			}
		} catch (Exception e) {
			this.showErrorJson("数据库错误");
		}
		return WWAction.JSON_MESSAGE;
	}
	
	/**
	 * 确认收货
	 * @author yexf
	 * 2017-3-6
	 * @return
	 */
	public String rogConfirmWbl() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		try {
			String order_id = request.getParameter("order_id");
			Member member = UserServiceFactory.getUserService().getCurrentMember();
			if(member==null){
				this.showErrorJson("确认收货失败：登录超时");
			} else {
				String name = StringUtil.isEmpty(member.getName()) ? member.getUname() : member.getName();
				this.orderFlowManager.appRogConfirm(order_id, member.getMember_id(), name, name,DateUtil.getDatelineLong());
				this.showSuccessJson("确认收货成功");
			}
		} catch (Exception e) {
			this.showErrorJson("服务器出错");
		}
		return WWAction.JSON_MESSAGE;
	}
	
	/**************以下非api，不用书写文档**************/
	
	private Order createOrder(){
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		
		
		Integer shippingId = StringUtil.toInt(request.getParameter("typeId"),null);
 		if(shippingId==null ) throw new RuntimeException("配送方式不能为空");
		
		Integer paymentId = StringUtil.toInt(request.getParameter("paymentId"),0);
 		if(paymentId==0 ) throw new RuntimeException("支付方式不能为空");
		
		Order order = new Order() ;
		order.setShipping_id(shippingId); //配送方式
		order.setPayment_id(paymentId);//支付方式
		Integer addressId = StringUtil.toInt(request.getParameter("addressId"), false);
		MemberAddress address = new MemberAddress();

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
					addressId= this.memberAddressManager.addAddress(address);
			}
		}
//		}
	 	
 	 	address.setAddr_id(addressId);
	 	order.setMemberAddress(address);
		order.setShip_day(request.getParameter("shipDay"));
		order.setShip_time(request.getParameter("shipTime"));
		order.setRemark(request.getParameter("remark"));
		order.setAddress_id(address.getAddr_id());//保存本订单的会员id
		return	this.orderManager.add(order,request.getSession().getId());
		
	}
	
	private MemberAddress createAddress(){
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		
		MemberAddress address = new MemberAddress();
 

		String name = request.getParameter("shipName");
		address.setName(name);

		String tel = request.getParameter("shipTel");
		address.setTel(tel);

		String mobile = request.getParameter("shipMobile");
		address.setMobile(mobile);

		String province_id = request.getParameter("province_id");
		if(province_id!=null){
			address.setProvince_id(Integer.valueOf(province_id));
		}

		String city_id = request.getParameter("city_id");
		if(city_id!=null){
			address.setCity_id(Integer.valueOf(city_id));
		}

		String region_id = request.getParameter("region_id");
		if(region_id!=null){
			address.setRegion_id(Integer.valueOf(region_id));
		}

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

	public String orderKuaidi(){
		try {
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			String logino = request.getParameter("logino");//物流号
			String code = request.getParameter("code");//物流公司代码
			if(logino==null || logino.length()<5){
				Map result = new HashMap();
				result.put("status", "-1");
				this.showErrorJson("请输入正确的运单号");
				return "";
			}
			if(code == null || code.equals("")){
				code = "yuantong";
			}
			Request remoteRequest  = new RemoteRequest();
			String kuaidiurl="http://www.kuaidiapi.cn/rest/?uid=34210&key=ceea91ab561640979b75e78a0cbd5128&order="+logino+"&id="+code;
			//System.out.println(kuaidiurl);
			Response remoteResponse = remoteRequest.execute(kuaidiurl);
			String content  = remoteResponse.getContent();
			kuaidiResult = (Map)JSONObject.toBean( JSONObject.fromObject(content) ,Map.class);
			
		} catch (Exception e) {
			this.logger.error("查询货运状态", e);
		}
		return "kuaidi";
	}

	//set get
	public IOrderManager getOrderManager() {
		return orderManager;
	}


	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}


	public IMemberAddressManager getMemberAddressManager() {
		return memberAddressManager;
	}


	public void setMemberAddressManager(IMemberAddressManager memberAddressManager) {
		this.memberAddressManager = memberAddressManager;
	}

	public IOrderFlowManager getOrderFlowManager() {
		return orderFlowManager;
	}

	public void setOrderFlowManager(IOrderFlowManager orderFlowManager) {
		this.orderFlowManager = orderFlowManager;
	}

	public Map getKuaidiResult() {
		return kuaidiResult;
	}

	public void setKuaidiResult(Map kuaidiResult) {
		this.kuaidiResult = kuaidiResult;
	}
	
}
