package com.enation.app.shop.component.email.widget;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.app.shop.core.service.IMemberPointManger;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.eop.sdk.widget.AbstractWidget;
import com.enation.framework.jms.EmailModel;
import com.enation.framework.jms.EmailProducer;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.EncryptionUtil1;
import com.enation.framework.util.RequestUtil;

@Component("resend_regmail")
@Scope("prototype")
public class ReSendRegMailWidget extends AbstractWidget {
	
	private IMemberManager memberManager;
	private JavaMailSender mailSender;
	private EmailProducer mailMessageProducer;
	private IMemberPointManger memberPointManger;
	@Override
	protected void display(Map<String, String> params) {
		
		
		try{
			//重新发送激活邮件
			Member member = UserServiceFactory.getUserService().getCurrentMember();
			if(member == null){
				this.showErrorJson("请您先登录再重新发送激活邮件!");
				return;
			}
			member = memberManager.get(member.getMember_id());
			if(member == null){
				this.showErrorJson("用户不存在,请您先登录再重新发送激活邮件!");
				return;
			}
			if(member.getLast_send_email() != null && System.currentTimeMillis() / 1000 - member.getLast_send_email().intValue() < 2 * 60 * 60){
				this.showErrorJson("对不起，两小时之内只能重新发送一次激活邮件!");
				return;
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
	}

	@Override
	protected void config(Map<String, String> params) {
	 

	}

	public IMemberManager getMemberManager() {
		return memberManager;
	}

	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
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

	
	
}
