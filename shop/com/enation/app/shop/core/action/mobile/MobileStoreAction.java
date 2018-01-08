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

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IStoreAdminManager;
import com.enation.app.base.core.service.IPayTrackingManager;
import com.enation.app.shop.core.constant.CXConstant;
import com.enation.app.shop.core.constant.ReturnMsg;
import com.enation.app.shop.core.model.StoreAdmin;
import com.enation.app.shop.core.model.PayTracking;
import com.enation.app.shop.core.service.IDictionaryManager;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.app.shop.core.utils.DateUtil;
import com.enation.app.shop.core.utils.ResponseUtils;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

/**
 * 商家二维码支付接口
 * @author yexf
 * 2017-12-10
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/shop")
@Action("mobileStore")
public class MobileStoreAction extends WWAction{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private IMemberManager memberManager;
	@Autowired
	private IDictionaryManager dictionaryManager;
	@Autowired
	private IPayTrackingManager payTrackingManager;
	@Autowired
	private IStoreAdminManager storeAdminManager;
	
	/**
	 * 6、获取商家扣款信息
	 * @author yexf
	 * 2017-11-26
	 * @return
	 */
	public void getStoreSubMessage(){
		
		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		String store_admin_id = request.getParameter("store_admin_id"); // 商家管理员id
		
		try{
		
			/*if(validateLoginUtil.checkLogin(request, response)){
				return;
			}*/
			
			if(StringUtil.isEmpty(store_admin_id)){
				json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.PARAMETER_ERROR, "2", jsonData.toString());
				ResponseUtils.renderJson(response, json.toString());
				return;
			}
			
			//String member_id = request.getHeader("member_id");
			
			StoreAdmin storeAdmin = this.storeAdminManager.getStoreAdminById(store_admin_id);
			
			if(storeAdmin == null){
				json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.STORE_ADMIN_EXISTENT, "2", jsonData.toString());
				ResponseUtils.renderJson(response, json.toString());
				return;
			}
			
			jsonData.put("store_name", storeAdmin.getStore_name()); // 商家店铺名称
			jsonData.put("qrcode_price", storeAdmin.getQrcode_price()); // 扣款金额
			
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("获取商家扣款信息出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}
		
	}
	
	/**
	 * 7、扣款给商家
	 * @author yexf
	 * 2017-11-26
	 * @return
	 */
	public void subPriceToStore(){
		
		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		String store_admin_id = request.getParameter("store_admin_id"); // 商家管理员id
		
		try{
		
			/*if(validateLoginUtil.checkLogin(request, response)){
				return;
			}*/
			String member_id = request.getHeader("member_id"); // member_id = "83";
			
			if(StringUtil.isEmpty(store_admin_id) || StringUtil.isEmpty(member_id)){
				json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.PARAMETER_ERROR, "2", jsonData.toString());
				ResponseUtils.renderJson(response, json.toString());
				return;
			}
			
			StoreAdmin storeAdmin = this.storeAdminManager.getStoreAdminById(store_admin_id);
			Member member = this.memberManager.getMemberByMemberId(member_id);
			
			if(member == null){
				json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.USER_NO_EXISTENT, "2", jsonData.toString());
				ResponseUtils.renderJson(response, json.toString());
				return;
			}
			
			if(storeAdmin == null){
				json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.STORE_ADMIN_EXISTENT, "2", jsonData.toString());
				ResponseUtils.renderJson(response, json.toString());
				return;
			}
			
			if(member.getBalance() < storeAdmin.getQrcode_price()){
				json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.BALANCE_LESS, "3", jsonData.toString());
				ResponseUtils.renderJson(response, json.toString());
				return;
			}
			
			PayTracking spl = new PayTracking();
			spl.setCreate_time(System.currentTimeMillis());
			spl.setMember_id(Integer.parseInt(member_id));
			spl.setPrice(storeAdmin.getQrcode_price());
			spl.setStore_admin_id(storeAdmin.getStore_admin_id());
			
			this.payTrackingManager.addPayTrackingAndUpdMember(spl);
			
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("扣款给商家出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}
		
	}
	
	/**
	 * 8、商家支付列表
	 * @author yexf
	 * 2017-11-26
	 * @return
	 */
	public void getStorePayList(){
		
		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		String page_no = request.getParameter("page_no");
		String page_size = request.getParameter("page_size");
		
		page_no = StringUtil.isEmpty(page_no) ? "1" : page_no;
		page_size = StringUtil.isEmpty(page_size) ? "10" : page_size;
		
		try{
		
			/*if(validateLoginUtil.checkLogin(request, response)){
				return;
			}*/
			String member_id = request.getHeader("member_id"); // member_id = "83";
			
			if(StringUtil.isEmpty(member_id)){
				json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.PARAMETER_ERROR, "2", jsonData.toString());
				ResponseUtils.renderJson(response, json.toString());
				return;
			}
			
			Page storePayPage = this.payTrackingManager.getStorePayPage(member_id, page_no, page_size);
			
			@SuppressWarnings("unchecked")
			List<PayTracking> recordList = (List<PayTracking>) storePayPage.getResult();
			if(recordList != null && recordList.size() !=0 ){
				for(PayTracking pt : recordList){
					JSONObject srJson = new JSONObject();
					srJson.put("pay_tracking_id", pt.getPay_tracking_id()); // 商家付款记录id
					srJson.put("store_name", pt.getStore_name());
					srJson.put("price", pt.getPrice());
					String time = DateUtil.LongToString(pt.getCreate_time(), "yyyy-MM-dd HH:mm");
					srJson.put("create_time", time);
					jsonArray.add(srJson);
				}
			}
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonArray.toString());
			ResponseUtils.renderJson(response, json.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("商家支付列表出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}
		
	}
	
	

	public IMemberManager getMemberManager() {
		return memberManager;
	}

	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}

	public IDictionaryManager getDictionaryManager() {
		return dictionaryManager;
	}

	public void setDictionaryManager(IDictionaryManager dictionaryManager) {
		this.dictionaryManager = dictionaryManager;
	}

	public IStoreAdminManager getStoreAdminManager() {
		return storeAdminManager;
	}

	public void setStoreAdminManager(IStoreAdminManager storeAdminManager) {
		this.storeAdminManager = storeAdminManager;
	}

	public IPayTrackingManager getPayTrackingManager() {
		return payTrackingManager;
	}

	public void setPayTrackingManager(IPayTrackingManager payTrackingManager) {
		this.payTrackingManager = payTrackingManager;
	}
	
	
	
}
