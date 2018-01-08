package com.enation.app.shop.core.action.mobile;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.MemberAddress;
import com.enation.app.shop.core.service.IMemberAddressManager;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.utils.RequestHeaderUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.StringUtil;

/**
 * 用户收货地址接口
 * @author yexf
 * 2016-10-17
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/shop")
@Action("mobileMemberAddress")
public class MobileMemberAddressAction extends WWAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private IOrderManager orderManager;
	
	@Autowired
	private IMemberManager memberManager;
	
	@Autowired
	private IMemberAddressManager memberAddressManager;
	
	/**
	 * 收货地址列表
	 * @author yexf
	 * 2016-10-17
	 * @return
	 */
	public String getAddressList(){
		
		//JSONObject json = new JSONObject();
		//JSONObject jsonData = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();
		
		String rh = RequestHeaderUtil.requestHeader(request, response);
		
		String member_id = request.getHeader("u");
		//member_id = request.getParameter("member_id");
		//String token = request.getHeader("k");
		//String platform = request.getHeader("p");//系统
		//String version = request.getHeader("v");//版本号
		
		if(rh!=null && rh=="1"){
			this.renderJson("-99", "用户未登录", "[]");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="2"){
			this.renderJson("-99", "您账号在其他设备登陆", "[]");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="3"){
			this.renderJson("1", "参数验证错误", "[]");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="4"){
			this.renderJson("1", "请升级到最新版本", "[]");
			return WWAction.APPLICAXTION_JSON;
		}
		
		try{	
			
			List<MemberAddress> addressList = memberAddressManager.listAddress(member_id);
			
			if(addressList.size()>0){
				for(int i = 0 ; i < addressList.size() ; i ++){
					JSONObject addrJson = new JSONObject();
					MemberAddress ma = addressList.get(i);
					addrJson.put("addr_id", ma.getAddr_id());
					addrJson.put("def_addr", ma.getDef_addr());
					addrJson.put("name", ma.getName());
					addrJson.put("mobile", ma.getMobile());
					addrJson.put("addr", ma.getAddr());
					addrJson.put("province_id", ma.getProvince_id());
					addrJson.put("province", ma.getProvince());
					addrJson.put("city_id", ma.getCity_id());
					addrJson.put("city", ma.getCity());
					addrJson.put("region_id", ma.getRegion_id());
					addrJson.put("region", ma.getRegion());
					jsonArray.add(addrJson);
				}
				this.renderJson("0", "获取成功", jsonArray.toString());
				return WWAction.APPLICAXTION_JSON;
				
			}else{
				this.renderJson("0", "地址为空", jsonArray.toString());
				return WWAction.APPLICAXTION_JSON;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			if(StringUtil.isEmpty(message) || message.equals("null")){
				message = "服务器出错，请重试";
			}
			logger.error("收货地址列表出错！错误信息："+message);
			this.renderJson("1", message, "[]");
			return WWAction.APPLICAXTION_JSON;
		}
	}
	
	/**
	 * 修改/新增收货地址
	 * @author yexf
	 * 2016-10-17
	 * http://localhost:8080/lysks/shop/mobileMemberAddress!editOrAddAddress.do?type=1&name=1&mobile=1&province_id=1&province=1&city_id=1&city=1&region_id=1&region=1&addr=1&def_addr=0&member_id=72
	 * @return
	 */
	public String editOrAddAddress(){
		
		//JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		//JSONArray jsonArray = new JSONArray();
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();
		
		//String rh = RequestHeaderUtil.requestHeader(request, response);
		
		String member_id = request.getHeader("u");
		//member_id = request.getParameter("member_id");
		//String token = request.getHeader("k");
		//String platform = request.getHeader("p");//系统
		//String version = request.getHeader("v");//版本号
		
		/*if(rh!=null && rh=="1"){
			this.renderJson("-99", "用户未登录", "");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="2"){
			this.renderJson("-99", "您账号在其他设备登陆", "");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="3"){
			this.renderJson("1", "参数验证错误", "");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="4"){
			this.renderJson("1", "请升级到最新版本", "");
			return WWAction.APPLICAXTION_JSON;
		}*/
		
		try{	
			
			String type = request.getParameter("type");//0：修改 1：新增
			
			if(StringUtil.isEmpty(type)){
				this.renderJson("1", "参数有误", "");
				return WWAction.APPLICAXTION_JSON;
			}
			
			String addr_id = request.getParameter("addr_id");
			String name = StringUtil.isNull(request.getParameter("name"));
			String mobile = StringUtil.isNull(request.getParameter("mobile"));
			String province_id = StringUtil.isNull(request.getParameter("province_id"));
			String province = StringUtil.isNull(request.getParameter("province"));
			String city_id = StringUtil.isNull(request.getParameter("city_id"));
			String city = StringUtil.isNull(request.getParameter("city"));
			String region_id = StringUtil.isNull(request.getParameter("region_id"));
			String region = StringUtil.isNull(request.getParameter("region"));
			String addr = StringUtil.isNull(request.getParameter("addr"));
			String def_addr = StringUtil.isNull(request.getParameter("def_addr"));
			
			if(StringUtil.isEmpty(name) || StringUtil.isEmpty(mobile) || StringUtil.isEmpty(province_id) || 
					StringUtil.isEmpty(province) || StringUtil.isEmpty(city_id) || StringUtil.isEmpty(city) || 
						StringUtil.isEmpty(region_id) || StringUtil.isEmpty(region) || StringUtil.isEmpty(addr) ||
									StringUtil.isEmpty(def_addr)){
				this.renderJson("1", "参数有误", "");
				return WWAction.APPLICAXTION_JSON;
			}
			
			if("0".equals(type)){//修改
				
				MemberAddress ma = this.memberAddressManager.getAddress(StringUtil.toInt(addr_id, 0));
				if(ma==null){
					this.renderJson("1", "未获取到地址对象", "");
					return WWAction.APPLICAXTION_JSON;
				}else{
					ma.setName(name);
					ma.setMobile(mobile);
					ma.setProvince(province);
					ma.setProvince_id(StringUtil.toInt(province_id,0));
					ma.setCity(city);
					ma.setCity_id(StringUtil.toInt(city_id,0));
					ma.setRegion(region);
					ma.setRegion_id(StringUtil.toInt(region_id,0));
					ma.setAddr(addr);
					ma.setDef_addr(StringUtil.toInt(def_addr,0));
					
					this.memberAddressManager.appUpdateAddress(ma);
					
					jsonData.put("addr_id", addr_id);
					
					this.renderJson("0", "修改成功", jsonData.toString());
					return WWAction.APPLICAXTION_JSON;
				}
				
			}else if("1".equals(type)){//新增
				
				MemberAddress ma = new MemberAddress();
				
				ma.setDef_addr(StringUtil.toInt(def_addr, 0));
				ma.setName(name);
				ma.setMobile(mobile);
				ma.setProvince(province);
				ma.setProvince_id(StringUtil.toInt(province_id, 0));
				ma.setCity(city);
				ma.setCity_id(StringUtil.toInt(city_id, 0));
				ma.setRegion(region);
				ma.setRegion_id(StringUtil.toInt(region_id, 0));
				ma.setAddr(addr);
				ma.setMember_id(Integer.parseInt(member_id));
				
				int result = this.memberAddressManager.appAddAddress(ma);
				
				jsonData.put("addr_id", StringUtil.isNull(result));
				
				this.renderJson("0", "新增成功", jsonData.toString());
				return WWAction.APPLICAXTION_JSON;
				
			}else{
				this.renderJson("1", "参数有误", "");
				return WWAction.APPLICAXTION_JSON;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			if(StringUtil.isEmpty(message) || message.equals("null")){
				message = "服务器出错，请重试";
			}
			logger.error("修改/新增收货地址出错！错误信息："+message);
			this.renderJson("1", message, "");
			return WWAction.APPLICAXTION_JSON;
		}
	}
	
	
	/**
	 * 删除收货地址
	 * @author yexf
	 * 2016-10-17
	 * @return
	 */
	public String deleteAddress(){
		
		//JSONObject json = new JSONObject();
		//JSONObject jsonData = new JSONObject();
		//JSONArray jsonArray = new JSONArray();
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();
		
		String rh = RequestHeaderUtil.requestHeader(request, response);
		
		String member_id = request.getHeader("u");
		//member_id = request.getParameter("member_id");
		//String token = request.getHeader("k");
		//String platform = request.getHeader("p");//系统
		//String version = request.getHeader("v");//版本号
		
		if(rh!=null && rh=="1"){
			this.renderJson("-99", "用户未登录", "");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="2"){
			this.renderJson("-99", "您账号在其他设备登陆", "");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="3"){
			this.renderJson("1", "参数验证错误", "");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="4"){
			this.renderJson("1", "请升级到最新版本", "");
			return WWAction.APPLICAXTION_JSON;
		}
		
		try{	
			
			String addr_id = request.getParameter("addr_id");//0：修改 1：新增
			
			if(StringUtil.isEmpty(addr_id)){
				this.renderJson("1", "参数有误", "");
				return WWAction.APPLICAXTION_JSON;
			}
			
			this.memberAddressManager.deleteAddress(Integer.parseInt(addr_id));
			
			this.renderJson("0", "删除成功", "");
			return WWAction.APPLICAXTION_JSON;
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			if(StringUtil.isEmpty(message) || message.equals("null")){
				message = "服务器出错，请重试";
			}
			logger.error("删除收货地址出错！错误信息："+message);
			this.renderJson("1", message, "");
			return WWAction.APPLICAXTION_JSON;
		}
	}

	
	
}
