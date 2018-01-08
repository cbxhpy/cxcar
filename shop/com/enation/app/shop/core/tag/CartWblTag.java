package com.enation.app.shop.core.tag;

import java.util.ArrayList;
import java.util.List;
import net.sf.json.JSONObject;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.app.shop.core.utils.StringUtil;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

/**
 * 购物车标签
 * @author yexf
 * 2016-11-8
 */
@Component
@Scope("prototype") 
public class CartWblTag implements TemplateMethodModel {
	
	private ICartManager cartManager;
	
	/**
	 * 返回购物车中的购物列表
	 * @param 
	 * @return 购物列表 类型List<CartItem>
	 * {@link CartItem}
	 */
	@Override
	public Object exec(List args) throws TemplateModelException {
		 
		
		//cartManager = SpringContextHolder.getBean("cartManager");
		//String sessionid = request.getSession().getId();
		//List goodsList = cartManager.listGoods(sessionid); //商品列表
		//String keyword = (String) params.get("keyword");
		
		List goodsList = new ArrayList();
		if(args.size() != 0){
			String args_str = args.get(0).toString();
			JSONObject json = JSONObject.fromObject(args_str);
			goodsList = this.cartManager.listCartByMemberIdPrice(StringUtil.isNull(json.get("member_id")), StringUtil.isNull(json.get("cart_ids")));
		}
		
		return goodsList;
	}

	public ICartManager getCartManager() {
		return cartManager;
	}

	public void setCartManager(ICartManager cartManager) {
		this.cartManager = cartManager;
	}
	

}
