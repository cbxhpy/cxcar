package com.enation.app.shop.component.bonus.tag;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.component.bonus.service.impl.BonusManager;
import com.enation.eop.sdk.user.IUserService;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;
@Component
@Scope("prototype")
public class MyCouponTag extends BaseFreeMarkerTag {
	private BonusManager bonusManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		IUserService userService = UserServiceFactory.getUserService();//获取当前登录会员
		Member member = userService.getCurrentMember();
		HttpServletRequest request =  ThreadContextHolder.getHttpRequest();
		int pagenum=StringUtil.toInt(request.getParameter("page"),1);
		 Page pages=bonusManager.pageList(pagenum,10, member.getMember_id());
		return pages;
	}
	public BonusManager getBonusManager() {
		return bonusManager;
	}
	public void setBonusManager(BonusManager bonusManager) {
		this.bonusManager = bonusManager;
	}
	
    
}
