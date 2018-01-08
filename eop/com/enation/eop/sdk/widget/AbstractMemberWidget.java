package com.enation.eop.sdk.widget;

import com.enation.app.base.core.model.Member;
import com.enation.eop.sdk.user.UserServiceFactory;

/**
 * 会员中心挂件基类
 * @author kingapex
 *2010-4-29下午02:13:09
 */
public abstract class AbstractMemberWidget extends AbstractWidget {
	
	
	/**
	 *会员中心挂件全部不缓存
	 */
	@Override
	public boolean  cacheAble(){
		return false;
	}
	

	private boolean showMenu= true; 
	

	
	protected void showMenu(boolean show){
		showMenu = show;
	}
	
	protected void setMenu(String menuName){
		this.putData("menu",menuName);
	}
	
	public boolean getShowMenu(){
		return this.showMenu;
	}
	
	protected Member getCurrentMember(){
		Member member = UserServiceFactory.getUserService().getCurrentMember();
		return member;
	}
}
