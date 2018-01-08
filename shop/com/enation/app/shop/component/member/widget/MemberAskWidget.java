package com.enation.app.shop.component.member.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.service.IMemberCommentManager;
import com.enation.eop.sdk.user.IUserService;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.eop.sdk.widget.AbstractMemberWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;

/**
 * 我的咨询挂件
 * @author kingapex
 *2012-4-1上午7:48:21
 */

@Component("member_ask")
@Scope("prototype")
public class MemberAskWidget extends AbstractMemberWidget {

	private IMemberCommentManager memberCommentManager;
	
	@Override
	protected void display(Map<String, String> params) {
		this.setMenu("ask");
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String page = request.getParameter("page");
		
		page = (page == null || page.equals("")) ? "1" : page;
		this.setPageName("myask");
		int pageSize = 5;
		IUserService userService = UserServiceFactory.getUserService();
		Member member = userService.getCurrentMember();
		Page commentsPage = memberCommentManager.getMemberComments(Integer.valueOf(page), pageSize,2,member.getMember_id());
		Long totalCount = commentsPage.getTotalCount();

		List commentsList = (List) commentsPage.getResult();
		commentsList = commentsList == null ? new ArrayList() : commentsList;

		this.putData("totalCount", totalCount);
		this.putData("pageSize", pageSize);
		this.putData("page", page);
		this.putData("commentsList", commentsList);
	}

	@Override
	protected void config(Map<String, String> params) {

	}

	public void setMemberCommentManager(IMemberCommentManager memberCommentManager) {
		this.memberCommentManager = memberCommentManager;
	}
	
}
