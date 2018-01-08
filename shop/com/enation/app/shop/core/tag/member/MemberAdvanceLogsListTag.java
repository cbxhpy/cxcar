package com.enation.app.shop.core.tag.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.eop.sdk.user.IUserService;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 获得该会员的账户款项明细，读取es_advance_logs表
 * @author wanghongjun
 * 2015-04-15
 */

@Component
@Scope("prototype")
public class MemberAdvanceLogsListTag extends BaseFreeMarkerTag{

	private IMemberManager memberManager;
	
	@Override
	public Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();

		IUserService userService = UserServiceFactory.getUserService();
		Member member = userService.getCurrentMember();
		if(member==null){
			throw new TemplateModelException("未登陆不能使用此标签[MemberOrderListTag]");
		}
		Map result = new HashMap();
		String page = request.getParameter("page");
		page = (page == null || page.equals("")) ? "1" : page;
		int pageSize = 10;
		
		Page advanceLogs = memberManager.pageAdvanceLogs(Integer.valueOf(page), pageSize);
		
		
		Long totalCount = advanceLogs.getTotalCount();
		
		List advanceLogsList = (List) advanceLogs.getResult();
		advanceLogsList = advanceLogsList == null ? new ArrayList() : advanceLogsList;

		result.put("totalCount", totalCount);
		result.put("pageSize", pageSize);
		result.put("page", page);
		result.put("advanceLogsList", advanceLogsList);
		
		return result;
	}

	public IMemberManager getMemberManager() {
		return memberManager;
	}

	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}
	
	
	
}
