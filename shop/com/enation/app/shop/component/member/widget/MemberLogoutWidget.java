package com.enation.app.shop.component.member.widget;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.eop.processor.httpcache.HttpCacheManager;
import com.enation.eop.sdk.user.IUserService;
import com.enation.eop.sdk.widget.AbstractMemberWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.HttpUtil;

/**
 * 会员注销挂件
 * @author kingapex
 *2010-5-4上午11:03:58
 */
@Component("member_logout")
@Scope("prototype")
public class MemberLogoutWidget extends AbstractMemberWidget {

	
	@Override
	protected void config(Map<String, String> params) {
			
	}

	
	@Override
	protected void display(Map<String, String> params) {
		this.showMenu(false);
	   ThreadContextHolder.getSessionContext().removeAttribute(IUserService.CURRENT_MEMBER_KEY);
	   HttpUtil.addCookie(ThreadContextHolder.getHttpResponse(), "JavaShopUser","", 0);
	   HttpCacheManager.sessionChange();
	   
	   this.showSuccess("成功退出","商城首页", "index.html");
	}

}
