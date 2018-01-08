package com.enation.app.shop.component.member.widget;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.eop.sdk.widget.AbstractWidget;

/**
 * 判断会员是否登陆的挂件
 * @author kingapex
 *
 */
@Component("member_islogin")
@Scope("prototype")
public class MemberIsLoginWidget extends AbstractWidget {

	@Override
	public boolean cacheAble() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void config(Map<String, String> params) {
		
	}

	@Override
	protected void display(Map<String, String> params) {
		Member member = UserServiceFactory.getUserService().getCurrentMember();
		if(member==null){
			this.putData2("isLogin", false);
			this.putData("isLogin", false);
		}else{
			this.putData2("isLogin", true);
			this.putData("isLogin", true);
		}
	}

}
