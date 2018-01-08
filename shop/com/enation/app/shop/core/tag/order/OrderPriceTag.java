package com.enation.app.shop.core.tag.order;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.MemberAddress;
import com.enation.app.shop.core.model.support.OrderPrice;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.app.shop.core.service.IMemberAddressManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;

/**
 * 订单价格tag
 * @author kingapex
 *2013-7-26下午1:27:28
 */
@Component
@Scope("prototype")
public class OrderPriceTag extends BaseFreeMarkerTag {
	private ICartManager cartManager;
	private IMemberAddressManager memberAddressManager ;
	
	/**
	 * 订单价格标签
	 * @param address_id:收货地址id，int型
	 * @param shipping_id:配送方式id，int型
	 * @return 订单价格,OrderPrice型
	 * {@link OrderPrice}
	 */
	@Override
	public Object exec(Map args) throws TemplateModelException {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String sessionid = request.getSession().getId();
		
		Integer addressid = (Integer)args.get("address_id");
		Integer shipping_id = (Integer)args.get("shipping_id"); 
		String regionid = null;

		String cart_ids = (String)args.get("cart_ids");
		
		//如果传递了地址，已经选完配送方式了
		if(addressid != null){
			MemberAddress address = memberAddressManager.getAddress(addressid);
			regionid = ""+address.getRegion_id();
		}
		
		//OrderPrice orderprice = this.cartManager.countPrice(cartManager.listGoods(sessionid), shipping_id, regionid);
		if(StringUtil.isEmpty(cart_ids)){
			cart_ids = null;
		}
		OrderPrice orderprice = this.cartManager.appCountPrice(cart_ids, null, null, null);
		
		return orderprice;
	}

	public ICartManager getCartManager() {
		return cartManager;
	}

	public void setCartManager(ICartManager cartManager) {
		this.cartManager = cartManager;
	}

	public IMemberAddressManager getMemberAddressManager() {
		return memberAddressManager;
	}

	public void setMemberAddressManager(IMemberAddressManager memberAddressManager) {
		this.memberAddressManager = memberAddressManager;
	}

}
