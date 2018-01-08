package com.enation.app.shop.component.member.widget;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.service.IMemberOrderManager;
import com.enation.eop.sdk.widget.AbstractMemberWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

/**
 * 购买商品列表挂件
 * @author kingapex
 *2012-4-1上午8:37:02
 */
@Component("member_goods")
@Scope("prototype")
public class MemberGoodsWidget extends AbstractMemberWidget {

	private IMemberOrderManager memberOrderManager;
	
	@Override
	protected void config(Map<String, String> params) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void display(Map<String, String> params) {
		this.setMenu("goods");
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String page = request.getParameter("page");
		page = (page == null || page.equals("")) ? "1" : page;
		int pageSize = 10;
		
		String keyword = request.getParameter("keyword");
		if(!StringUtil.isEmpty(keyword)){
			keyword = StringUtil.toUTF8(keyword);
		}
		
		Page goodsPage = memberOrderManager.pageGoods(
				Integer.valueOf(page), pageSize,keyword);
		Long totalCount = goodsPage.getTotalCount();

		this.putData("goodsPage",goodsPage);
		this.putData("totalCount", totalCount);
		this.putData("pageSize", pageSize);
		this.putData("page", page);
		this.putData("keyword",keyword);
	}

	public void setMemberOrderManager(IMemberOrderManager memberOrderManager) {
		this.memberOrderManager = memberOrderManager;
	}
	
}
