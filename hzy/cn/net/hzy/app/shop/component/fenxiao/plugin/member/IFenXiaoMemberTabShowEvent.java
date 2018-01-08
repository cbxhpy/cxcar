package cn.net.hzy.app.shop.component.fenxiao.plugin.member;

import com.enation.app.base.core.model.Member;

/**
 * 会员分销后台tab页显示事件
 * @author radioend
 * 2015/09/29 16:56:00
 */
public interface IFenXiaoMemberTabShowEvent {

	/**
	 * 返回选项卡的名称
	 * @return
	 */
	public String getTabName(Member member);
	
	
	/**
	 * 返回排序
	 * @return
	 */
	public int getOrder();
	
	
	/**
	 * 设定此事件是否响应
	 * @param order
	 * @return
	 */
	public boolean canBeExecute(Member member);
	
	

	/**
	 * 返回要在会员详细页面显示的HTML
	 * @param member 当前会员
	 * @return
	 */
	public String onShowMemberDetailHtml(Member member);
}
