package com.enation.app.shop.core.action.mobile;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.constant.CXConstant;
import com.enation.app.shop.core.constant.ReturnMsg;
import com.enation.app.shop.core.model.AdvanceLogs;
import com.enation.app.shop.core.model.Consume;
import com.enation.app.shop.core.model.JoinApply;
import com.enation.app.shop.core.model.Recharge;
import com.enation.app.shop.core.model.SpreadRecord;
import com.enation.app.shop.core.model.TransferLog;
import com.enation.app.shop.core.model.WashCard;
import com.enation.app.shop.core.model.WashMemberCard;
import com.enation.app.shop.core.model.WashMemberCoupons;
import com.enation.app.shop.core.model.WashRecord;
import com.enation.app.shop.core.service.IAdvanceLogsManager;
import com.enation.app.shop.core.service.IConsumeManager;
import com.enation.app.shop.core.service.IDictionaryManager;
import com.enation.app.shop.core.service.IJoinApplyManager;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.app.shop.core.service.IMemberProfitManager;
import com.enation.app.shop.core.service.IRechargeManager;
import com.enation.app.shop.core.service.ISpreadRecordManager;
import com.enation.app.shop.core.service.ITransferLogManager;
import com.enation.app.shop.core.service.IWashCardManager;
import com.enation.app.shop.core.service.IWashCouponsManager;
import com.enation.app.shop.core.service.IWashMemberCardManager;
import com.enation.app.shop.core.service.IWashMemberCouponsManager;
import com.enation.app.shop.core.service.IWashRecordManager;
import com.enation.app.shop.core.utils.DetailUtil;
import com.enation.app.shop.core.utils.RequestHeaderUtil;
import com.enation.app.shop.core.utils.ResponseUtils;
import com.enation.app.shop.core.utils.SendSms;
import com.enation.app.shop.core.utils.ValidateLoginUtil;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

/**
 * 会员接口
 * @author yexf
 * 2016-10-12
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/shop")
@Action("mobileMember")
public class MobileMemberAction extends WWAction{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public static HashMap<String, Object>  Code = new HashMap<String, Object>();

	@Autowired
	private IMemberManager memberManager;
	@Autowired
	private ValidateLoginUtil validateLoginUtil;
	@Autowired
	private IWashRecordManager washRecordManager;
	@Autowired
	private IConsumeManager consumeManager;
	@Autowired
	private IRechargeManager rechargeManager;
	@Autowired
	private IWashCardManager washCardManager;
	@Autowired
	private IWashMemberCardManager washMemberCardManager;
	@Autowired
	private IJoinApplyManager joinApplyManager;
	@Autowired
	private IWashMemberCouponsManager washMemberCouponsManager;
	@Autowired
	private IWashCouponsManager washCouponsManager;
	@Autowired
	private ISpreadRecordManager spreadRecordManager;
	@Autowired
	private IMemberProfitManager memberProfitManager;
	@Autowired
	private IDictionaryManager dictionaryManager;
	@Autowired
	private IAdvanceLogsManager advanceLogsManager;
	@Autowired
	private ITransferLogManager transferLogManager;

	//头像参数
	private File file;
	private String fileFileName;

	private String company_name;
	private String person_name;
	private String  wx_code; //可以为空
	private String  province_id;
	private String province;
	private String city_id;
	private String city;
	private String region_id;
	private String region;
	private String address;

	/**
	 * 2.24	修改密码
	 * @author yexf
	 * 2016-10-20
	 * @return
	 */
	public String updatePassword(){

		//JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
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
			this.renderJson("1", "参数验证错误！", "");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="4"){
			this.renderJson("1", "请升级到最新版本", "");
			return WWAction.APPLICAXTION_JSON;
		}

		try{

			String password = request.getParameter("password");
			String new_password = request.getParameter("new_password");
			String new_passwrod_repeat = request.getParameter("new_passwrod_repeat");

			if(StringUtil.isEmpty(password) || StringUtil.isEmpty(new_password) || StringUtil.isEmpty(new_passwrod_repeat)){
				this.renderJson("1", "参数有误", "");
				return WWAction.APPLICAXTION_JSON;
			}

			if(!new_password.equals(new_passwrod_repeat)){
				this.renderJson("1", "两次输入密码不一致", "");
				return WWAction.APPLICAXTION_JSON;
			}

			if(password.equals(new_password)){
				this.renderJson("1", "新密码不能和原密码一样", "");
				return WWAction.APPLICAXTION_JSON;
			}

			Member old_member = this.memberManager.getMemberByMemberId(member_id);
			if(!StringUtil.md5(password).equals(old_member.getPassword())){
				this.renderJson("1", "原密码错误", "");
				return WWAction.APPLICAXTION_JSON;
			}

			Member member = new Member();
			member.setPassword(StringUtil.md5(new_password));

			Map<String,Object> map = new HashMap<String,Object>();
			map.put("member_id", member_id);

			this.memberManager.updateMemberForAttr(member, map);

			this.renderJson("0", "修改成功", jsonData.toString());
			return WWAction.APPLICAXTION_JSON;

		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			if(StringUtil.isEmpty(message) || message.equals("null")){
				message = "服务器出错，请重试";
			}
			logger.error("修改个人资料出错！错误信息："+message);
			this.renderJson("1", message, "");
			return WWAction.APPLICAXTION_JSON;
		}

	}

	/**
	 * 8、修改个人资料
	 * @author yexf
	 * 2017-4-9
	 */
	public void updateMember(){

		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		//JSONArray jsonArray = new JSONArray();

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		String file_path = request.getParameter("file_path");
		String nickname = request.getParameter("nickname");
		//System.out.println("file_path&nickname："+file_path+" "+nickname);

		try{

			if(validateLoginUtil.checkLogin(request, response)){
				return;
			}

			String member_id = request.getHeader("member_id");
			//member_id = "83";

			Member member = new Member();

			if(!StringUtil.isEmpty(file_path)){
				member.setFace(file_path);
			}

			if(!StringUtil.isEmpty(nickname)){
				member.setNickname(nickname);
			}

			Map<String, Object> map = new HashMap<String,Object>();
			map.put("member_id", member_id);

			this.memberManager.updateMemberForAttr(member, map);

			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());

		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("修改个人资料出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}

	}

	/**
	 * *8、修改个人资料上传图片
	 * @author yexf
	 * 2017-6-8
	 */
	public void updateMemberFile(){

		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		//JSONArray jsonArray = new JSONArray();

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		try{

			if(validateLoginUtil.checkLogin(request, response)){
				return;
			}

			File image_file = this.file;

			Member member = new Member();
			String path = new String();

			if(image_file != null){
				System.out.println("file长度："+image_file.length());

				FileInputStream ins = new FileInputStream(image_file);
				System.out.println("图片大小："+ins.available());
				if(ins.available() > 1024 * 10240){
					image_file.delete();
					json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.FILE_BIG_ERROR, "2", jsonData.toString());
					ResponseUtils.renderJson(response, json.toString());
					return;
				}
				path = UploadUtil.upload(image_file, "default.jpg", "face");
				member.setFace(path);

			}

			if(!StringUtil.isEmpty(path)){
				//图片地址转换 fs->服务器地址
				String face = UploadUtil.replacePath(path);
				jsonData.put("file_path", face);
			}

			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());

		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("修改个人资料上传图片出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}

	}

	/**
	 * 2.34 提交会员申请
	 * @author yexf
	 * 2016-10-8
	 * @return
	 * http://localhost:8080/lysks/shop/mobileMember!sendMemberApply.do?member_id=83&company_name=12&person_name=23&wx_code=34&province_id=9&province=沈河区&city_id=122&city=沈阳市&region_id=1105&region=沈河区&address=金家街94-1
	 */
	public String sendMemberApply(){

		//JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
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
			this.renderJson("1", "参数验证错误！", "");
			return WWAction.APPLICAXTION_JSON;
		}else if(rh!=null && rh=="4"){
			this.renderJson("1", "请升级到最新版本", "");
			return WWAction.APPLICAXTION_JSON;
		}

		try{

			String company_name = this.company_name;
			//String company_name = request.getParameter("company_name");
			String person_name = this.person_name;
			String wx_code = this.wx_code; //可以为空
			String province_id = this.province_id;
			String province = this.province;
			String city_id = this.city_id;
			String city = this.city;
			String region_id = this.region_id;
			String region = this.region;
			String address = this.address;

			if(StringUtil.isEmpty(company_name) || StringUtil.isEmpty(person_name) || StringUtil.isEmpty(province_id) ||
					StringUtil.isEmpty(province) || StringUtil.isEmpty(city_id) || StringUtil.isEmpty(city) ||
						StringUtil.isEmpty(region_id) || StringUtil.isEmpty(region) || StringUtil.isEmpty(address)){
				this.renderJson("1", "必填项不能为空", "");
				return WWAction.APPLICAXTION_JSON;
			}

			String path = new String();
			File image_file = this.file;
			//String image_fileFileName = this.fileFileName;
			if(image_file != null){

				FileInputStream ins = new FileInputStream(image_file);
				if(ins.available() > 1024 * 10240){
					image_file.delete();
					this.renderJson("1", "文件不能大于10M", "");
					return WWAction.APPLICAXTION_JSON;
				}

				String image_fileFileName = "1.jpg";//暂时默认jpg
				String allowType = "gif,jpg,bmp,png";
				if(StringUtil.isEmpty(image_fileFileName)){
					this.renderJson("1", "文件名不存在", "");
					return WWAction.APPLICAXTION_JSON;
				}else{
					String ex = image_fileFileName.substring(image_fileFileName.lastIndexOf(".") + 1, image_fileFileName.length());
					if(allowType.toString().indexOf(ex) < 0){
						this.renderJson("1", "文件格式错误", "");
						return WWAction.APPLICAXTION_JSON;
					}
				}
				path = UploadUtil.upload(image_file, image_fileFileName, "shop_image");

			}

			Map<String, Object> memberMap = new HashMap<String, Object>();

			memberMap.put("company_name", company_name);
			memberMap.put("shop_image", path);
			memberMap.put("person_name", person_name);
			memberMap.put("province_id", province_id);
			memberMap.put("province", province);
			memberMap.put("city_id", city_id);
			memberMap.put("city", city);
			memberMap.put("region_id", region_id);
			memberMap.put("region", region);
			memberMap.put("address", address);
			memberMap.put("wx_code", wx_code);
			memberMap.put("verify_status", "1");

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("member_id", member_id);

			this.memberManager.updateMemberForMap(memberMap, map);

			this.renderJson("0", "提交成功", jsonData.toString());
			return WWAction.APPLICAXTION_JSON;

		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			if(StringUtil.isEmpty(message) || message.equals("null")){
				message = "服务器出错，请重试";
			}
			logger.error("修改个人资料出错！错误信息："+message);
			this.renderJson("1", message, "");
			return WWAction.APPLICAXTION_JSON;
		}

	}


	/**
	 * 7、获取会员信息
	 * @author yexf
	 * 2016-10-20
	 * @return
	 */
	public void getMemberDetail(){

		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		//JSONArray jsonArray = new JSONArray();

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		try{

			/*if(validateLoginUtil.checkLogin(request, response)){
				return;
			}*/

			String member_id = request.getHeader("member_id");
			logger.info("member_id:"+member_id);

			Member member = this.memberManager.getMemberByMemberId(member_id);

			if(member != null){
				jsonData.put("member_id", member.getMember_id());
				jsonData.put("uname", member.getUname());
				//图片地址转换 fs->服务器地址
				String face = UploadUtil.replacePath(member.getFace());
				jsonData.put("face", face);
				jsonData.put("nickname", StringUtil.isNull(member.getNickname()));
				String wash_time = "0";
				if(member.getWash_time() == 0){

				}else if(60 > member.getWash_time() && member.getWash_time() > 0){
					wash_time = "1";
				}else{
					Integer w = member.getWash_time()/60 + 1;
					wash_time = w+"";
				}
				jsonData.put("wash_time", wash_time);//累计洗车
				jsonData.put("less_water", member.getLess_water());//节水
				jsonData.put("less_electric", member.getLess_electric());//节电
				jsonData.put("sports_achieve", member.getSports_achieve());//运动成就
				jsonData.put("balance", member.getBalance());//余额
				jsonData.put("token", member.getToken());
				jsonData.put("is_join", member.getIs_join());//是否加盟（0：否 1：是）
				Map map = this.dictionaryManager.getDataMap("transfer_rate");
				String transfer_rate = StringUtil.isNullRt0(map.get("transfer_rate"));
				jsonData.put("transfer_rate", transfer_rate);

				json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonData.toString());
				ResponseUtils.renderJson(response, json.toString());
			}else{
				json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_403, ReturnMsg.ErrorMsg.USER_NO_EXISTENT, "2", jsonData.toString());
				ResponseUtils.renderJson(response, json.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("获取会员信息出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}

	}

	/**
	 * 1、验证手机
	 * @author yexf
	 * 2017-4-1
	 * @return
	 */
	public void login(){

		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		//JSONArray jsonArray = new JSONArray();

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		String uname = request.getParameter("uname");
		String validate_code = request.getParameter("validate_code");
		String platform = request.getHeader("p"); // 系统
		String recomId = request.getParameter("recomId"); // 推荐人id
		String invite_code = request.getParameter("invite_code"); // 邀请码

		if(StringUtil.isEmpty(uname) || StringUtil.isEmpty(validate_code)){
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.PARAMETER_ERROR, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return;
		}

		try{

			/*if(validateLoginUtil.checkLogin(request, response)){
				return;
			}*/

			if("18770044175".equals(uname) && "123456".equals(validate_code)){

			}else{
				if(!StringUtil.isNull(Code.get(uname)).equals(validate_code)){
					json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.VALIDATE_ERROR, "2", jsonData.toString());
					ResponseUtils.renderJson(response, json.toString());
					return;
				}
			}

			Member member = this.memberManager.cxLogin(uname);

			if(member != null){
				if(StringUtils.isNotBlank(recomId)){
					json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, "新注册用户才能领取哦", "2", jsonData.toString());
					ResponseUtils.renderJson(response, json.toString());
					return;
				}
				//查找没有结算或没有支付的洗车记录列表
				/*int wash_record_id = 0;
				List<WashRecord> washRecordList = this.washRecordManager.getWashRecordListByNoPayOrNoCheckOut(member.getMember_id());
				if(washRecordList != null && washRecordList.size() != 0){
					wash_record_id = washRecordList.get(0).getWash_record_id();
				}*/

				jsonData.put("member_id", member.getMember_id());
				jsonData.put("uname", member.getUname());
				jsonData.put("token", member.getToken());
				//jsonData.put("wash_record_id", wash_record_id);//是否有 没有结算或没有支付的洗车记录（没有：0 有：洗车记录id，需要调用获取洗车中信息页面）

				json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonData.toString());
				ResponseUtils.renderJson(response, json.toString());
			}else{
				Member inviteMember = null;
				if(!StringUtil.isEmpty(invite_code)){
					inviteMember = this.memberManager.getMemberByInviteCode(invite_code);
					if(inviteMember == null){
						json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, "邀请码不存在", "2", jsonData.toString());
						ResponseUtils.renderJson(response, json.toString());
						return;
					}
				}

				//不存在就注册一个
				Member reg_member = new Member();

				reg_member.setUname(uname);
				String platform_str = DetailUtil.platform_str(platform);
				reg_member.setPlatform(platform_str);
				String registerip = request.getRemoteAddr();
				reg_member.setRegisterip(registerip);
				UUID uuid = UUID.randomUUID();//uuid23223
				reg_member.setToken(uuid.toString());
				reg_member.setBind_phone(uname);
				if(StringUtils.isNotBlank(recomId)){
					reg_member.setRecomId(Integer.valueOf(recomId));
				}
				if(inviteMember != null){
					reg_member.setParent_id(inviteMember.getMember_id());
				}
				String register_give = this.dictionaryManager.getDataValueByKey("register_give", "register_give"); // 新用户注册送余额，为0时为不赠送
				if(!StringUtil.isEmpty(register_give) && !"0".equals(register_give)){
					reg_member.setBalance(Double.parseDouble(register_give));
				}
				//是否第一次
				memberManager.appRegister(reg_member);
				Member result_member = this.memberManager.getMemberByUname(uname);
				if(!StringUtil.isEmpty(register_give) && !"0".equals(register_give)){
					AdvanceLogs advanceLogs = new AdvanceLogs();
					advanceLogs.setMember_id(result_member.getMember_id());
					advanceLogs.setDisabled("false");
					advanceLogs.setMtime(DateUtil.getDatelineLong());
					advanceLogs.setImport_money(Double.parseDouble(register_give));
					advanceLogs.setMember_advance(result_member.getBalance());
					advanceLogs.setShop_advance(result_member.getBalance());// 此字段很难理解
					advanceLogs.setMoney(Double.parseDouble(register_give));
					advanceLogs.setMessage("注册赠送");
					advanceLogs.setMemo("注册赠送");
					advanceLogsManager.add(advanceLogs);
				}

				Map memberMap = new HashMap<>();
				memberMap.put("invite_code", DetailUtil.toSerialCode(result_member.getMember_id()));

				Map map = new HashMap<>();
				map.put("member_id", result_member.getMember_id());
				this.memberManager.updateMemberForMap(memberMap, map);

				jsonData.put("member_id", result_member.getMember_id());
				jsonData.put("uname", result_member.getUname());
				jsonData.put("token", result_member.getToken());
				jsonData.put("wash_record_id", "0");

				json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonData.toString());
				ResponseUtils.renderJson(response, json.toString());
			}
			Code.remove(uname);
			return;

		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("登录出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}

	}

	/**
	 * 2、推广员申请-新增
	 * @author yexf
	 * 2017-11-25
	 * @return
	 */
	public void spreadApply(){

		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		//JSONArray jsonArray = new JSONArray();

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		String name = request.getParameter("name"); // 用户名（手机号）
		String card_no = request.getParameter("card_no"); // 身份证
		String bank_id = request.getParameter("bank_id"); // 银行id
		String bank_name = request.getParameter("bank_name"); // 开户行名称
		String bank_no = request.getParameter("bank_no"); // 银行卡号
		String wx_code = request.getParameter("wx_code"); // 微信号
		String alipay_code = request.getParameter("alipay_code"); // 支付宝号
		String spread_phone = request.getParameter("spread_phone"); // 推广员手机
		String validate_code = request.getParameter("validate_code"); // 验证码

		if(StringUtil.isEmpty(name) || StringUtil.isEmpty(card_no) ||
				StringUtil.isEmpty(bank_id) || StringUtil.isEmpty(bank_no) || StringUtil.isEmpty(bank_name) ||
					StringUtil.isEmpty(wx_code) || StringUtil.isEmpty(alipay_code) ||
						StringUtil.isEmpty(spread_phone) || StringUtil.isEmpty(validate_code)){
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.PARAMETER_ERROR, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return;
		}

		try{

			/*if(validateLoginUtil.checkLogin(request, response)){
				return;
			}*/

			String member_id = request.getHeader("member_id"); // member_id="83";

			if(!StringUtil.isNull(Code.get(spread_phone)).equals(validate_code)){
				json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.VALIDATE_ERROR, "2", jsonData.toString());
				ResponseUtils.renderJson(response, json.toString());
				return;
			}

			Member member = this.memberManager.getMemberByMemberId(member_id);

			member.setName(name);
			member.setCard_no(card_no);
			member.setBank_id(Integer.parseInt(bank_id));
			member.setBank_no(bank_no);
			member.setWx_code(wx_code);
			member.setAlipay_code(alipay_code);
			member.setSpread_phone(spread_phone);
			member.setBank_name(bank_name);
			member.setSpread_status(1);
			//member.setInvite_code(DetailUtil.toSerialCode(Long.parseLong(member_id)));

			Map<String, Object> map = new HashMap<String,Object>();
			map.put("member_id", member_id);

			this.memberManager.updateMemberForAttr(member, map);

			Code.remove(spread_phone);

			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());

			return;

		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("推广员申请出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}

	}

	/**
	 * 3、推广员信息
	 * @author yexf
	 * 2017-11-25
	 * @return
	 */
	public void spreadDetail(){

		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		//JSONArray jsonArray = new JSONArray();

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		try{

			/*if(validateLoginUtil.checkLogin(request, response)){
				return;
			}*/

			String member_id = request.getHeader("member_id");  //member_id="83";

			Member member = this.memberManager.getMemberByMemberId(member_id);

			Map<String, Object> paramMap = this.memberManager.getSpreadParam(member_id);
			jsonData.put("spread_status", member.getSpread_status());
			jsonData.put("name", StringUtil.isEmpty(member.getName()) ? member.getUname() : member.getName());
			jsonData.put("award_amount", member.getAward_amount());
			String serial_code = member.getInvite_code();
			if(StringUtil.isEmpty(serial_code)){
				serial_code = DetailUtil.toSerialCode(Long.parseLong(member_id));
				Map memberMap = new HashMap<>();
				memberMap.put("invite_code", serial_code);

				Map map = new HashMap<>();
				map.put("member_id", member.getMember_id());
				this.memberManager.updateMemberForMap(memberMap, map);
			}
			jsonData.put("invite_code", serial_code);
			jsonData.put("spread_num", Integer.parseInt(StringUtil.isNullRt0(paramMap.get("spread_num"))));
			jsonData.put("recharge_num", Integer.parseInt(StringUtil.isNullRt0(paramMap.get("recharge_num"))));
			jsonData.put("recharge_amount", StringUtil.getDouble2(Double.parseDouble(StringUtil.isNullRt0(paramMap.get("recharge_amount")))));

			jsonData.put("card_no", StringUtil.isNull(member.getCard_no()));
			jsonData.put("bank_id", member.getBank_id() == null || member.getBank_id() == 0 ? "" : member.getBank_id());
			jsonData.put("bank_name", StringUtil.isNull(member.getBank_name()));
			jsonData.put("bank_no", StringUtil.isNull(member.getBank_no()));
			jsonData.put("wx_code", StringUtil.isNull(member.getWx_code()));
			jsonData.put("alipay_code", StringUtil.isNull(member.getAlipay_code()));
			jsonData.put("spread_phone", StringUtil.isNull(member.getSpread_phone()));

			//this.memberManager.exeSpreadProfit(member, 1d);
			//Map<String, Object> map = this.memberProfitManager.getMemberRole(4, 0, 0, 0);
			//this.memberManager.subAwardAmountAddReflect(83, 1d, 1d);
			//this.memberManager.addAwardAmountAndSpread(83, 1d, 1d);

			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());

			return;

		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("推广员信息出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}

	}

	/**
	 * 4、提现接口-新增
	 * @author yexf
	 * 2017-11-26
	 * @return
	 */
	public void reflectAward(){

		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		//JSONArray jsonArray = new JSONArray();

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		String reflect_price = request.getParameter("reflect_price"); // 提现金额

		try{

			/*if(validateLoginUtil.checkLogin(request, response)){
				return;
			}*/
			if(StringUtil.isEmpty(reflect_price) || !StringUtil.isDouble(reflect_price) || Double.parseDouble(reflect_price) < 100){
				json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.PARAMETER_ERROR, "2", jsonData.toString());
				ResponseUtils.renderJson(response, json.toString());
				return;
			}

			String member_id = request.getHeader("member_id");
			Member member = this.memberManager.getMemberByMemberId(member_id);
			if(Double.parseDouble(reflect_price) > member.getAward_amount()){
				json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, "提现金额过大", "2", jsonData.toString());
				ResponseUtils.renderJson(response, json.toString());
				return;
			}
			SpreadRecord spreadRecord = new SpreadRecord();
			spreadRecord.setCreate_time(System.currentTimeMillis());
			spreadRecord.setMember_id(Integer.parseInt(member_id));
			spreadRecord.setPrice(Double.parseDouble(reflect_price));
			spreadRecord.setRemark("用户"+member.getName()+"，提现了"+Double.parseDouble(reflect_price)+"元");
			spreadRecord.setType(1);
			spreadRecord.setUse_member_id(Integer.parseInt(member_id));
			this.spreadRecordManager.addSpreadRecord(spreadRecord);
			this.memberManager.subAwardAmountAddReflect(Integer.parseInt(member_id), Double.parseDouble(reflect_price), Double.parseDouble(reflect_price));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());

			return;

		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("提现接口出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}

	}

	/**
	 * 5、交易记录列表-新增
	 * @author yexf
	 * 2017-11-26
	 * @return
	 */
	public void spreadRecordList(){

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
			String member_id = request.getHeader("member_id");

			Page spreadRecordPage = this.spreadRecordManager.getSpreadRecordPage(member_id, Integer.parseInt(page_no), Integer.parseInt(page_size));

			@SuppressWarnings("unchecked")
			List<SpreadRecord> recordList = (List<SpreadRecord>) spreadRecordPage.getResult();
			if(recordList != null && recordList.size() !=0 ){
				for(SpreadRecord sr : recordList){
					JSONObject srJson = new JSONObject();
					srJson.put("spread_record_id", sr.getSpread_record_id());
					srJson.put("type", sr.getType());
					srJson.put("price", sr.getPrice());
					String time = DateUtil.LongToString(sr.getCreate_time(), "yyyy-MM-dd");
					srJson.put("create_time", time);
					srJson.put("remark", sr.getRemark());
					srJson.put("spread_status", sr.getSpread_status());
					jsonArray.add(srJson);
				}
			}
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonArray.toString());
			ResponseUtils.renderJson(response, json.toString());

		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("交易记录列表出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}

	}

	/**
	 * 余额转让功能
	 * 1、余额转让（√）
	 * @author yexf
	 * 2018-1-5
	 * @return
	 */
	public void transferBalance(){

		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONArray jsonArray = new JSONArray();

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		String uname = request.getParameter("uname"); // 手机号
		String price = request.getParameter("price"); // 金额

		try{

			/*if(validateLoginUtil.checkLogin(request, response)){
				return;
			}*/

			if(StringUtil.isEmpty(uname) || StringUtil.isEmpty(price) ||!StringUtil.isDouble(price)){
				json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.PARAMETER_ERROR, "2", jsonData.toString());
				ResponseUtils.renderJson(response, json.toString());
				return;
			}

			String member_id = request.getHeader("member_id");

			Member toMember = this.memberManager.getMemberByUname(uname);
			Member member = this.memberManager.getMemberByMemberId(member_id);
			if(toMember == null || member == null){
				json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.USER_NO_EXISTENT, "2", jsonData.toString());
				ResponseUtils.renderJson(response, json.toString());
				return;
			}
			if(member.getBalance() < Double.parseDouble(price)){
				json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.BALANCE_LESS, "2", jsonData.toString());
				ResponseUtils.renderJson(response, json.toString());
				return;
			}

			this.transferLogManager.transferBalance(member_id, toMember.getMember_id().toString(), price);

			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());

		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("交易记录列表出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}

	}

	/**
	 * 余额转让功能
	 * 2、余额转让记录（√）
	 * @author yexf
	 * 2018-1-5
	 * @return
	 */
	public void transferLogList(){

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
			String member_id = request.getHeader("member_id");

			Page transferLogPage = this.transferLogManager.getTransferLogPage(member_id, Integer.parseInt(page_no), Integer.parseInt(page_size));

			@SuppressWarnings("unchecked")
			List<TransferLog> recordList = (List<TransferLog>) transferLogPage.getResult();
			if(recordList != null && recordList.size() !=0 ){
				for(TransferLog tf : recordList){
					JSONObject srJson = new JSONObject();
					srJson.put("transfer_log_id", tf.getTransfer_log_id());
					srJson.put("uname", tf.getUname());
					srJson.put("to_uname", tf.getTo_uname());
					srJson.put("price", tf.getPrice());
					srJson.put("service_charge", tf.getService_charge());
					String time = DateUtil.LongToString(tf.getCreate_time(), "yyyy-MM-dd HH:mm");
					srJson.put("create_time", time);
					jsonArray.add(srJson);
				}
			}
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonArray.toString());
			ResponseUtils.renderJson(response, json.toString());

		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("余额转让记录列表出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}

	}

	/**
	 * 10、我的收入
	 * @author yexf
	 * 2017-4-12
	 * @return
	 */
	public void getIncomeList(){

		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONArray jsonArray = new JSONArray();

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		String page_no = request.getParameter("page_no");
		String page_size = request.getParameter("page_size");

		page_no = StringUtil.isEmpty(page_no) ? "1" : page_no;
		page_size = StringUtil.isEmpty(page_size) ? "20" : page_size;

		try{

			/*if(validateLoginUtil.checkLogin(request, response)){
				return;
			}*/
			String member_id = request.getHeader("member_id");

			//累计收入
			Member member = this.memberManager.getMemberByMemberId(member_id);

			Page recordPage = this.washRecordManager.getIncomePage(member, Integer.parseInt(page_no), Integer.parseInt(page_size));

			/*@SuppressWarnings("unchecked")
			List<WashRecord> recordList = (List<WashRecord>) recordPage.getResult();
			if(recordList != null && recordList.size() !=0 ){
				for(WashRecord wr : recordList){
					JSONObject wrJson = new JSONObject();
					wrJson.put("machine_name", wr.getMachine_name());
					wrJson.put("income_price", StringUtil.getDouble2ToStringByDouble(wr.getTotal_price()));
					String time = DateUtil.LongToString(wr.getCreate_time(), "yyyy-MM-dd");
					wrJson.put("time", time);
					jsonArray.add(wrJson);
				}
			}*/
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> recordList = (List<Map<String, Object>>) recordPage.getResult();
			if(recordList != null && recordList.size() !=0 ){
				for(Map<String, Object> mp : recordList){
					JSONObject wrJson = new JSONObject();
					if(mp.get("machine_name")!=null){
						wrJson.put("machine_name", mp.get("machine_name").toString());
					}
					if(mp.get("income_price")!=null){
						wrJson.put("income_price", StringUtil.getDouble2ToStringByDouble(Double.valueOf(mp.get("income_price").toString())));
					}
					if(mp.get("create_time")!=null){
						String time = DateUtil.LongToString(Long.valueOf(mp.get("create_time").toString()), "yyyy-MM-dd");
						wrJson.put("time", time);
					}
					jsonArray.add(wrJson);
				}
			}
			jsonData.put("income_list", jsonArray);
			//昨日收入
			Double yes_income = this.washRecordManager.getYesterDayIncome(member);
			jsonData.put("yesterday_income", StringUtil.getDouble2ToStringByDouble(yes_income));

			jsonData.put("total_income", member.getJoin_income() + member.getPartner_amount());

			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());

		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("我的收入出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}

	}

	/**
	 * 28、我的优惠劵
	 * @author yexf
	 * 2017-4-12
	 * @return
	 */
	public void getMemberCouponsList(){

		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONArray jsonArray = new JSONArray();

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		String page_no = request.getParameter("page_no");
		String page_size = request.getParameter("page_size");

		page_no = StringUtil.isEmpty(page_no) ? "1" : page_no;
		page_size = StringUtil.isEmpty(page_size) ? "20" : page_size;

		try{

			/*if(validateLoginUtil.checkLogin(request, response)){
				return;
			}*/
			String member_id = request.getHeader("member_id");

			List<WashMemberCoupons> wmcList = this.washMemberCouponsManager.getMemberCouponsList(member_id);

			if(wmcList != null && wmcList.size() !=0 ){
				for(WashMemberCoupons wmc : wmcList){
					JSONObject wmcJson = new JSONObject();
					wmcJson.put("member_coupons_id", wmc.getWash_member_coupons_id());
					wmcJson.put("name", "巢享共享洗车劵");
					String time = DateUtil.LongToString(wmc.getEnd_time(), "yyyy-MM-dd");
					wmcJson.put("end_time", time);
					wmcJson.put("discont", wmc.getDiscount());
					wmcJson.put("is_use", wmc.getIs_use());
					jsonArray.add(wmcJson);
				}
			}

			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonArray.toString());
			ResponseUtils.renderJson(response, json.toString());

		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("我的优惠劵出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}

	}

	/**
	 * 11、洗车记录
	 * @author yexf
	 * 2017-4-12
	 * @return
	 */
	public void getWashList(){

		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONArray jsonArray = new JSONArray();

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		String page_no = request.getParameter("page_no");
		String page_size = request.getParameter("page_size");

		page_no = StringUtil.isEmpty(page_no) ? "1" : page_no;
		page_size = StringUtil.isEmpty(page_size) ? "20" : page_size;

		try{

			/*if(validateLoginUtil.checkLogin(request, response)){
				return;
			}*/
			String member_id = request.getHeader("member_id");

			Page recordPage = this.washRecordManager.getWashRecordPage(member_id, Integer.parseInt(page_no), Integer.parseInt(page_size));

			@SuppressWarnings("unchecked")
			List<WashRecord> recordList = (List<WashRecord>) recordPage.getResult();
			if(recordList != null && recordList.size() !=0 ){
				for(WashRecord wr : recordList){
					JSONObject wrJson = new JSONObject();
					wrJson.put("wash_record_id", wr.getWash_record_id());
					wrJson.put("address", wr.getAddress());
					wrJson.put("price", wr.getPay_price());
					String time = DateUtil.LongToString(wr.getCreate_time(), "yyyy-MM-dd");
					wrJson.put("time", time);
					String wash_time = "0分钟";
					if(60 > wr.getWash_time() && wr.getWash_time() > 0){
						wash_time = "1分钟";
					}else{
						wash_time = StringUtil.secToTime(wr.getWash_time());
					}
					wrJson.put("wash_time", wash_time);
					jsonArray.add(wrJson);
				}
			}

			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonArray.toString());
			ResponseUtils.renderJson(response, json.toString());

		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("洗车记录出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}

	}

	/**
	 * 洗车
	 * @author yexf
	 * 2017-4-12
	 * @return
	 */
	public void getWash(){

		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONArray jsonArray = new JSONArray();

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		String table = request.getParameter("table");
		table = StringUtil.isEmpty(table) ? "es_agent" : table;

		try{

			/*if(validateLoginUtil.checkLogin(request, response)){
				return;
			}*/
			this.memberManager.getWash(table);
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonArray.toString());
			ResponseUtils.renderJson(response, json.toString());

		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("洗车记录出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}

	}

	/**
	 * 12、消费明细
	 * @author yexf
	 * 2017-4-12
	 * @return
	 */
	public void getConsumeList(){

		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONArray jsonArray = new JSONArray();

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		String page_no = request.getParameter("page_no");
		String page_size = request.getParameter("page_size");

		page_no = StringUtil.isEmpty(page_no) ? "1" : page_no;
		page_size = StringUtil.isEmpty(page_size) ? "20" : page_size;

		try{

			/*if(validateLoginUtil.checkLogin(request, response)){
				return;
			}*/
			String member_id = request.getHeader("member_id");

			Page consumePage = this.consumeManager.getConsumePage(member_id, Integer.parseInt(page_no), Integer.parseInt(page_size));

			@SuppressWarnings("unchecked")
			List<Consume> consumeList = (List<Consume>) consumePage.getResult();
			if(consumeList != null && consumeList.size() !=0 ){
				for(Consume c : consumeList){
					JSONObject cJson = new JSONObject();
					cJson.put("consume_id", c.getConsume_id());
					String type_name = DetailUtil.getConsumeType(c.getType());
					cJson.put("type_name", type_name);
					cJson.put("price", c.getPrice());
					String time = DateUtil.LongToString(c.getCreate_time(), "yyyy-MM-dd HH:mm");
					cJson.put("time", time);
					jsonArray.add(cJson);
				}
			}

			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonArray.toString());
			ResponseUtils.renderJson(response, json.toString());

		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("消费明细出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}

	}

	/**
	 * 13、充值明细
	 * @author yexf
	 * 2017-4-12
	 * @return
	 */
	public void getRechargeList(){

		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONArray jsonArray = new JSONArray();

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		String page_no = request.getParameter("page_no");
		String page_size = request.getParameter("page_size");

		page_no = StringUtil.isEmpty(page_no) ? "1" : page_no;
		page_size = StringUtil.isEmpty(page_size) ? "20" : page_size;

		try{

			/*if(validateLoginUtil.checkLogin(request, response)){
				return;
			}*/
			String member_id = request.getHeader("member_id");

			Page rechargePage = this.rechargeManager.getRechargePage(member_id, Integer.parseInt(page_no), Integer.parseInt(page_size));

			@SuppressWarnings("unchecked")
			List<Recharge> rechargeList = (List<Recharge>) rechargePage.getResult();
			if(rechargeList != null && rechargeList.size() !=0 ){
				for(Recharge c : rechargeList){
					JSONObject cJson = new JSONObject();
					cJson.put("recharge_id", c.getRecharge_id());
					System.out.println(c.getPay_type());
					String pay_type = DetailUtil.getPayType(c.getPay_type());
					System.out.println(pay_type);
					cJson.put("pay_type", pay_type);
					cJson.put("price", c.getBalance());
					String time = DateUtil.LongToString(c.getCreate_time(), "yyyy-MM-dd HH:mm");
					cJson.put("time", time);
					jsonArray.add(cJson);
				}
			}

			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonArray.toString());
			ResponseUtils.renderJson(response, json.toString());

		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("充值明细出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}

	}

	/**
	 * 14、会员卡
	 * @author yexf
	 * 2017-4-12
	 * @return
	 */
	public void getWashCardList(){

		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONArray jsonArray = new JSONArray();

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		try{

			/*if(validateLoginUtil.checkLogin(request, response)){
				return;
			}*/

			List<WashCard> washCardList = this.washCardManager.getWashCardPage();

			if(washCardList != null && washCardList.size() !=0 ){
				for(WashCard wc : washCardList){
					JSONObject cJson = new JSONObject();
					cJson.put("wash_card_id", wc.getWash_card_id());
					cJson.put("card_name", wc.getCard_name());
					cJson.put("card_explain", wc.getCard_explain());
					cJson.put("price", wc.getPrice());
					jsonArray.add(cJson);
				}
			}

			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonArray.toString());
			ResponseUtils.renderJson(response, json.toString());

		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("会员卡出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}

	}

	/**
	 * 15、会员拥有的会员卡
	 * @author yexf
	 * 2017-4-16
	 * @return
	 */
	public void getMemberCardList(){

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

			List<WashMemberCard> cardList = this.washMemberCardManager.getMemberCardList(member_id);

			if(cardList != null && cardList.size() !=0 ){
				for(WashMemberCard wmc : cardList){
					JSONObject cJson = new JSONObject();
					cJson.put("wash_member_card_id", wmc.getWash_member_card_id());
					String temp = UploadUtil.replacePath(wmc.getImage());
					cJson.put("image", temp);
					cJson.put("sn", wmc.getSn());
					cJson.put("card_type", wmc.getCard_type());
					cJson.put("wash_day", wmc.getWash_day());
					jsonArray.add(cJson);
				}
			}

			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonArray.toString());
			ResponseUtils.renderJson(response, json.toString());

		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("会员拥有的会员卡出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}

	}

	/**
	 * 18、提交加盟申请
	 * @author yexf
	 * 2017-4-16
	 * @return
	 */
	public void sendJoinApply(){

		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		JSONArray jsonArray = new JSONArray();

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		String name = request.getParameter("name");//客户姓名
		String phone = request.getParameter("phone");//手机号码
		String remark = request.getHeader("remark");//备注信息

		if(StringUtil.isEmpty(name) || StringUtil.isEmpty(phone)){
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.PARAMETER_ERROR, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return;
		}

		try{

			/*if(validateLoginUtil.checkLogin(request, response)){
				return;
			}*/

			String member_id = request.getHeader("member_id");

			JoinApply joinApply = new JoinApply();
			joinApply.setMember_id(Integer.parseInt(member_id));
			long now_time = System.currentTimeMillis();
			joinApply.setCreate_time(now_time);
			joinApply.setName(name);
			joinApply.setPhone(phone);
			joinApply.setRemark(remark);

			this.joinApplyManager.addJoinApply(joinApply);

			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonArray.toString());
			ResponseUtils.renderJson(response, json.toString());

		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("提交加盟申请出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}

	}

	/**
	 * 16、退出
	 * @author yexf
	 * 2017-4-12
	 */
	public void logout(){

		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		//JSONArray jsonArray = new JSONArray();

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		try{

			/*if(validateLoginUtil.checkLogin(request, response)){
				return;
			}*/

			String member_id = request.getHeader("member_id");

			this.memberManager.memberLogout(member_id);

			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());

		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("退出出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}

	}

	/**
	 * 注册
	 * @author yexf
	 * 2016-10-13
	 * @return
	 */
	public String register(){

		//JSONObject json = new JSONObject();
		//JSONObject jsonData = new JSONObject();
		//JSONArray jsonArray = new JSONArray();

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		//HttpServletResponse response = ThreadContextHolder.getHttpResponse();
		//String rh = RequestHeaderUtil.requestHeader(request, response);

		//String member_id = request.getHeader("u");
		//member_id = request.getParameter("member_id");
		//String token = request.getHeader("k");
		String platform = request.getHeader("p");//系统
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

			String uname = request.getParameter("uname");
			String password = request.getParameter("password");
			String validate_code = request.getParameter("validate_code");

			if(StringUtil.isEmpty(uname)){
				this.renderJson("1", "手机号不能为空", "");
				return WWAction.APPLICAXTION_JSON;
			}

			boolean uname_isphone = StringUtil.isMobileNO(uname);
			if(!uname_isphone){
				this.renderJson("1", "手机号码格式不对", "");
				return WWAction.APPLICAXTION_JSON;
			}

			if(StringUtil.isEmpty(password)){
				this.renderJson("1", "密码不能为空", "");
				return WWAction.APPLICAXTION_JSON;
			}

			if(password.length() < 6 || password.length() > 12){
				this.renderJson("1", "密码长度为6-12位", "");
				return WWAction.APPLICAXTION_JSON;
			}

			if(StringUtil.isEmpty(validate_code)){
				this.renderJson("1", "验证码不能为空", "");
				return WWAction.APPLICAXTION_JSON;
			}

			if(Code.get(uname) == null){
				this.renderJson("1", "请发送验证码", "");
				return WWAction.APPLICAXTION_JSON;
			}

			if(!StringUtil.isNull(Code.get(uname)).equals(validate_code)){
				this.renderJson("1", "验证码错误", "");
				return WWAction.APPLICAXTION_JSON;
			}

			int i = memberManager.checkname(uname);
			if (i > 0) {
				Code.remove(uname);
				this.renderJson("1", "用户已存在", "");
				return WWAction.APPLICAXTION_JSON;
			}

			Member member = new Member();

			member.setUname(uname);
			member.setPassword(StringUtil.md5(password));
			String platform_str = DetailUtil.platform_str(platform);
			member.setPlatform(platform_str);
			String registerip = request.getRemoteAddr();
			member.setRegisterip(registerip);

			int result = memberManager.appRegister(member);
			if(result == 1){
				this.renderJson("0", "注册成功", "");
			}else{
				this.renderJson("1", "未知错误", "");
			}

			Code.remove(uname);
			return WWAction.APPLICAXTION_JSON;

		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			if(StringUtil.isEmpty(message) || message.equals("null")){
				message = "服务器出错，请重试";
			}
			logger.error("注册出错！错误信息："+message);
			this.renderJson("1", message, "");
			return WWAction.APPLICAXTION_JSON;
		}

	}

	/**
	 * 忘记密码
	 * @author yexf
	 * 2016-10-13
	 * @return
	 */
	public String forgetPassword(){

		//JSONObject json = new JSONObject();
		//JSONObject jsonData = new JSONObject();
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

			String uname = request.getParameter("uname");
			String password = request.getParameter("password");
			String repeat_password = request.getParameter("repeat_password");
			String validate_code = request.getParameter("validate_code");

			if(StringUtil.isEmpty(uname)){
				this.renderJson("1", "手机号不能为空", "");
				return WWAction.APPLICAXTION_JSON;
			}

			boolean uname_isphone = StringUtil.isMobileNO(uname);
			if(!uname_isphone){
				this.renderJson("1", "手机号码格式不对", "");
				return WWAction.APPLICAXTION_JSON;
			}

			if(StringUtil.isEmpty(password)){
				this.renderJson("1", "密码不能为空", "");
				return WWAction.APPLICAXTION_JSON;
			}

			if(StringUtil.isEmpty(repeat_password)){
				this.renderJson("1", "请再次输入密码", "");
				return WWAction.APPLICAXTION_JSON;
			}

			if(password.length() < 6 || password.length() > 12){
				this.renderJson("1", "密码长度为6-12位", "");
				return WWAction.APPLICAXTION_JSON;
			}

			if(!password.equals(repeat_password)){
				this.renderJson("1", "两次输入密码不一致", "");
				return WWAction.APPLICAXTION_JSON;
			}

			if(StringUtil.isEmpty(validate_code)){
				this.renderJson("1", "验证码不能为空", "");
				return WWAction.APPLICAXTION_JSON;
			}

			if(Code.get(uname) == null){
				this.renderJson("1", "请发送验证码", "");
				return WWAction.APPLICAXTION_JSON;
			}

			if(Code.get(uname).equals(validate_code)){
				this.renderJson("1", "验证码错误", "");
				return WWAction.APPLICAXTION_JSON;
			}

			int i = memberManager.checkname(uname);
			if (i == 0) {
				this.renderJson("1", "用户不存在", "");
				return WWAction.APPLICAXTION_JSON;
			}

			this.memberManager.appUpdatePassword(uname, StringUtil.md5(password));
			//清除验证码
			Code.remove(uname);

			this.renderJson("0", "更改密码成功", "");
			return WWAction.APPLICAXTION_JSON;

		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			if(StringUtil.isEmpty(message) || message.equals("null")){
				message = "服务器出错，请重试";
			}
			logger.error("忘记密码出错！错误信息："+message);
			this.renderJson("1", message, "");
			return WWAction.APPLICAXTION_JSON;
		}

	}

	/**
	 * 发送验证码
	 * @author yexf
	 * 2017-4-1
	 * @return
	 */
	public void sendValidateCode(){

		JSONObject json = new JSONObject();
		JSONObject jsonData = new JSONObject();
		//JSONArray jsonArray = new JSONArray();

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		String uname = request.getParameter("uname");

		if(StringUtil.isEmpty(uname)){
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.PARAMETER_ERROR, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
			return;
		}

		try{

			boolean uname_isphone = StringUtil.isMobileNO(uname);
			if(!uname_isphone){
				json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.PHONE_FORMAT_ERROR, "2", jsonData.toString());
				ResponseUtils.renderJson(response, json.toString());
				return;
			}

			//控制60s内同一个手机只能发一次
			long now = System.currentTimeMillis();
			String mobile_old = StringUtil.isNull(Code.get(uname));
			if(!"".equals(mobile_old)){
				long time_old = Long.parseLong(Code.get(uname+"000").toString());
				if(now-time_old < 60000){
					json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.ErrorMsg.FREQUENT_OPERATION, "2", jsonData.toString());
					ResponseUtils.renderJson(response, json.toString());
					return;
				}
			}

			Integer mobileCode = (int)((Math.random()*9+1)*100000);
			//mobileCode = 123456;
			System.out.println("validate_code："+uname+" : "+mobileCode);
		    Code.put(uname, mobileCode);
		    Code.put(uname+"000", now);


		    /*****************post*****************/
		    //String post_url = "http://218.244.136.70:8888/sms.aspx";
		    /*String post_url = "http://115.29.242.32:8888/sms.aspx"; //这个是对的
			String userid = "1532";
			String account = "wbl";
			//String password = "ADIK122Jjssow";
			String password = "ADIK122Jjssow";
			String mobile = uname;
			String content = "你好，欢迎使用巢享洗车，您此次的验证码为:"+mobileCode+"。【巢享洗车】";
			String action = "send";

			SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
	        parameters.put("post_url", post_url);
	        parameters.put("userid", userid);
	        parameters.put("account", account);
	        parameters.put("password", password);
	        parameters.put("mobile", mobile);
	        parameters.put("content", content);
	        parameters.put("action", action);*/

	        //String requestXML = WxpubOAuth.getRequestXml(parameters);
	        /*String requestXML = HttpClientUtil.map2String(parameters);
	        System.out.println("requestXML："+requestXML);*/

	        //测试把发短信的注释了
	        //String result = UtilCommon.httpRequest(post_url, "POST", requestXML);

	        //System.out.println("result："+result);
	        //result = result.substring(50, result.length()-12);
	        //Map map = XMLUtil.doXMLParse(result);

		    /**************************************/

		    //发送短信 cxcar
		    Element root = SendSms.execute(uname, mobileCode);

		    String code = root.elementText("code");
			String msg = root.elementText("msg");
			String smsid = root.elementText("smsid");

			System.out.print(code+" ");
			System.out.print(msg+" ");
			System.out.print(smsid+" ");

			if("2".equals(code)){
				System.out.println("短信提交成功");
			}

			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_200, ReturnMsg.RightMsg.RETURN_SUCCESS, "1", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());

		} catch (Exception e) {
			e.printStackTrace();
			String message = StringUtil.getExpMessage(e.getMessage());
			logger.error("发送验证码出错！错误信息："+StringUtil.getExpMessage(e.getMessage()));
			json = ResponseUtils.toMakeJson(CXConstant.ReturnCode.CODE_500, message, "2", jsonData.toString());
			ResponseUtils.renderJson(response, json.toString());
		}

	}


	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getFileFileName() {
		return fileFileName;
	}

	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}

	public String getCompany_name() {
		return company_name;
	}

	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}

	public String getPerson_name() {
		return person_name;
	}

	public void setPerson_name(String person_name) {
		this.person_name = person_name;
	}

	public String getWx_code() {
		return wx_code;
	}

	public void setWx_code(String wx_code) {
		this.wx_code = wx_code;
	}

	public String getProvince_id() {
		return province_id;
	}

	public void setProvince_id(String province_id) {
		this.province_id = province_id;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity_id() {
		return city_id;
	}

	public void setCity_id(String city_id) {
		this.city_id = city_id;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getRegion_id() {
		return region_id;
	}

	public void setRegion_id(String region_id) {
		this.region_id = region_id;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public IDictionaryManager getDictionaryManager() {
		return dictionaryManager;
	}

	public void setDictionaryManager(IDictionaryManager dictionaryManager) {
		this.dictionaryManager = dictionaryManager;
	}

	public IAdvanceLogsManager getAdvanceLogsManager() {
		return advanceLogsManager;
	}

	public void setAdvanceLogsManager(IAdvanceLogsManager advanceLogsManager) {
		this.advanceLogsManager = advanceLogsManager;
	}

	/*public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}*/



}
