package com.enation.app.shop.component.member.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.service.IMemberCommentManager;
import com.enation.app.shop.core.service.IMemberOrderItemManager;
import com.enation.eop.sdk.user.IUserService;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.eop.sdk.widget.AbstractMemberWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

/**
 * 会员中心评论挂件
 * 
 * @author kingapex
 * 
 */

@Component("member_comments")
@Scope("prototype")
public class MemberCommentsWidget extends AbstractMemberWidget {

	private IMemberCommentManager memberCommentManager;
	
	private IMemberOrderItemManager memberOrderItemManager;

	@Override
	protected void display(Map<String, String> params) {
		this.setMenu("comments");
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String page = request.getParameter("page");
		page = (page == null || page.equals("")) ? "1" : page;
		this.setPageName("mycomments");
		IUserService userService = UserServiceFactory.getUserService();
		Member member = userService.getCurrentMember();
		int pageSize = 5;
		
		if(StringUtil.isEmpty(action)){
			Page commentsPage = memberCommentManager.getMemberComments(Integer.valueOf(page), pageSize,1,member.getMember_id());
			Long totalCount = commentsPage.getTotalCount();
	
			List commentsList = (List) commentsPage.getResult();
			commentsList = commentsList == null ? new ArrayList() : commentsList;
	
			this.putData("totalCount", totalCount);
			this.putData("pageSize", pageSize);
			this.putData("page", page);
			this.putData("commentsList", commentsList);
			
		}else if("goods".equals(action)){			
			pageSize = 10;
			Page goodsPage = memberOrderItemManager.getGoodsList(member.getMember_id(), 0, Integer.valueOf(page), pageSize);
			this.putData("goodsPage", goodsPage);
			this.putData("totalCount", goodsPage.getTotalCount());
			this.putData("pageSize", pageSize);
			this.putData("page", page);
			
			this.setActionPageName("comments_goods");
		}
	}

	@Override
	protected void config(Map<String, String> params) {

	}

	public void setMemberOrderItemManager(
			IMemberOrderItemManager memberOrderItemManager) {
		this.memberOrderItemManager = memberOrderItemManager;
	}

	public void setMemberCommentManager(IMemberCommentManager memberCommentManager) {
		this.memberCommentManager = memberCommentManager;
	}

}
