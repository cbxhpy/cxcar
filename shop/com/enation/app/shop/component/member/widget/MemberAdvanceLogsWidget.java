package com.enation.app.shop.component.member.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.service.IAdvanceLogsManager;
import com.enation.eop.sdk.user.IUserService;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.eop.sdk.widget.AbstractMemberWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
 
/**
 * 暂不上线 
 * @author kingapex
 *2012-4-1上午7:48:05
 */

//@Component("member_advanceLogs")
//@Scope("prototype")
public class MemberAdvanceLogsWidget extends AbstractMemberWidget {
	
	private IAdvanceLogsManager advanceLogsManager;

	
	@Override
	protected void config(Map<String, String> params) {

	}

	
	@Override
	protected void display(Map<String, String> params) {
		this.setPageName("member_advanceLogs");
		HttpServletRequest request =  ThreadContextHolder.getHttpRequest();
		 String page  = request.getParameter("page");
		 page = (page == null || page.equals("")) ? "1" : page;
		 int pageSize = 20;
		 Page advanceLogsPage = advanceLogsManager.pageAdvanceLogs(Integer.valueOf(page), pageSize);
		 Long pageCount = advanceLogsPage.getTotalPageCount();
		 List advanceLogsList = (List)advanceLogsPage.getResult();
			advanceLogsList = advanceLogsList == null ? new ArrayList() : advanceLogsList;
			IUserService userService = UserServiceFactory.getUserService();
			Member member = userService.getCurrentMember();
			this.putData("advanceLogsList", advanceLogsList);
			this.putData("member_advance", member.getAdvance());
			this.putData("pageSize", pageSize);
			this.putData("pageCount", pageCount);
			this.putData("page", page);
			this.putData("totalCount",advanceLogsPage.getTotalCount());
	}

	public IAdvanceLogsManager getAdvanceLogsManager() {
		return advanceLogsManager;
	}

	public void setAdvanceLogsManager(IAdvanceLogsManager advanceLogsManager) {
		this.advanceLogsManager = advanceLogsManager;
	}

}
