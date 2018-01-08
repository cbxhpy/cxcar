package com.enation.app.shop.core.action.api;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IRegionsManager;
import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.shop.core.action.mobile.MobileMemberAction;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.app.shop.core.service.IMemberPointManger;
import com.enation.app.shop.core.service.IPushMsgManager;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.user.IUserService;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.eop.sdk.utils.ValidCodeServlet;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.jms.EmailModel;
import com.enation.framework.jms.EmailProducer;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.EncryptionUtil1;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.HttpUtil;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.RequestUtil;
import com.enation.framework.util.StringUtil;

@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/shop")
@Action("member")
@SuppressWarnings({ "rawtypes", "unchecked", "serial","static-access" })
public class MemberApiAction extends WWAction {

	private IMemberManager memberManager;
	private IAdminUserManager adminUserManager;
	
	private IRegionsManager regionsManager;
	private String username;
	private String password;
	private String validcode;//验证码
	private String remember;//两周内免登录
	private Map memberMap;
	private String license;//同意注册协议
	private String email;
	private String friendid;
	private String invite;

	/**
	 * 上传头像变量
	 */
	private File faceFile ;
	private File face ;         //633行有单独的上传头像，没动，酌情处理，需要这里，
	private String faceFileName;
	
	private String faceFileFileName;

	private String photoServer;
	private String photoId;
	private String type;
	private String turename;

	/**
	 * 更改密码
	 */
	private String oldpassword;
	private String newpassword;
	private String re_passwd;
	
	private String passwd_re;
	
	private Integer province_id;
	private Integer city_id;
	private Integer region_id;
	private String province;
	private String city;
	private String region;
	private String address;
	private String zip;
	private String mobile;
	private String tel;
	private String nickname;
	private String sex;
	private String mybirthday;
	private String card_no;
	/**
	 * 搜索
	 */
	private Integer lvid; //会员级别id
	private String keyword; //关键词
	/**
	 * 重新发送激活邮件
	 */
	private JavaMailSender mailSender;
	private EmailProducer mailMessageProducer;
	private IMemberPointManger memberPointManger;

	private IPushMsgManager pushMsgManager;
	
	private File file ;
	private String fileFileName;
	
	/**
	 * 支付密码
	 */
	private String pay_password;
	private String bank_name;
	private String bank_no;
	
	/**
	 * 新增字段 -wbl
	 */
	private String validate_code;
	private String new_password;
	private String new_password_repeat;
	private String name;
	
	private String push_msg_id;
	
	/**
	 * 会员登录
	 * @param username:用户名,String型
	 * @param password:密码,String型
	 * @param validcode:验证码,String型
	 * @param remember:两周内免登录,String型
	 * @return json字串
	 * result  为1表示登录成功，0表示失败 ，int型
	 * message 为提示信息 ，String型
	 */
	public String login() {
		//if (this.validcode(validcode,"memberlogin") == 1) {
			int result = this.memberManager.login(username, password);
			if (result == 1) {
				// 两周内免登录
				if (remember != null && remember.equals("1")) {
					String cookieValue = EncryptionUtil1.authcode(
							"{username:\"" + username + "\",password:\"" + StringUtil.md5(password) + "\"}",
							"ENCODE", "", 0);
					HttpUtil.addCookie(ThreadContextHolder.getHttpResponse(), "JavaShopUser", cookieValue, 60 * 24 * 14);
				}
				this.showSuccessJson("登陆成功");
			}else{
				this.showErrorJson("账号密码错误");
			}
		//} else {
		//	this.showErrorJson("验证码错误！");
		//}
		return WWAction.JSON_MESSAGE;
	}

	public String seePushMsg() {
		
		IUserService userService = UserServiceFactory.getUserService();
		Member member = userService.getCurrentMember();
		
		if (member == null) {
			this.showErrorJson("请重新登录");
			return JSON_MESSAGE;
		}
		this.pushMsgManager.insertMemberPush(member.getMember_id(), push_msg_id);
		this.showSuccessJson("查看成功");
		return JSON_MESSAGE;
	}
	
	/**
	 * 注销会员登录
	 * @param 无
	 * @return json字串
	 * result  为1表示注销成功，0表示失败 ，int型
	 * message 为提示信息 ，String型
	 */
	public String logout() {
		this.showSuccessJson("注销成功");
		this.memberManager.logout();
		return WWAction.JSON_MESSAGE;
	}

	/**
	 * 修改会员密码
	 * @param oldpassword:原密码,String类型
	 * @param newpassword:新密码,String类型
	 * @return json字串
	 * result  为1表示修改成功，0表示失败 ，int型
	 * message 为提示信息 ，String型
	 */
	public String changePassword() {
		Member member = UserServiceFactory.getUserService().getCurrentMember();
		if(member==null){
			this.showErrorJson("尚未登陆，无权使用此api");
			return this.JSON_MESSAGE;
		}
		String oldPassword = this.getOldpassword();
		oldPassword = oldPassword == null ? "" : StringUtil.md5(oldPassword);
		if (oldPassword.equals(member.getPassword())) {
			String password = this.getNewpassword();
			String passwd_re = this.getRe_passwd();

			if (passwd_re.equals(password)) {
				try {
					memberManager.updatePassword(password);
					this.showSuccessJson("修改密码成功");
				} catch (Exception e) {
					if (this.logger.isDebugEnabled()) {
						logger.error(e.getStackTrace());
					}
					this.showErrorJson("修改密码失败");
				}
			} else {
				this.showErrorJson("修改失败！两次输入的密码不一致");
			}
		} else {
			this.showErrorJson("修改失败！原始密码不符");
		}
		return WWAction.JSON_MESSAGE;
	}
	
	/**
	 * 修改会员密码
	 * @author yexf
	 * 2016-11-16
	 * @return
	 */
	public String changePasswordWbl() {
		
		Member member = UserServiceFactory.getUserService().getCurrentMember();
		if(member==null){
			this.showErrorJson("请先登录");
			return this.JSON_MESSAGE;
		}
		
		String password = this.password;
		String new_password = this.new_password;
		String new_password_repeat = this.new_password_repeat;
		
		if(StringUtil.isEmpty(password) || StringUtil.isEmpty(new_password) || StringUtil.isEmpty(new_password_repeat)){
			this.showErrorJson("参数有误");
			return this.JSON_MESSAGE;
		}
		
		if(StringUtil.md5(password).equals(member.getPassword())){

			if(new_password_repeat.equals(new_password)){
				try {
					memberManager.updatePasswordWbl(StringUtil.md5(new_password));
					this.showSuccessJson("修改成功");
				}catch (Exception e){
					if(this.logger.isDebugEnabled()){
						logger.error(e.getStackTrace());
					}
					this.showErrorJson("修改失败");
				}
			}else{
				this.showErrorJson("两次密码输入不一致");
			}
		}else{
			this.showErrorJson("原密码错误");
		}
		return WWAction.JSON_MESSAGE;
	}
	
	/**
	 * 忘记密码
	 * @author yexf
	 * 2016-11-13
	 * @return
	 */
	public String forgetPassword() {
		
		HashMap<String, Object> Code = MobileMemberAction.Code;
		
		String validate_code = this.validate_code;
		String username = this.username;
		String new_password = this.new_password;
		String new_password_repeat = this.new_password_repeat;
		
		if(StringUtil.isEmpty(username)){
			this.showErrorJson("请填写手机号");
			return WWAction.APPLICAXTION_JSON;
		}
		
		boolean uname_isphone = StringUtil.isMobileNO(username);
		if(!uname_isphone){
			this.renderJson("1", "手机号码格式不对", "");
			return WWAction.APPLICAXTION_JSON;
		}
		
		if(Code.get(username) == null){
			this.showErrorJson("请发送验证码");
		}else if(StringUtil.isEmpty(username) || StringUtil.isEmpty(validate_code) || 
				StringUtil.isEmpty(new_password) || StringUtil.isEmpty(new_password_repeat)){
			this.showErrorJson("请完整表单信息");
		}else if(new_password.length() < 6 || new_password.length() > 12){
			this.showErrorJson("密码长度为6-12位");
		}else if(!new_password.equals(new_password_repeat)){
			this.showErrorJson("两次密码输入不一致");
		}else if(!Code.get(username).toString().equals(validate_code)){
			this.showErrorJson("验证码错误");
		}else{
			this.memberManager.appUpdatePassword(username, StringUtil.md5(new_password));
			//清除验证码
			Code.remove(username);
			this.showSuccessJson("修改成功");
		}
		
		/*String oldPassword = this.getOldpassword();
		oldPassword = oldPassword == null ? "" : StringUtil.md5(oldPassword);
		if (oldPassword.equals(member.getPassword())) {
			String password = this.getNewpassword();
			String passwd_re = this.getRe_passwd();

			if (passwd_re.equals(password)) {
				try {
					memberManager.updatePassword(password);
					this.showSuccessJson("修改密码成功");
				} catch (Exception e) {
					if (this.logger.isDebugEnabled()) {
						logger.error(e.getStackTrace());
					}
					this.showErrorJson("修改密码失败");
				}
			} else {
				this.showErrorJson("修改失败！两次输入的密码不一致");
			}
		} else {
			this.showErrorJson("修改失败！原始密码不符");
		}*/
		return WWAction.JSON_MESSAGE;
	}
	
	/**
	 * 验证原密码输入是否正确
	 * @param oldpassword:密码，String型
	 * @return json字串
	 * result  为1表示原密码正确，0表示失败 ，int型
	 * message 为提示信息 ，String型
	 */
	public String password(){
		Member member = UserServiceFactory.getUserService().getCurrentMember();
		String old = oldpassword;
		String oldPassword = StringUtil.md5(old);
		if (oldPassword.equals(member.getPassword())){
			this.showSuccessJson("正确");
		}else{
			this.showErrorJson("输入原始密码错误");
		}
		return WWAction.JSON_MESSAGE;
	}
	
	
	
	/**
	 * 搜索会员(要求管理员身份)
	 * @param lvid:会员级别id，如果为空则搜索全部会员级别，int型
	 * @param keyword:搜索关键字,可为空，String型
	 * @return json字串
	 * result  为1表示搜索成功，0表示失败 ，int型
	 * data: 会员列表
	 * {@link Member}
	 */
	public String search(){
		try{
			if(this.adminUserManager.getCurrentUser()==null){
				this.showErrorJson("无权访问此api");
				return this.JSON_MESSAGE;
			}
			memberMap = new HashMap();
			memberMap.put("lvId", lvid);
			memberMap.put("keyword", keyword);
			memberMap.put("stype", 0);
			List memberList  =this.memberManager.search(memberMap);
			this.json = JsonMessageUtil.getListJson(memberList);
		}catch(Throwable e){
			this.logger.error("搜索会员出错", e);
			this.showErrorJson("搜索会员出错");
			
		}
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 检测username是否存在，并生成json返回给客户端
	 */
	public String checkname() {
		int result = this.memberManager.checkname(username);
		if(result==0){
			this.showSuccessJson("会员名称可以使用！");
		}else{
			this.showErrorJson("该会员名称已经存在！");
		}
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 检测支付密码是否正常，并生成json返回给客户端
	 */
	public String checkPaypassword() {
		
		Member member = UserServiceFactory.getUserService().getCurrentMember();
		
		if(StringUtil.md5(pay_password).equals(member.getPay_password())){
			this.showSuccessJson("支付密码正确");
		}else{
			this.showErrorJson("支付密码错误！");
		}
		return this.JSON_MESSAGE;
	}
	
	public String validatePaypassword() {
		
		Member member = UserServiceFactory.getUserService().getCurrentMember();
		
		if(StringUtil.md5(pay_password).equals(member.getPassword())){
			this.showErrorJson("支付密码与登录密码不能一样");
		}else{
			this.showSuccessJson("支付验证通过");
		}
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 检测email是否存在，并生成json返回给客户端
	 */
	public String checkemail() {
		int result = this.memberManager.checkemail(email);
		if(result==0){
			this.showSuccessJson("邮箱不存在，可以使用");
		}else{
			this.showErrorJson("该邮箱已经存在！");
		}
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 会员注册
	 */
	public String registerWbl() {
		
		HashMap<String, Object> Code = MobileMemberAction.Code;
		
		String validate_code = this.validate_code;
		String username = this.username;
		String password = this.password;
		
		if(StringUtil.isEmpty(username)){
			this.showErrorJson("请填写手机号");
			return WWAction.APPLICAXTION_JSON;
		}
		
		boolean uname_isphone = StringUtil.isMobileNO(username);
		if(!uname_isphone){
			this.renderJson("1", "手机号码格式不对", "");
			return WWAction.APPLICAXTION_JSON;
		}
		
		if(Code.get(username) == null){
			this.showErrorJson("请发送验证码");
			return this.JSON_MESSAGE;
		}
		if(StringUtil.isEmpty(username) || StringUtil.isEmpty(validate_code) || 
				StringUtil.isEmpty(password)){
			this.showErrorJson("请完整表单信息");
			return this.JSON_MESSAGE;
		}
		if(password.length() < 6 || password.length() > 12){
			this.showErrorJson("密码长度为6-12位");
			return this.JSON_MESSAGE;
		}
		if(!Code.get(username).toString().equals(validate_code)){
			this.showErrorJson("验证码错误");
			return this.JSON_MESSAGE;
		}

		Member member = new Member();
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String registerip = request.getRemoteAddr();

		if (memberManager.checkname(username) > 0) {
			this.showErrorJson("手机号已存在");
			return this.JSON_MESSAGE;
		}
		
		member.setMobile("");
		member.setUname(username);
		member.setPassword(StringUtil.md5(password));
		member.setEmail("");
		member.setRegisterip(registerip);
		
		int result = this.memberManager.appRegister(member);
		if (result == 1) { // 添加成功
			this.showSuccessJson("注册成功");
		}else{
			this.showErrorJson("手机号" + member.getUname() + "已存在!");
		}
		
		//清除验证码
		Code.remove(username);
		
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 会员注册
	 */
	public String register() {
		if (this.validcode(validcode,"memberreg") == 0) {
			this.showErrorJson("验证码输入错误!");
			return this.JSON_MESSAGE;
		}
		if (!"agree".equals(license)) {
			this.showErrorJson("同意注册协议才可以注册!");
			return this.JSON_MESSAGE;
		}

		Member member = new Member();
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String registerip = request.getRemoteAddr();

		if (StringUtil.isEmpty(username)) {
			this.showErrorJson("用户名不能为空！");
			return this.JSON_MESSAGE;
		}
		if (username.length() < 2 || username.length() > 20) {
			this.showErrorJson("用户名的长度为2-20个字符！");
			return this.JSON_MESSAGE;
		}
		if (username.contains("@")) {
			this.showErrorJson("用户名中不能包含@等特殊字符！");
			return this.JSON_MESSAGE;
		}
		if (StringUtil.isEmpty(email)) {
			this.showErrorJson("注册邮箱不能为空！");
			return this.JSON_MESSAGE;
		}
		if (!StringUtil.validEmail(email)) {
			this.showErrorJson("注册邮箱格式不正确！");
			return this.JSON_MESSAGE;
		}
		if (StringUtil.isEmpty(password)) {
			this.showErrorJson("密码不能为空！");
			return this.JSON_MESSAGE;
		}
		if (memberManager.checkname(username) > 0) {
			this.showErrorJson("此用户名已经存在，请您选择另外的用户名!");
			return this.JSON_MESSAGE;
		}
		if (memberManager.checkemail(email) > 0) {
			this.showErrorJson("此邮箱已经注册过，请您选择另外的邮箱!");
			return this.JSON_MESSAGE;
		}
		
		if (!password.equals(passwd_re)) {
			this.showErrorJson("密码不一致");
			return this.JSON_MESSAGE;
		}
		
		if(memberPointManger.checkIsOpen(IMemberPointManger.TYPE_REGISTER_LINK)){
			if(StringUtil.isEmpty(invite)){
				this.showErrorJson("推荐人不能为空！");
				return this.JSON_MESSAGE;
			}
		}

		member.setMobile("");
		member.setUname(username);
		member.setPassword(password);
		member.setEmail(email);
		member.setRegisterip(registerip);
		
		if (!StringUtil.isEmpty(invite)) {
			
			if (invite.trim().equals(username.trim())) {
				this.showErrorJson("推荐人的用户名请不要填写自己的用户名!");
				return this.JSON_MESSAGE;
			}
			
			Member parentMember = this.memberManager.getMemberByUname(invite);
			if (parentMember != null && parentMember.getInfo_full()==1) {
				member.setParentid(parentMember.getMember_id());
			}else if(parentMember != null && parentMember.getInfo_full()==0){
				this.showErrorJson("您填写的推荐人会员资料不完善,暂冻结!");
				return this.JSON_MESSAGE;
			}
			else{
				this.showErrorJson("您填写的推荐人不存在!");
				return this.JSON_MESSAGE;
			}
		} 

		int result = memberManager.register(member);
		if (result == 1) { // 添加成功
			this.memberManager.login(username, password);
			//String forward = request.getParameter("forward");
			String mailurl = "http://mail." + StringUtils.split(member.getEmail(), "@")[1];
			/*if (forward != null && !forward.equals("")) {
				String message = request.getParameter("message");
				this.setMsg(message);
			} else {
				this.setMsg("注册成功");
			}*/
			this.json = JsonMessageUtil.getStringJson("mailurl", mailurl);
		} else {
			this.showErrorJson("用户名[" + member.getUname() + "]已存在!");
		}
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 重新发送激活邮件
	 */
	public String reSendRegMail(){
		try{
			//重新发送激活邮件
			Member member = UserServiceFactory.getUserService().getCurrentMember();
			if(member == null){
				this.showErrorJson("请您先登录再重新发送激活邮件!");
				return this.JSON_MESSAGE;
			}
			member = memberManager.get(member.getMember_id());
			if(member == null){
				this.showErrorJson("用户不存在,请您先登录再重新发送激活邮件!");
				return this.JSON_MESSAGE;
			}
			if(member.getLast_send_email() != null && System.currentTimeMillis() / 1000 - member.getLast_send_email().intValue() < 2 * 60 * 60){
				this.showErrorJson("对不起，两小时之内只能重新发送一次激活邮件!");
				return this.JSON_MESSAGE;
			}
	
			EopSite site  = EopContext.getContext().getCurrentSite();
			String domain =RequestUtil.getDomain();
			String checkurl  = domain+"/memberemailcheck.html?s="+ EncryptionUtil1.authcode(member.getMember_id()+","+member.getRegtime(), "ENCODE","",0);
			EmailModel emailModel = new EmailModel();
			emailModel.getData().put("username", member.getUname());
			emailModel.getData().put("checkurl", checkurl);
			emailModel.getData().put("sitename", site.getSitename());
			emailModel.getData().put("logo", site.getLogofile());
			emailModel.getData().put("domain", domain);
			if (memberPointManger.checkIsOpen(IMemberPointManger.TYPE_EMIAL_CHECK) ){
				int point =memberPointManger.getItemPoint(IMemberPointManger.TYPE_EMIAL_CHECK+"_num");
				int mp =memberPointManger.getItemPoint(IMemberPointManger.TYPE_EMIAL_CHECK+"_num_mp");
				emailModel.getData().put("point", point);
				emailModel.getData().put("mp", mp);
			}
			emailModel.setTitle(member.getUname()+"您好，"+site.getSitename()+"会员注册成功!");
			emailModel.setEmail(member.getEmail());
			emailModel.setTemplate("reg_email_template.html");
			emailModel.setEmail_type("邮箱激活");
			mailMessageProducer.send(emailModel);
			member.setLast_send_email(DateUtil.getDateline());
			memberManager.edit(member);
			this.showSuccessJson("激活邮件发送成功，请登录您的邮箱 " + member.getEmail() + " 进行查收！");
		}catch(RuntimeException e){
			this.showErrorJson(e.getMessage());
		}
		return this.JSON_MESSAGE;
	}
	
	
	public String saveInfo(){
		IUserService userService = UserServiceFactory.getUserService();
		Member member = userService.getCurrentMember();
 
		member = memberManager.get(member.getMember_id());
		
		String encoding = EopSetting.ENCODING;
		if (StringUtil.isEmpty(encoding)) {
			encoding = "UTF-8";
		}
		//先上传图片
		String faceField = "faceFile";
		
		if(file!=null){
		
			//判断文件类型
			String allowTYpe = "gif,jpg,bmp,png";
			if (!fileFileName.trim().equals("") && fileFileName.length() > 0) {
				String ex = fileFileName.substring(fileFileName.lastIndexOf(".") + 1, fileFileName.length());
				if(allowTYpe.toString().indexOf(ex.toLowerCase()) < 0){
					this.showErrorJson("对不起,只能上传gif,jpg,bmp,png格式的图片！");
					return this.JSON_MESSAGE;
				}
			}
			
			//判断文件大小
			
			if(file.length() > 1024 * 1024){
				this.showErrorJson("'对不起,图片不能大于1mb！");
				return this.JSON_MESSAGE;
			}
			
			String imgPath=	UploadUtil.upload(file, fileFileName, faceField);
			member.setFace(imgPath);
		}
		
		HttpServletRequest request =  ThreadContextHolder.getHttpRequest();
		
		if(StringUtil.isEmpty(mybirthday)){
			member.setBirthday(0L);
		}else{
			member.setBirthday(DateUtil.getDatelineLong(mybirthday));
		}

		
		
		member.setProvince_id(province_id);
		member.setCity_id(city_id);
		member.setRegion_id(region_id);
		member.setProvince(province);
		member.setCity(city);
		member.setRegion(region);
		member.setAddress(address);
		member.setZip(zip);
		if(mobile!=null){
			member.setMobile(mobile);
		}
		member.setTel(tel);
		if(nickname!=null){
			member.setNickname(nickname);
		}
		if(card_no!=null){
			member.setCard_no(card_no);
		}
		member.setSex(Integer.valueOf(sex));

		
		if(pay_password!=null){
			member.setPay_password(StringUtil.md5(pay_password));
		}
		
		if(StringUtil.md5(pay_password).equals(member.getPassword())){
			this.showErrorJson("支付密码与登录密码不能一样");
			return this.JSON_MESSAGE;
		}
		
		if(bank_name!=null){
			member.setBank_name(bank_name);
		}
		if(bank_no!=null){
			member.setBank_no(bank_no);
		}
	
		// 身份
		String midentity = request.getParameter("member.midentity");
		if (!StringUtil.isEmpty(midentity)) {
			member.setMidentity(StringUtil.toInt(midentity));
		} else {
			member.setMidentity(0);
		}
		// String pw_question = request.getParameter("member.pw_question");
		// member.setPw_question(pw_question);
		// String pw_answer = request.getParameter("member.pw_answer");
		// member.setPw_answer(pw_answer);
		try {
			// 判断否需要增加积分
			boolean addPoint = false;
			if (member.getInfo_full() == 0 && !StringUtil.isEmpty(member.getName())
					&& !StringUtil.isEmpty(member.getNickname()) && !StringUtil.isEmpty(member.getProvince())
					&& !StringUtil.isEmpty(member.getCity()) && !StringUtil.isEmpty(member.getRegion())
					&& (!StringUtil.isEmpty(member.getMobile()) || !StringUtil.isEmpty(member.getTel()))
					&& !StringUtil.isEmpty(member.getCard_no())) {
				addPoint = true;
			}
			// 增加积分
			if (addPoint) {
				member.setInfo_full(1);
				memberManager.edit(member);
				if (memberPointManger.checkIsOpen(IMemberPointManger.TYPE_FINISH_PROFILE)) {
					int point = memberPointManger.getItemPoint(IMemberPointManger.TYPE_FINISH_PROFILE + "_num");
					int mp = memberPointManger.getItemPoint(IMemberPointManger.TYPE_FINISH_PROFILE + "_num_mp");
					memberPointManger.add(member.getMember_id(), point,	"完善个人资料", null, mp);
				}
			} else {
				memberManager.edit(member);
			}
			this.showSuccessJson("编辑个人资料成功！");
			return this.JSON_MESSAGE;
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				logger.error(e.getStackTrace());
			}
			this.showErrorJson("编辑个人资料失！");
			
			return this.JSON_MESSAGE;
		}
	} 


	//************to宏俊：以api先不用书写文档****************/
	protected String toUrl(String path) {
		String urlBase = EopSetting.IMG_SERVER_DOMAIN
				+ EopContext.getContext().getContextPath();
		return path.replaceAll("fs:", urlBase);
	}

	protected String makeFilename(String subFolder) {
		String ext = FileUtil.getFileExt(photoServer);
		String fileName = photoId + "_" + type + "." + ext;

		String filePath = EopSetting.IMG_SERVER_PATH
				+ EopContext.getContext().getContextPath() + "/attachment/";
		if (subFolder != null) {
			filePath += subFolder + "/";
		}

		filePath += fileName;
		return filePath;
	}

	/**
	 * 保存从Flash编辑后返回的头像，保存二次，一大一小两个头像
	 * @return
	 */
	public String saveAvatar() {
		String targetFile = makeFilename("avatar");

		int potPos = targetFile.lastIndexOf('/') + 1;
		String folderPath = targetFile.substring(0, potPos);
		FileUtil.createFolder(folderPath);

		try {
			File file = new File(targetFile);

			if (!file.exists()) {
				file.createNewFile();
			}

			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			FileOutputStream dos = new FileOutputStream(file);
			int x = request.getInputStream().read();
			while (x > -1) {
				dos.write(x);
				x = request.getInputStream().read();
			}
			dos.flush();
			dos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if ("big".equals(type)) {
			Member member = UserServiceFactory.getUserService()
					.getCurrentMember();
			member.setFace("fs:/attachment/avatar/" + photoId + "_big."
					+ FileUtil.getFileExt(photoServer));
			memberManager.edit(member);
		}

		json = "{\"data\":{\"urls\":[\"" + targetFile
				+ "\"]},\"status\":1,\"statusText\":\"保存存成功\"}";

		return WWAction.JSON_MESSAGE;
	}

	/**
	 * 上传头像文件
	 * @return
	 */
	public String uploadAvatar() {
		JSONObject jsonObject = new JSONObject();
		try {
			if (faceFile != null) {
				String file = UploadUtil.upload(face, faceFileName, "avatar");
				Member member = UserServiceFactory.getUserService()
						.getCurrentMember();
				jsonObject.put("result", 1);
				jsonObject.put("member_id", member.getMember_id());
				jsonObject.put("url", toUrl(file));
				jsonObject.put("message", "操作成功！");
			}
		} catch (Exception e) {
			jsonObject.put("result", 0);
			jsonObject.put("message", "操作失败！");
		}

		this.json = jsonObject.toString();

		return WWAction.JSON_MESSAGE;
	}

	/**
	 * 上传头像文件
	 * @return
	 */
	public String updateMember() {
		
		try {
			
			String name = this.name;
			
			Member member = UserServiceFactory.getUserService()
					.getCurrentMember();
			if(member == null){
				this.showErrorJson("请重新登录");
			}
			member = this.memberManager.getMemberByMemberId(member.getMember_id().toString());
			if(member == null){
				this.showErrorJson("会员不存在");
			}
			if (face != null) {
				String file = UploadUtil.upload(face, faceFileName, "avatar");
				member.setFace(file);
			}
			if(!StringUtil.isEmpty(name)){
				member.setName(name);
			}
			
			this.memberManager.edit(member);
			// 图片地址转换 fs->服务器地址
			String temp = "";
			if(face != null){
				temp = UploadUtil.replacePath(member.getFace());
			}
			
			this.json="{\"result\":1,\"message\":\"修改成功\",\"file\":\""+temp+"\"}";
			
		} catch (Exception e) {
			this.showErrorJson("出现错误 ，请重试");
		}

		return WWAction.JSON_MESSAGE;
	}
	
	/**
	 * 校验验证码
	 * @param validcode
	 * @param name (1、memberlogin:会员登录  2、memberreg:会员注册)
	 * @return 1成功 0失败
	 */
	private int validcode(String validcode, String name) {
		if (validcode == null) {
			return 0;
		}

		String code = (String) ThreadContextHolder.getSessionContext().getAttribute(ValidCodeServlet.SESSION_VALID_CODE + name);
		if(code == null){
			return 0;
		}else{
			if(!code.equalsIgnoreCase(validcode)){
				return 0;
			}
		}
		return 1;
	}
	public IMemberManager getMemberManager() {
		return memberManager;
	}

	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public File getFace() {
		return face;
	}

	public void setFace(File face) {
		this.face = face;
	}

	public String getFaceFileName() {
		return faceFileName;
	}

	public void setFaceFileName(String faceFileName) {
		this.faceFileName = faceFileName;
	}

	public String getPhotoServer() {
		return photoServer;
	}

	public void setPhotoServer(String photoServer) {
		this.photoServer = photoServer;
	}

	public String getPhotoId() {
		return photoId;
	}

	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOldpassword() {
		return oldpassword;
	}

	public void setOldpassword(String oldpassword) {
		this.oldpassword = oldpassword;
	}

	public String getNewpassword() {
		return newpassword;
	}

	public void setNewpassword(String newpassword) {
		this.newpassword = newpassword;
	}

	public String getRe_passwd() {
		return re_passwd;
	}

	public void setRe_passwd(String re_passwd) {
		this.re_passwd = re_passwd;
	}

	public Integer getLvid() {
		return lvid;
	}

	public void setLvid(Integer lvid) {
		this.lvid = lvid;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}

	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}

	public String getValidcode() {
		return validcode;
	}

	public void setValidcode(String validcode) {
		this.validcode = validcode;
	}

	public String getRemember() {
		return remember;
	}

	public void setRemember(String remember) {
		this.remember = remember;
	}

	public Map getMemberMap() {
		return memberMap;
	}

	public void setMemberMap(Map memberMap) {
		this.memberMap = memberMap;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFriendid() {
		return friendid;
	}

	public void setFriendid(String friendid) {
		this.friendid = friendid;
	}

	public JavaMailSender getMailSender() {
		return mailSender;
	}

	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public EmailProducer getMailMessageProducer() {
		return mailMessageProducer;
	}

	public void setMailMessageProducer(EmailProducer mailMessageProducer) {
		this.mailMessageProducer = mailMessageProducer;
	}

	public IMemberPointManger getMemberPointManger() {
		return memberPointManger;
	}

	public void setMemberPointManger(IMemberPointManger memberPointManger) {
		this.memberPointManger = memberPointManger;
	}

	public File getFaceFile() {
		return faceFile;
	}

	public void setFaceFile(File faceFile) {
		this.faceFile = faceFile;
	}

	public String getTurename() {
		return turename;
	}

	public void setTurename(String turename) {
		this.turename = turename;
	}

	public IRegionsManager getRegionsManager() {
		return regionsManager;
	}

	public void setRegionsManager(IRegionsManager regionsManager) {
		this.regionsManager = regionsManager;
	}

	public String getFaceFileFileName() {
		return faceFileFileName;
	}

	public void setFaceFileFileName(String faceFileFileName) {
		this.faceFileFileName = faceFileFileName;
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

	public Integer getProvince_id() {
		return province_id;
	}

	public void setProvince_id(Integer province_id) {
		this.province_id = province_id;
	}

	public Integer getCity_id() {
		return city_id;
	}

	public void setCity_id(Integer city_id) {
		this.city_id = city_id;
	}

	public Integer getRegion_id() {
		return region_id;
	}

	public void setRegion_id(Integer region_id) {
		this.region_id = region_id;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
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

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getMybirthday() {
		return mybirthday;
	}

	public void setMybirthday(String mybirthday) {
		this.mybirthday = mybirthday;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getInvite() {
		return invite;
	}

	public void setInvite(String invite) {
		this.invite = invite;
	}

	public String getCard_no() {
		return card_no;
	}

	public void setCard_no(String card_no) {
		this.card_no = card_no;
	}

	public String getPay_password() {
		return pay_password;
	}

	public void setPay_password(String pay_password) {
		this.pay_password = pay_password;
	}

	public String getBank_name() {
		return bank_name;
	}

	public void setBank_name(String bank_name) {
		this.bank_name = bank_name;
	}

	public String getBank_no() {
		return bank_no;
	}

	public void setBank_no(String bank_no) {
		this.bank_no = bank_no;
	}

	public String getPasswd_re() {
		return passwd_re;
	}

	public void setPasswd_re(String passwd_re) {
		this.passwd_re = passwd_re;
	}

	public String getValidate_code() {
		return validate_code;
	}

	public void setValidate_code(String validate_code) {
		this.validate_code = validate_code;
	}

	public String getNew_password() {
		return new_password;
	}

	public void setNew_password(String new_password) {
		this.new_password = new_password;
	}

	public String getNew_password_repeat() {
		return new_password_repeat;
	}

	public void setNew_password_repeat(String new_password_repeat) {
		this.new_password_repeat = new_password_repeat;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public IPushMsgManager getPushMsgManager() {
		return pushMsgManager;
	}

	public void setPushMsgManager(IPushMsgManager pushMsgManager) {
		this.pushMsgManager = pushMsgManager;
	}

	public String getPush_msg_id() {
		return push_msg_id;
	}

	public void setPush_msg_id(String push_msg_id) {
		this.push_msg_id = push_msg_id;
	}

	
}
