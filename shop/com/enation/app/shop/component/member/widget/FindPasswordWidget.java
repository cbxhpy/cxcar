package com.enation.app.shop.component.member.widget;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.HeaderConstants;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.widget.AbstractMemberWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.jms.EmailModel;
import com.enation.framework.jms.EmailProducer;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.EncryptionUtil1;
import com.enation.framework.util.RequestUtil;
import com.enation.framework.util.StringUtil;

/**
 * 找回密码挂件
 * @author kingapex
 *2012-4-1上午8:09:10
 */
@Component("member_findpassword")
@Scope("prototype")
public class FindPasswordWidget extends AbstractMemberWidget {
	private IMemberManager memberManager; 
	private EmailProducer mailMessageProducer;
	
	@Override
	protected void config(Map<String, String> params) {
		
	}

	
	@Override
	protected void display(Map<String, String> params) {
		
		EopSite  site  = EopContext.getContext().getCurrentSite();
		
		this.putData(HeaderConstants.title, "忘记密码 - "+site.getSitename());
		this.putData(HeaderConstants.keywords, "忘记密码,"+site.getSitename());
		
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		String action = request.getParameter("action");
		this.showMenu(false);
		if("find".equals(action)){
			this.find();
		}else if("restorepwd".equals(action)){
			this.restorePwd();
		}
	}
	
	private void find(){
		EopSite  site  = EopContext.getContext().getCurrentSite();
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		String email = request.getParameter("email");
		if(email==null || !StringUtil.validEmail(email)){
			this.showError("请输入正确的邮箱地址");
			return;
		}
		Member member =	this.memberManager.getMemberByEmail(email);
		if(member==null){
			this.showError("["+ email +"]不存在!");
			return;
		}
		String domain =RequestUtil.getDomain();
		String initCode = member.getMember_id()+","+member.getRegtime();
		String edit_url  =domain+ "/modifypassword.html?s="+ EncryptionUtil1.authcode(initCode, "ENCODE","",0);
		
		EmailModel emailModel = new EmailModel();
		emailModel.getData().put("logo", site.getLogofile());
		emailModel.getData().put("sitename", site.getSitename());
		emailModel.getData().put("username", member.getUname());
		emailModel.getData().put("checkurl", edit_url);
		emailModel.setTitle("找回您的登录密码--"+site.getSitename());
		emailModel.setEmail(member.getEmail());
		emailModel.setTemplate("findpass_email_template.html");
		emailModel.setEmail_type("找回密码");
		mailMessageProducer.send(emailModel);
		
		this.memberManager.updateFindCode(member.getMember_id(),DateUtil.getDateline()+"");
		this.showSuccess("请登录"+ email+"查收邮件并完成密码修改。", "登录页面", "login.html"); 	
		
//		
//		if( question==null || answer==null
//			||!question.equals(member.getPw_question() )
//		    ||!answer.equals( member.getPw_answer() )){
//			this.showError("问题或答案输入错误!");
//			return;
//		}
//	
//		ThreadContextHolder.getSessionContext().setAttribute("can_find_pwd", "yes");
//		this.putData("memberid", member.getMember_id());
//		this.putData("username", username);
//		this.setPageName("TypeNewPassword");
	
	}
	
	public void  restorePwd(){
		Object flag = ThreadContextHolder.getSessionContext().getAttribute("can_find_pwd");
		if("yes".equals(flag)){
			HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
			String memberid = request.getParameter("memberid");
			String password = request.getParameter("password");
			this.memberManager.updatePassword(Integer.valueOf(memberid),password);
			 ThreadContextHolder.getSessionContext().removeAttribute("can_find_pwd");
			this.showSuccess("密码成功更新", "登录页面", "member_login.html");
			
		}else{
			this.showError("非法操作!");
		}
	}
	

	public IMemberManager getMemberManager() {
		return memberManager;
	}

	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}


	public EmailProducer getMailMessageProducer() {
		return mailMessageProducer;
	}


	public void setMailMessageProducer(EmailProducer mailMessageProducer) {
		this.mailMessageProducer = mailMessageProducer;
	}
	



}
