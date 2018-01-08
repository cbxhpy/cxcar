package com.enation.app.shop.component.email.widget;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.eop.sdk.widget.AbstractWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.EncryptionUtil1;

@Component("member_email_check")
@Scope("prototype")
public class MemberEmailCheckWidget extends AbstractWidget {
	private IMemberManager memberManager;
	@Override
	protected void config(Map<String, String> params) {
		
	}

	@Override
	protected void display(Map<String, String> params) {

		try{
			String s = ThreadContextHolder.getHttpRequest().getParameter("s");
	 
			String str = EncryptionUtil1.authcode(s, "DECODE","",0);
			String[] array = StringUtils.split(str,",");
			if(array.length!=2) throw new RuntimeException("验证字串不正确");
			int memberid  = Integer.valueOf(array[0]);
			long regtime = Long.valueOf(array[1]);
			
			Member member = memberManager.get(memberid);
			if(member.getRegtime() != regtime){
				this.showError("验证字串不正确");
				return ;
			}
		 
		 
			if(member.getIs_cheked()==0){
				memberManager.checkEmailSuccess( member);	
				this.showSuccess(member.getUname() +"您好，您的邮箱验证成功!", "会员中心首页", "member_index.html");
			}else{
				this.showError(member.getUname() +"您好，验证失败，您已经验证过该邮箱!", "会员中心首页", "member_index.html");
			}
			
		}catch(RuntimeException e){
			this.showError("验证地址不正确");
		}
		
	}
 
	
	public IMemberManager getMemberManager() {
		return memberManager;
	}

	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}

}
