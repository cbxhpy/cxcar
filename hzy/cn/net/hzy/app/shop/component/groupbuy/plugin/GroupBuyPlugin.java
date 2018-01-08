package cn.net.hzy.app.shop.component.groupbuy.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.net.hzy.app.shop.component.groupbuy.model.GroupBuy;
import cn.net.hzy.app.shop.component.groupbuy.service.IGroupBuyManager;

import com.enation.app.base.core.plugin.job.IEveryDayExecuteEvent;
import com.enation.app.shop.core.model.Cart;
import com.enation.app.shop.core.plugin.cart.ICartAddEvent;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

/**
 * @ClassName: GroupBuyPlugin
 * @Description: 团购插件
 * @author radioend
 * @date 2015年7月17日 上午11:21:18
 */
@Component
public class GroupBuyPlugin extends AutoRegisterPlugin implements ICartAddEvent,IEveryDayExecuteEvent {

	private IDaoSupport daoSupport;
	
	@Autowired
	private IGroupBuyManager groupBuyManager;

	@Override
	public void add(Cart cart) {
		
		GroupBuy groupBuy = groupBuyManager.getBuyGoodsId(cart.getGoods_id());
		
		if(groupBuy!=null){
			cart.setPrice(groupBuy.getGroup_price());
			String addon = cart.getAddon();
			if(!StringUtil.isEmpty(addon)){
				JSONArray specArray=	JSONArray.fromObject(addon);
				List<Map> list = (List) JSONArray.toCollection(specArray,Map.class);
				Map<String,String> groupMap = new HashMap<String, String>();
				groupMap.put("name", "促销");
				groupMap.put("value", "团购");
				list.add(groupMap);
				
				String specstr =JSONArray.fromObject(list).toString();
				cart.setAddon(specstr);
				
			}else{
				List<Map> list = new ArrayList<Map>();
				Map<String,String> groupMap = new HashMap<String, String>();
				groupMap.put("name", "促销");
				groupMap.put("value", "团购");
				list.add(groupMap);
				
				String specstr =JSONArray.fromObject(list).toString();
				cart.setAddon(specstr);
			}
		}
		
	}

	/**
	 * 
	 * 响应购物车添加后事件
	 * 如果有规格，读取这个货品的规格List
	 * 然后形成Json串，增加促销信息存在cart表的附加字段中
	 */
	@Override
	public void afterAdd(Cart cart) {
		
	}

	@Override
	public void appAdd(Cart cart, String havespec) {
		
	}
	
	@Override
	public void everyDay() {
		//关闭团购
		String sql="UPDATE es_group_buy SET group_stat=0  WHERE group_stat=1 AND end_time<?";
		this.daoSupport.execute(sql, DateUtil.getDateline());
	}

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}

}
