package com.enation.app.shop.component.widget.cartbar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.eop.sdk.user.UserServiceFactory;
import com.enation.eop.sdk.widget.AbstractWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.directive.ImageUrlDirectiveModel;
import com.enation.framework.util.JsonMessageUtil;

/**
 * 会员信息面板挂件
 * @author kingapex
 *2010-5-4上午10:31:20
 */
@Component("cart_bar")
@Scope("prototype")
public class CartBarWidget extends AbstractWidget {

	private ICartManager cartManager;
	
	@Override
	protected void config(Map<String, String> params) {

	}

	
	@Override
	protected void display(Map<String, String> params) {
		
		Member member = UserServiceFactory.getUserService().getCurrentMember();
		if(member==null){
			this.putData("isLogin", false);
		}else{
			this.putData("isLogin", true);
		}
		
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		String sessionid =request.getSession().getId();
		
		 
		
		if(  "showList".equals(action)){
			List goodsList  = cartManager.listGoods( sessionid );
			this.putData("goodsList", goodsList);			 
			this.putData("GoodsPic",new  ImageUrlDirectiveModel());
			this.setActionPageName("show_list");
			
		}

		if("showCount".equals(action)){
			Double goodsTotal  =cartManager.countGoodsTotal( sessionid );
			this.putData("total", goodsTotal);
			int count = this.cartManager.countItemNum(sessionid);
			this.putData("count", count);
			Map data = new HashMap(2);
			data.put("total", goodsTotal);
			data.put("count",count);
			String json = JsonMessageUtil.getObjectJson(data);
			this.showJson(json);
			
		//	JsonMessageUtil.getNumberJson("count", count);
		}
		
		
	}

	public ICartManager getCartManager() {
		return cartManager;
	}

	public void setCartManager(ICartManager cartManager) {
		this.cartManager = cartManager;
	}
	
	

}
