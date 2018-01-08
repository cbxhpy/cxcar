package com.enation.app.shop.component.member.widget;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.eop.sdk.widget.AbstractWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.StringUtil;

/**
 * 会员登录bar
 * 
 * @author kingapex
 * 
 */
@Component("member_login_bar")
@Scope("prototype")
public class MemberLoginBarWidget extends AbstractWidget {	
	
	@Override
	protected void config(Map<String, String> params) {

	}

	@Override
	public boolean cacheAble() {
		 
		return false;
	}

	@Override
	protected void display(Map<String, String> params) {
		Member member = UserServiceFactory.getUserService().getCurrentMember();
		if (member == null) {
			//压入当前页面的URL，为了登录后能跳转回来
			//Author:Dawei
			this.putData("loginForward", StringUtil.toUTF8(ThreadContextHolder.getHttpRequest().getServletPath()));	
			//End
			this.putData2("isLogin", false);
			this.putData("isLogin", false);
		} else {
			this.putData2("isLogin", true);
			this.putData("isLogin", true);
			this.putData("member", member);
		}
		this.setPageName("member_login_bar");
	}

}
