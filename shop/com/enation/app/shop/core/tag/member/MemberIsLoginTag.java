package com.enation.app.shop.core.tag.member;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
/**
 * 判断会员是否登陆的标签
 * @author lina
 *
 */
@Component
@Scope("prototype")
public class MemberIsLoginTag extends BaseFreeMarkerTag {
	/**
	 * @param 无
	 * @return  isLogin boolean型 
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Member member = UserServiceFactory.getUserService().getCurrentMember();
		boolean isLogin = false;
		if(member!=null){
			isLogin = true;
		}
		return isLogin;
	}

}
