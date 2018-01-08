package com.enation.app.shop.core.tag.member;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.RequestUtil;

import freemarker.template.TemplateModelException;

@Component
@Scope("prototype")
public class MemberLoginCheckTag extends BaseFreeMarkerTag {
private IMemberManager memberManager;
	
	/**
	 * 会员详细标签
	 * @param 无
	 * @return 返回当前会员,Member型
	 * 如果未登陆，返回null
	 * {@link Member}
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		String loginUrl = (String) params.get("login_url");
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String curr_url = RequestUtil.getRequestUrl(request);
		
		String ctx = this.getRequest().getContextPath();
		if("/".equals(ctx)){
			ctx="";
		}
		loginUrl = ctx+"/login.html?forward=" + curr_url;
		
		Member member = UserServiceFactory.getUserService().getCurrentMember();
		if (member == null) {
			HttpServletResponse response = ThreadContextHolder.getHttpResponse();
			try {
				response.sendRedirect(loginUrl);
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return member;
		}
	}
	public IMemberManager getMemberManager() {
		return memberManager;
	}
	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}
}
