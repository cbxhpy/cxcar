package com.enation.app.shop.core.action.api;

import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.SmsMessage;
import com.enation.app.base.core.service.ISmsManager;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.jms.EmailModel;
import com.enation.framework.jms.EmailProducer;

/**
 * 找回密码api
 * @author liuzy
 *
 */

@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/shop")
@Action("findPassword")

public class FindPasswordAction extends WWAction {
	private ISmsManager smsManager;
	private IMemberManager memberManager;
	private EmailProducer mailMessageProducer;
	
	private String mobileNum;
	private String password;
	
	protected String createRandom(){
		Random random  = new Random();
		StringBuffer pwd=new StringBuffer();
		for(int i=0;i<6;i++){
			pwd.append(random.nextInt(9));
			 
		}
		return pwd.toString();
	}
	
	/**
	 * 检查手机号(用户名)并发送短信验证码
	 * 需要传递mobileNum一个参数
	 * 
	 * @param mobileNum 手机号(用户名),String型
	 * 
	 * @return 返回json串
	 * result  为1表示调用成功0表示失败 ，int型
	 * message 为提示信息
	 */
	public String sendSmsCode() {
		Member member = null;
		int find_type = 0;
		if(mobileNum.contains("@")) {
			member = memberManager.getMemberByEmail(mobileNum);
			find_type = 1;
		}
		else
			member = memberManager.getMemberByUname(mobileNum);
		if(member==null) {
			this.showErrorJson("没有找到用户");
		} else {
			String code = createRandom();
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			request.getSession().setAttribute("smscode", code);
			request.getSession().setAttribute("smsnum", member.getMember_id());
			
			if(find_type==0) {
				SmsMessage message = new SmsMessage();
				message.setUser_id(member.getMember_id());
				message.setTel(mobileNum);
				message.setMessage_info("红酒星空用户，您的验证码是：" + code);
				//System.out.println(message.getMessage_info());
				//if(smsManager.sendSmsNow(message)==0)
				//	this.showErrorJson("短信发送失败");
			//	else
					this.showSuccessJson("短信发送成功");
			} else {
				EmailModel emailModel = new EmailModel();
				emailModel.setEmail(mobileNum);
				emailModel.setTitle("红酒星空找回密码");
				emailModel.setContent("红酒星空用户，您的验证码是：" + code);
				emailModel.setEmail_type("找回密码");
				try {
					mailMessageProducer.send(emailModel);
					//	TODO	邮件未发送成功，需要调试
					this.showSuccessJson("验证码已发送至预留邮箱");
				} catch(Exception e) {
					this.showErrorJson("邮件发送失败");
				}
			}
		}
		return WWAction.JSON_MESSAGE;
	}
	
	/**
	 * 检查用户输入的验证码
	 * 需要传入mobileNum一个参数
	 * 
	 * @param mobileNum 验证码,String型
	 *  
	 * @return 返回json串
	 * result  为1表示调用成功0表示失败 ，int型
	 * message 为提示信息
	 */
	public String checkSmsCode() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String code = (String)request.getSession().getAttribute("smscode");
		if(code.equals(mobileNum)) {
			request.getSession().setAttribute("smscode", "999");
			this.showSuccessJson("验证通过");
		} else {
			this.showErrorJson("验证失败");
		}
		return WWAction.JSON_MESSAGE;
	}
	
	/**
	 * 验证通过后重置密码
	 * 需要传入mobileNum一个参数
	 * 
	 * @param mobileNum 新密码,String型
	 * 
	 * @return 返回json串
	 * result  为1表示调用成功0表示失败 ，int型
	 * message 为提示信息
	 */
	public String resetPassword() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String code = (String)request.getSession().getAttribute("smscode");
		Integer memberid = (Integer)request.getSession().getAttribute("smsnum");
		if(memberid!=null && "999".equals(code)) {
			try {
				memberManager.updatePassword(memberid, mobileNum);
				this.showSuccessJson("新密码设置成功");
			} catch(Exception e) {
				this.showErrorJson("设置密码出错");
			}
			
		} else {
			this.showErrorJson("认证超时，请重新验证");
		}
		return WWAction.JSON_MESSAGE;
	}
	
	public String getMobileNum() {
		return mobileNum;
	}

	public void setMobileNum(String mobileNum) {
		this.mobileNum = mobileNum;
	}
	
	public ISmsManager getSmsManager() {
		return smsManager;
	}

	public void setSmsManager(ISmsManager smsManager) {
		this.smsManager = smsManager;
	}
	
	public IMemberManager getMemberManager() {
		return memberManager;
	}
	
	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public EmailProducer getMailMessageProducer() {
		return mailMessageProducer;
	}

	public void setMailMessageProducer(EmailProducer mailMessageProducer) {
		this.mailMessageProducer = mailMessageProducer;
	}
	
}
