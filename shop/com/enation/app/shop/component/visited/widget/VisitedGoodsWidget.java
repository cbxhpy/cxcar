package com.enation.app.shop.component.visited.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.eop.sdk.widget.AbstractWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.context.webcontext.WebSessionContext;
import com.enation.framework.directive.ImageUrlDirectiveModel;

/**
 * 浏览过的商品
 * @author lzf
 * 2012-4-17上午10:09:27
 */
@Component("visited_goods")
@Scope("prototype")
public class VisitedGoodsWidget extends AbstractWidget {

	@Override
	protected void display(Map<String, String> params) {
		WebSessionContext sessionContext = ThreadContextHolder.getSessionContext();
		List<Map> visitedGoods = (List<Map>)sessionContext.getAttribute("visitedGoods");
		if(visitedGoods==null) visitedGoods = new ArrayList<Map>();
		this.putData("visitedGoods", visitedGoods);
		this.putData("GoodsPic",new  ImageUrlDirectiveModel());
		this.setPageName("visited");

	}

	@Override
	protected void config(Map<String, String> params) {
		// TODO Auto-generated method stub

	}

}
