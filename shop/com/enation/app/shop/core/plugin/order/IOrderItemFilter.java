package com.enation.app.shop.core.plugin.order;

import java.util.List;
import com.enation.app.shop.core.model.OrderItem;
/**
 * 订单项过滤器
 * @author kingapex
 *
 */
public interface IOrderItemFilter {
	public void filter(Integer orderid,List<OrderItem> itemList);
}
