package com.enation.app.shop.component.member.widget;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.mapper.GoodsListMapper;
import com.enation.app.shop.core.service.IFavoriteManager;
import com.enation.app.shop.core.service.IMemberCommentManager;
import com.enation.app.shop.core.service.IMemberManager;
import com.enation.app.shop.core.service.IMemberPointManger;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IWelcomeInfoManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.eop.sdk.user.IUserService;
import com.enation.eop.sdk.widget.AbstractMemberWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;

/**
 * 会员中心首页
 * 
 * @author kingapex
 * 
 */
@Component("member_index")
@Scope("prototype")
public class MemberIndexWidget extends AbstractMemberWidget {

	private IWelcomeInfoManager welcomeInfoManager;
	private IMemberCommentManager memberCommentManager;
	private IFavoriteManager favoriteManager;
	private IMemberManager memberManager;
	private IMemberPointManger memberPointManger;
	private IOrderManager orderManager;

	@Override
	protected void display(Map<String, String> params) {
		this.setPageName("index");
		Member member = this.getCurrentMember();
		member = this.memberManager.get(member.getMember_id());
		ThreadContextHolder.getSessionContext().setAttribute(IUserService.CURRENT_MEMBER_KEY, member);

		Map wel = welcomeInfoManager.mapWelcomInfo();

		this.putData("member", member);
		this.putData("wel", wel);
		// 评论消息 总数
		int discussTotal = memberCommentManager.getMemberCommentTotal(member.getMember_id(), 1);
		this.putData("discussTotal", discussTotal);

		// 咨询回复 总数
		int askTotal = memberCommentManager.getMemberCommentTotal(member.getMember_id(), 2);
		this.putData("askTotal", askTotal);

		// 收藏商品 总数
		int favoriteTotal = favoriteManager.getCount(member.getMember_id());
		this.putData("favoriteTotal", favoriteTotal);

		// 取热卖商品
		String sql = "select * from goods where disabled=0 and market_enable=1 order by buy_count asc";
		List list = this.baseDaoSupport.queryForList(sql.toString(), 1, 8, new GoodsListMapper());
		this.putData("goodsList", list);

		this.putData("freezepoint",	memberPointManger.getFreezeMpByMemberId(member.getMember_id()));
		// 未付款订单列表
		Page orderPage = orderManager.listByStatus(1, 100, 0, member.getMember_id());
		this.putData("orderPage", orderPage);
		this.putData("orderTotal", orderManager.getMemberOrderNum(member.getMember_id()));

		this.putData(OrderStatus.getOrderStatusMap());
	}

	@Override
	protected void config(Map<String, String> params) {

	}

	public IWelcomeInfoManager getWelcomeInfoManager() {
		return welcomeInfoManager;
	}

	public void setWelcomeInfoManager(IWelcomeInfoManager welcomeInfoManager) {
		this.welcomeInfoManager = welcomeInfoManager;
	}

	public void setMemberCommentManager(IMemberCommentManager memberCommentManager) {
		this.memberCommentManager = memberCommentManager;
	}

	public IFavoriteManager getFavoriteManager() {
		return favoriteManager;
	}

	public void setFavoriteManager(IFavoriteManager favoriteManager) {
		this.favoriteManager = favoriteManager;
	}

	public IMemberManager getMemberManager() {
		return memberManager;
	}

	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}

	public IMemberPointManger getMemberPointManger() {
		return memberPointManger;
	}

	public void setMemberPointManger(IMemberPointManger memberPointManger) {
		this.memberPointManger = memberPointManger;
	}

	public IOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}

	public IMemberCommentManager getMemberCommentManager() {
		return memberCommentManager;
	}

}
