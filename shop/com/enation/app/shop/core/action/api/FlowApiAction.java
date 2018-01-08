/**
 *  版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 *  本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 *  描述：流量ApiAction
 *  修改人：Sylow
 *  修改时间：2015-10-05
 *  修改内容：制定初版
 */
package com.enation.app.shop.core.action.api;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.stereotype.Component;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.statistics.IFlowStatisticsManager;
import com.enation.framework.action.WWAction;

/**
 * 流量api Action
 * @author Sylow
 * @version v1.0,2015-10-05
 * @since v4.0
 */
@Component
@ParentPackage("shop_default")
@Namespace("/api/shop")
@Action("flow")
public class FlowApiAction extends WWAction {

	/**
	 * serialVersionUID 自动生成
	 */
	private static final long serialVersionUID = 1481528992683399602L;
	
	private IFlowStatisticsManager flowStatisticsManager;
	private IGoodsManager goodsManager;
	
	/**
	 * 商品id
	 */
	private String goods_id;
	
	/**
	 * 记录一条访问记录
	 * @param goods_id 商品id 必填
	 * @return 
	 */
	public String add(){
		try { 
			
			//如果id不为空 
			if (goods_id != null && !"".equals(goods_id)) {
				boolean isNumeric = isNumeric(goods_id);
				
				// 如果id是数字
				if (isNumeric) {
					boolean isHave = goodsManager.isHave(Integer.parseInt(goods_id));
					
					// 如果存在该商品
					if (isHave) {
						flowStatisticsManager.addFlowLog(Integer.parseInt(goods_id));
						this.showSuccessJson("记录成功");
					} else {
						this.showErrorJson("不存在该商品");
					}
				} else {
					this.showErrorJson("商品id格式错误");
				}
			} else {
				this.showErrorJson("商品id不能为空");
			}
			
		} catch(RuntimeException e) {
			e.printStackTrace();
			this.logger.error("记录访问记录出错",e);
			this.showErrorJson("记录访问记录出错["+e.getMessage()+"]");
		}
		return WWAction.JSON_MESSAGE;
	}
	
	/**
	 * 判断一个字符串是不是数字
	 * @param str 要判断的字符串
	 * @return 是或否
	 */
	private boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	public String getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(String goods_id) {
		this.goods_id = goods_id;
	}

	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}

	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}

	public IFlowStatisticsManager getFlowStatisticsManager() {
		return flowStatisticsManager;
	}

	public void setFlowStatisticsManager(
			IFlowStatisticsManager flowStatisticsManager) {
		this.flowStatisticsManager = flowStatisticsManager;
	}
	
}
