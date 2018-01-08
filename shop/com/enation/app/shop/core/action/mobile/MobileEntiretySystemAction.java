package com.enation.app.shop.core.action.mobile;

import java.util.List;
import java.util.Map;

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

import com.enation.app.base.core.model.Adv;
import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.Regions;
import com.enation.app.base.core.service.IAdvManager;
import com.enation.app.base.core.service.IAppVersionManager;
import com.enation.app.base.core.service.IRegionsManager;
import com.enation.app.cms.core.service.IDataManager;
import com.enation.app.shop.core.constant.CXConstant;
import com.enation.app.shop.core.constant.ReturnMsg;
import com.enation.app.shop.core.model.AppVersion;
import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.model.PayEnable;
import com.enation.app.shop.core.model.PushMsg;
import com.enation.app.shop.core.service.IDictionaryManager;
import com.enation.app.shop.core.service.IGoodsCatManager;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IPushMsgManager;
import com.enation.app.shop.core.service.IWashMemberCardManager;
import com.enation.app.shop.core.utils.RequestHeaderUtil;
import com.enation.app.shop.core.utils.ResponseUtils;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

/** 
 * @ClassName: MobileEntiretySystemAction 
 * @Description: 系统数据接口
 * @author yexf
 * @date 2017-5-4 下午6:15:43  
 */ 
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/shop")
@Action("mobileEntiretySystem")
public class MobileEntiretySystemAction extends WWAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Autowired
	private IOrderManager orderManager;
	@Autowired
	private IAdvManager advManager;
	@Autowired
	private IDataManager dataManager;
	@Autowired
	private IAppVersionManager appVersionManager;
	
	private IGoodsCatManager goodsCatManager;
	private IRegionsManager regionsManager;
	
	@Autowired
	private IPushMsgManager pushMsgManager;
	@Autowired
	private IGoodsManager goodsManager;
	
	@Autowired
	private IWashMemberCardManager washMemberCardManager;
	@Autowired
	private IDictionaryManager dictionaryManager;
	@Autowired
	private IMemberManager memberManager;
	
	/**
	 * 17、获取充值金额列表
	 * @author yexf
	 * 2017-4-16
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public void getRechargeList(){
		
		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		try{
		
			/*if(validateLoginUtil.checkLogin(request, response)){
				return;
			}*/
			
			String member_id = request.getHeader("member_id");

			Member member = this.memberManager.getMemberByMemberId(member_id);
			List<Map> rechargeList = null;
			if(member.getParent_id() == null || member.getParent_id() == 0){
				rechargeList = this.dictionaryManager.getDataList("recharge_list");
			}else{
				// 推广
				rechargeList = this.dictionaryManager.getDataList("recharge_spread_list");
			}
			
			if(rechargeList != null && rechargeList.size() !=0 ){
				for(Map m : rechargeList){
					JSONObject mJson = new JSONObject();
					String v_str = StringUtil.isNull(m.get("d_value"));
					String charge_price = new String();
					String get_price = new String();
					if(!StringUtil.isEmpty(v_str)){
						String[] get_prices = v_str.split(",");
						if(get_prices.length >= 3){
							charge_price = get_prices[0];
							get_price = get_prices[1];
						}
					}
					mJson.put("charge_price", charge_price);
					mJson.put("get_price", get_price);
					mJson.put("id", m.get("dictionary_id"));
					jsonArray.add(mJson);
				}
			}
			
			jsonData.put("charge_list", jsonArray);
			jsonData.put("balance", member.getBalance());//我的余额
			
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("获取充值金额列表出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}
		
	}
	
	/**
	 * 2.37点击推送消息
	 * @return
	 */
	public String seePushMsg(){
		
		//JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
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
			this.renderJson("-99", "用户未登录", "");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="2"){
			this.renderJson("-99", "您账号在其他设备登陆", "");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="3"){
			this.renderJson("1", "参数验证错误！", "");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="4"){
			this.renderJson("1", "请升级到最新版本", "");
			return WWAction.APPLICAXTION_JSON;
		}
		
		try{
			
			String push_msg_id = request.getParameter("push_msg_id");
			
			this.pushMsgManager.insertMemberPush(Integer.parseInt(member_id), push_msg_id);
			
			this.renderJson("0", "获取成功", jsonData.toString());
			return WWAction.APPLICAXTION_JSON;
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			if(StringUtil.isEmpty(message) || message.equals("null")){
				message = "服务器出错，请重试";
			}
			logger.error("微信获取APP支付信息出错！错误信息："+message);
			this.renderJson("1", message, "");
			return WWAction.APPLICAXTION_JSON;
		}
	}
	
	/**
	 * 2.36推送信息列表
	 * @return
	 */
	public String pushMsgList(){
		
		//JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
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
			this.renderJson("-99", "用户未登录", "");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="2"){
			this.renderJson("-99", "您账号在其他设备登陆", "");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="3"){
			this.renderJson("1", "参数验证错误！", "");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="4"){
			this.renderJson("1", "请升级到最新版本", "");
			return WWAction.APPLICAXTION_JSON;
		}
		
		try{
			
			String page_no = request.getParameter("page_no");
			String page_size = request.getParameter("page_size");
			
			
			page_no = StringUtil.isEmpty(page_no) ? "1" : page_no;
			page_size = StringUtil.isEmpty(page_size) ? "10" : page_size;
			
			
			Page pushPage = this.pushMsgManager.pagePushMsgByMemberId(member_id, null, Integer.parseInt(page_no), Integer.parseInt(page_size));
			List<PushMsg> pushList = (List<PushMsg>)pushPage.getResult();
			
			if(pushList.size() != 0){
				for(PushMsg pm : pushList){
					JSONObject pushJson = new JSONObject();
					pushJson.put("push_msg_id", pm.getPush_msg_id());
					pushJson.put("title", pm.getTitle());
					pushJson.put("content", pm.getContent());
					pushJson.put("skip_type", pm.getSkip_type());
					Goods goods = new Goods();
					if("1".equals(pm.getSkip_type())){
						try {
							goods = this.goodsManager.getGoodBySn(pm.getGoods_sn());
						} catch (Exception e) {
							goods = null;
						}
						if(goods == null){
							pushJson.put("goods_id", "");
						}else{
							pushJson.put("goods_id", goods.getGoods_id());
						}
					}else{
						pushJson.put("goods_id", "");
					}
					pushJson.put("html_url", pm.getHtml_url());
					String create_time = DateUtil.toString(pm.getCreate_time(), "MM月dd日");
					pushJson.put("create_time", create_time);
					// 图片地址转换 fs->服务器地址
					String temp = UploadUtil.replacePath(pm.getImage());
					pushJson.put("image", temp);
					String is_see = "0";
					if("0".equals(pm.getSkip_type())){
						is_see = "1";
					}else{
						if(!StringUtil.isEmpty(pm.getMember_id()) && !"0".equals(pm.getMember_id())){
							is_see = "1";
						}
					}
					pushJson.put("is_see", is_see);
					jsonArray.add(pushJson);
				}
			}
			
			this.renderJson("0", "获取成功", jsonArray.toString());
			return WWAction.APPLICAXTION_JSON;
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			if(StringUtil.isEmpty(message) || message.equals("null")){
				message = "服务器出错，请重试";
			}
			logger.error("微信获取APP支付信息出错！错误信息："+message);
			this.renderJson("1", message, "");
			return WWAction.APPLICAXTION_JSON;
		}
	}
	
	/**
	 * 检验应用版本
	 * @return
	 */
	public void checkUpdate(){
		
		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();
		
		String platform = request.getHeader("platform");//平台（1：android 2：ios）
		String version_no = request.getHeader("version_no");//版本序号（ps：1）
		String version = request.getParameter("version");//版本号（ps：2.0.1）
		
		if(StringUtil.isEmpty(platform) || StringUtil.isEmpty(version_no)){
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.PARAMETER_ERROR, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}

		try{
		
			AppVersion appVersion = this.appVersionManager.getNewVersion(platform, version_no);
			
			if(appVersion != null){
				jsonData.put("version", appVersion.getVersion_num());
				jsonData.put("version_no", appVersion.getNum()+"");
				jsonData.put("update_type", appVersion.getUpdate_type()+"");
				jsonData.put("updata_url", appVersion.getUpdata_url());
				jsonData.put("content", appVersion.getContent());
				jsonData.put("file_size", appVersion.getFile_size());
			}else{
				/*jsonData.put("new_version", "");
				jsonData.put("num", "");
				jsonData.put("update_type", "0");
				jsonData.put("updata_url", "");
				jsonData.put("content", "");
				jsonData.put("file_size", "");*/
			}
				
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("检验应用版本出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}
	}
	
	/**
	 * 验证应用版本
	 * @return
	 */
	public String checkUpdate1(){
		
		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		//JSONArray jsonArray = new JSONArray();
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		//HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		
		String platform = request.getHeader("p");//系统
		//platform = request.getParameter("p");
		String version = request.getHeader("v");//版本号
		
		try{
			
			String num = request.getParameter("num");//版本序号
			
			if(StringUtil.isEmpty(num) || StringUtil.isEmpty(platform)){
				this.renderJson("0", "参数有误", jsonData.toString());
				return WWAction.APPLICAXTION_JSON;
			}
			
			
			AppVersion appVersion = this.appVersionManager.getNewVersion(platform, num);
			
			if(appVersion != null){
				jsonData.put("new_version", appVersion.getVersion_num());
				jsonData.put("num", appVersion.getNum()+"");
				jsonData.put("update_type", appVersion.getUpdate_type()+"");
				jsonData.put("updata_url", appVersion.getUpdata_url());
				jsonData.put("content", appVersion.getContent());
				jsonData.put("file_size", appVersion.getFile_size());
			}else{
				jsonData.put("new_version", "");
				jsonData.put("num", "");
				jsonData.put("update_type", "0");
				jsonData.put("updata_url", "");
				jsonData.put("content", "");
				jsonData.put("file_size", "");
			}
			if("2".equals(platform)){
				this.renderJson("0", "获取成功", jsonData.toString());
				return WWAction.APPLICAXTION_JSON;
			}else{
				
				if(appVersion != null){
					json.put("new_version", appVersion.getVersion_num());
					json.put("num", appVersion.getNum()+"");
					json.put("update_type", appVersion.getUpdate_type()+"");
					json.put("updata_url", appVersion.getUpdata_url());
					json.put("content", appVersion.getContent());
					json.put("file_size", appVersion.getFile_size());
				}else{
					json.put("new_version", "");
					json.put("num", "0");
					json.put("update_type", "0");
					json.put("updata_url", "");
					json.put("content", "");
					json.put("file_size", "");
				}
				
				json.put("Return", "0");
				json.put("Detail", "获取成功");
				json.put("Data", "");
				this.renderJson1(json.toString());
				return WWAction.APPLICAXTION_JSON;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			if(StringUtil.isEmpty(message) || message.equals("null")){
				message = "服务器出错，请重试";
			}
			logger.error("微信获取APP支付信息出错！错误信息："+message);
			this.renderJson("1", message, "");
			return WWAction.APPLICAXTION_JSON;
		}
	}
	
	/**
	 * 首页信息
	 * @return
	 */
	public String getHomeData(){
		
		//JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		//JSONArray jsonArray = new JSONArray();
		
		//HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		//HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		//String rh = RequestHeaderUtil.requestHeader(request, response);
		
		//String member_id = request.getHeader("u");
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
			this.renderJson("1", "参数验证错误！", "");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="4"){
			this.renderJson("1", "请升级到最新版本", "");
			return WWAction.APPLICAXTION_JSON;
		}*/
		
		try{
			
			JSONArray bannerArray = new JSONArray();//上方banner
			JSONArray noticeArray = new JSONArray();//公告
			JSONArray goodsCatArray = new JSONArray();//商品和分类
			JSONArray activityArray = new JSONArray();//下方活动
			
			List<Adv> bannerList = this.advManager.listAdvByKeyword("homebanner", "false");
			for(Adv adv : bannerList){
				JSONObject bannerJson = new JSONObject();
				bannerJson.put("adv_id", adv.getAid());
				bannerJson.put("name", adv.getAname());
				// 图片地址转换 fs->服务器地址
				String temp = UploadUtil.replacePath(adv.getAtturl());
				bannerJson.put("image", temp);
				bannerArray.add(bannerJson);
			}
			
			List<Map> noticeList = this.dataManager.listNoticeByDataCatName("noticeList", null);
			noticeArray = JSONArray.fromObject(noticeList);
			
			/*List<Map> goodsList = this.goodsManager.getGoodsByNavigation("1");
			for(Map goodsMap : goodsList){
				JSONObject goodsJson = new JSONObject();
				goodsJson.put("info_id", goodsMap.get("goods_id"));
				goodsJson.put("name", goodsMap.get("name"));
				// 图片地址转换 fs->服务器地址
				String temp = UploadUtil.replacePath(StringUtil.isNull(goodsMap.get("thumbnail")));
				goodsJson.put("image", temp);
				goodsJson.put("type", "1");//0：分类 1：商品
				goodsCatArray.add(goodsJson);
			}*/
			
			List<Map> catList = this.goodsCatManager.getFirstShowCat();
			for(Map catMap : catList){
				JSONObject catJson = new JSONObject();
				catJson.put("cat_id", catMap.get("cat_id"));
				catJson.put("name", catMap.get("name"));
				// 图片地址转换 fs->服务器地址
				String temp = UploadUtil.replacePath(StringUtil.isNull(catMap.get("image")));
				catJson.put("image", temp);
				//catJson.put("type", "0");//0：分类 1：商品
				goodsCatArray.add(catJson);
			}
			
			
			List<Adv> activityList = this.advManager.listAdvByKeyword("homeactivity", "false");
			for(Adv adv : activityList){
				JSONObject activityJson = new JSONObject();
				activityJson.put("adv_id", adv.getAid());
				activityJson.put("name", adv.getAname());
				// 图片地址转换 fs->服务器地址
				String temp = UploadUtil.replacePath(adv.getAtturl());
				activityJson.put("image", temp);
				activityArray.add(activityJson);
			}
			
			jsonData.put("bannerList", bannerArray);
			jsonData.put("noticeList", noticeArray);
			jsonData.put("goodsCatList", goodsCatArray);
			jsonData.put("activityList", activityArray);
			
			this.renderJson("0", "获取成功", jsonData.toString());
			return WWAction.APPLICAXTION_JSON;
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			if(StringUtil.isEmpty(message) || message.equals("null")){
				message = "服务器出错，请重试";
			}
			logger.error("微信获取APP支付信息出错！错误信息："+message);
			this.renderJson("1", message, "");
			return WWAction.APPLICAXTION_JSON;
		}
	}
	
	/**
	 * 获取省市区 
	 * @author yexf
	 * 2016-10-19
	 */
	@SuppressWarnings("unchecked")
	public String getRegionsData(){
		
		//JSONObject json = new JSONObject();
		JSONObject json5 = new JSONObject();
		JSONArray jsonArray = new JSONArray();

		//HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		//HttpServletResponse response = ThreadContextHolder.getHttpResponse();
		
		//String platform = request.getHeader("p");//系统
		//String version = request.getHeader("v");//版本号
		
		try{
			
			List<Regions> list = this.regionsManager.listProvince();
			
			for(Regions r : list){
				json5.put("region_id", r.getRegion_id());
				json5.put("local_name", r.getLocal_name());
				
				JSONArray jsonArray1 = new JSONArray();
				JSONObject json1 = new JSONObject();
				
				List<Regions> list1 = this.regionsManager.listCity(r.getRegion_id());
	
				for(Regions r1 : list1){
					
					json1.put("region_id", r1.getRegion_id());
					json1.put("local_name", r1.getLocal_name());
					
					//二期添加 城市全拼
					/*String spell = new String();
					char[] strChar = r1.getLocal_name().toCharArray();
					for (char aaa:strChar){
						String strs[] = PinyinHelper.toHanyuPinyinStringArray(aaa);
						if(strs.length!=0){
							spell += strs[0].substring(0, strs[0].length()-1);
						}
					}*/
					
					/*if(r1.getSpell()==null || "".equals(r1.getSpell())){
						json1.put("spell", "#");
					}else{
						json1.put("spell", StringUtil.isNull(r1.getSpell()));
					}*/
					
					//二期添加 城市首字母
					/*String letter = PinyinAPI.getPinYinHeadChar(r1.getLocal_name());
					if(StringUtil.isNull(r1.getSpell()).length()!=0){
						String str = StringUtil.isNull(r1.getSpell()).substring(0, 1);
						json1.put("letter", str.toUpperCase());
					}else{
						json1.put("letter", "#");
					}
					
					json1.put("is_hot", r1.getIs_hot());*/
					
					List<Regions> list2 = this.regionsManager.listRegion(r1.getRegion_id());
					
					JSONArray jsonArray2 = new JSONArray();
					JSONObject json2 = new JSONObject();
					
					for(Regions r2 : list2){
						json2.put("region_id", r2.getRegion_id());
						json2.put("local_name", r2.getLocal_name());
						jsonArray2.add(json2);
						
						json1.put("regionList", jsonArray2.toArray());
						
					}
					
					jsonArray1.add(json1);
					json5.put("cityList", jsonArray1.toArray());
					
				}
				
				jsonArray.add(json5);
			}
			
			this.renderJson("0", "获取成功", jsonArray.toString());
			return WWAction.APPLICAXTION_JSON;
		
		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			if(StringUtil.isEmpty(message) || message.equals("null")){
				message = "服务器出错，请重试";
			}
			logger.error("获取省市区出错！错误信息："+message);
			this.renderJson("1", message, "");
			return WWAction.APPLICAXTION_JSON;
		}
		
	}
	
	/**
	 * 微信获取APP支付信息
	 * @return
	 */
	public String getWxpayParam(){
		
		//JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		//JSONArray jsonArray = new JSONArray();
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		//HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		//String rh = RequestHeaderUtil.requestHeader(request, response);
		
		//String member_id = request.getHeader("u");
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
			this.renderJson("1", "参数验证错误！", "");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="4"){
			this.renderJson("1", "请升级到最新版本", "");
			return WWAction.APPLICAXTION_JSON;
		}*/
		
		try{
			
			String order_id = request.getParameter("order_id");
			
			String sn = "";
			Double price = 0.0;
			String notify_url = "";
			PayEnable order = null;
			
			if(order_id==null) {
				this.renderJson("1", "订单ID不能为空", "");
				return WWAction.APPLICAXTION_JSON;
			}

			order = this.orderManager.get(Integer.parseInt(order_id));
			if(order == null) {
				this.renderJson("1", "该订单不存在", "");
				return WWAction.APPLICAXTION_JSON;
			}
			
			sn = order.getSn();
			price = order.getNeedPayMoney();
			notify_url = "http://www.wx0598.com/api/shop/shicai_wxMobilePayPlugin_payment-callback.do";//回调地址
			
			/**
			 * 更新订单的付款方式
			 */
			
			
			Map map = this.orderManager.getWxpayParam(sn, price, notify_url);
			
			if(map.get("error")!=null){
				this.renderJson("1", "error", "");
				return WWAction.APPLICAXTION_JSON;
			}else{
				JSONObject pay_message = JSONObject.fromObject(map);
				jsonData.put("pay_message", pay_message);
				this.renderJson("0", "获取成功", jsonData.toString());
				return WWAction.APPLICAXTION_JSON;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			if(StringUtil.isEmpty(message) || message.equals("null")){
				message = "服务器出错，请重试";
			}
			logger.error("微信获取APP支付信息出错！错误信息："+message);
			this.renderJson("1", message, "");
			return WWAction.APPLICAXTION_JSON;
		}
	}

	public IRegionsManager getRegionsManager() {
		return regionsManager;
	}

	public void setRegionsManager(IRegionsManager regionsManager) {
		this.regionsManager = regionsManager;
	}

	public IGoodsCatManager getGoodsCatManager() {
		return goodsCatManager;
	}

	public void setGoodsCatManager(IGoodsCatManager goodsCatManager) {
		this.goodsCatManager = goodsCatManager;
	}
	
	
	
}
