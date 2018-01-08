package com.enation.app.shop.component.bonus.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.component.bonus.model.MemberBonus;
import com.enation.app.shop.component.bonus.service.BonusSession;
import com.enation.app.shop.component.bonus.service.IBonusManager;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.OrderMeta;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.model.support.OrderPrice;
import com.enation.app.shop.core.plugin.cart.ICountPriceEvent;
import com.enation.app.shop.core.plugin.order.IAfterOrderCreateEvent;
import com.enation.app.shop.core.plugin.order.IOrderCanelEvent;
import com.enation.app.shop.core.service.IOrderMetaManager;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.CurrencyUtil;

/**
 * 红包订单插件
 * 
 * @author kingapex
 *2013-9-20上午10:44:21
 */
@Component
@Scope("prototype")
public class BonusOrderDiscountPlugin extends AutoRegisterPlugin  implements ICountPriceEvent, IAfterOrderCreateEvent,IOrderCanelEvent {
	private IOrderMetaManager orderMetaManager;
	private  final String discount_key ="bonusdiscount";
	private IBonusManager bonusManager;
	
	
	/**
	 * 记录使用的红包
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void onAfterOrderCreate(Order order, List<CartItem> arg1, String arg2) {
		//读取列表式的
		List<MemberBonus> bonusList =BonusSession.get();
		if(bonusList==null || bonusList.isEmpty()){
			bonusList=new ArrayList<MemberBonus>();	
		}
		
		//读取单个的
		MemberBonus bonus = BonusSession.getOne();
		if(bonus!=null){
			bonusList.add(bonus);
		}
		
		for (MemberBonus memberBonus : bonusList) {
			int bonusid =memberBonus.getBonus_id();
			int bonusTypeid= memberBonus.getBonus_type_id();
			bonusManager.use(bonusid, order.getMember_id(), order.getOrder_id(),order.getSn(),bonusTypeid);
		}
		
		OrderPrice orderPrice  = order.getOrderprice();
		Map disItems = orderPrice.getDiscountItem();
		
		Double bonusdiscount =(Double) disItems.get(discount_key);
		
		if(bonusdiscount!=null && bonusdiscount>0.0){
			OrderMeta orderMeta = new OrderMeta();
			orderMeta.setOrderid(order.getOrder_id());
			orderMeta.setMeta_key(discount_key);
			orderMeta.setMeta_value( String.valueOf( bonusdiscount) );
			this.orderMetaManager.add(orderMeta);
			
		}
		 
		BonusSession.cleanAll();
	}

	@Override
	public OrderPrice countPrice(OrderPrice orderprice) {
		//订单优惠项
		Map<String,Object> disItems  = orderprice.getDiscountItem();
		double moneyCount = BonusSession.getUseMoney();
 
		disItems.put(discount_key, moneyCount);//记录红包优惠金额
		
		orderprice.setDiscountItem(disItems);
		
		//处理订单金额
		double needPay =orderprice.getNeedPayMoney();
		needPay= CurrencyUtil.sub(needPay, moneyCount);
		
		//orderprice.setGoodsPrice(CurrencyUtil.sub(orderprice.getGoodsPrice(),moneyCount)); //商品应付
		//System.out.println(orderprice.getDiscountPrice()+"-----"+moneyCount+"---相加="+CurrencyUtil.add(orderprice.getDiscountPrice(),moneyCount));
		orderprice.setDiscountPrice(CurrencyUtil.add(orderprice.getDiscountPrice(),moneyCount));//优惠总金额

		if(needPay<0){
			orderprice.setNeedPayMoney(0.0);
		}else{
			orderprice.setNeedPayMoney(needPay);
		}
		
		return orderprice;
	}
	
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void canel(Order order) {
		//退回红包
		this.bonusManager.returned(order.getOrder_id());
	}

	public IOrderMetaManager getOrderMetaManager() {
		return orderMetaManager;
	}

	public void setOrderMetaManager(IOrderMetaManager orderMetaManager) {
		this.orderMetaManager = orderMetaManager;
	}

	public IBonusManager getBonusManager() {
		return bonusManager;
	}

	public void setBonusManager(IBonusManager bonusManager) {
		this.bonusManager = bonusManager;
	}

	

	

}
