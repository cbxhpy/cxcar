package cn.net.hzy.app.shop.component.fenxiao.plugin;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.net.hzy.app.shop.component.fenxiao.service.IFenXiaoManager;
import cn.net.hzy.app.shop.component.fenxiao.service.MemberPointSession;

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

@Component
public class BonusFenXiaoPlugin extends AutoRegisterPlugin implements ICountPriceEvent, IAfterOrderCreateEvent,IOrderCanelEvent{

	@Autowired
	private IFenXiaoManager fenxiaoManager;
	@Autowired
	private IOrderMetaManager orderMetaManager;
	private final String hongbao_discount_key = "hongbaodiscount";
	private final String yongjin_discount_ky = "yongjindiscount";

	@Override
	public void canel(Order order) {
		fenxiaoManager.returnedYongjinMp(order);
	}

	@Override
	public void onAfterOrderCreate(Order order, List<CartItem> itemList,
			String sessionid) {
		
		//读取消费红包(转换成分单位)、佣金积分抵扣信息
		Double yongjin = MemberPointSession.getYongjin();
		Double mp = MemberPointSession.getPoint()*100;
		
		if(yongjin!=null && yongjin>0.0){
			fenxiaoManager.useYongjin(yongjin, order);
		}
		if(mp!=null && mp>0.0){
			fenxiaoManager.useMp(mp.intValue(), order);
		}		
		
		OrderPrice orderPrice  = order.getOrderprice();
		Map disItems = orderPrice.getDiscountItem();
		
		Double yongjindiscount =(Double) disItems.get(yongjin_discount_ky);
		
		
		
		if(yongjindiscount!=null && yongjindiscount>0.0){
			OrderMeta orderMeta = new OrderMeta();
			orderMeta.setOrderid(order.getOrder_id());
			orderMeta.setMeta_key(yongjin_discount_ky);
			orderMeta.setMeta_value( String.valueOf( yongjindiscount) );
			this.orderMetaManager.add(orderMeta);
			
		}
		
		Double hongbaodiscount = (Double) disItems.get(hongbao_discount_key);
		if(hongbaodiscount!=null && hongbaodiscount>0.0){
			OrderMeta orderMeta = new OrderMeta();
			orderMeta.setOrderid(order.getOrder_id());
			orderMeta.setMeta_key(hongbao_discount_key);
			orderMeta.setMeta_value( String.valueOf( hongbaodiscount) );
			this.orderMetaManager.add(orderMeta);
		}
		
		//消费红包、佣金积分抵扣清空
		MemberPointSession.cleanAll();
		
	}

	@Override
	public OrderPrice countPrice(OrderPrice orderprice) {
		//订单优惠项
		Map<String,Object> disItems  = orderprice.getDiscountItem();
		//佣金、消费红包优惠
		double moneyMp = MemberPointSession.getPoint();
		disItems.put(hongbao_discount_key, moneyMp);
		double moneyYj = MemberPointSession.getYongjin();
		disItems.put(yongjin_discount_ky, moneyYj);
		
		orderprice.setDiscountItem(disItems);
		//处理订单金额
		double moneyCount = MemberPointSession.getUseMoney();
		double needPay =orderprice.getNeedPayMoney();
		needPay= CurrencyUtil.sub(needPay, moneyCount);
		
		orderprice.setDiscountPrice(CurrencyUtil.add(orderprice.getDiscountPrice(),moneyCount));
		if(needPay<0){
			orderprice.setNeedPayMoney(0.0);
		}else{
			orderprice.setNeedPayMoney(needPay);
		}
		
		return orderprice;
	}
	
	

}
