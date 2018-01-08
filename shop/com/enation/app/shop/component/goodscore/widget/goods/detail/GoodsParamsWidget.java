package com.enation.app.shop.component.goodscore.widget.goods.detail;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.component.goodscore.widget.goods.AbstractGoodsDetailWidget;
import com.enation.app.shop.core.model.support.ParamGroup;
import com.enation.app.shop.core.service.GoodsTypeUtil;

/**
 * 商品参数挂件<br/>
 * 读取商品的参数供页面显示<br/>
 * 并设置hasParam变量以在页面中判断是否显示"详细参数"选择卡
 * @author kingapex
 *
 */
@Component("goods_params")
@Scope("prototype")
public class GoodsParamsWidget extends AbstractGoodsDetailWidget {
 
	
	@Override
	protected void config(Map<String, String> params) {

	}

	
	@Override
	protected void execute(Map<String, String> params,Map goods) {
 
		String goodParams  =(String)goods.get("params");
		if(goodParams!=null && !goodParams.equals("")){
			ParamGroup[] paramList =GoodsTypeUtil.converFormString(goodParams);
			this.putData("paramList", paramList);
			
			if(paramList!=null && paramList.length>0)
			this.putData("hasParam", true);
			else
				this.putData("hasParam", false);
		}else{
			this.putData("hasParam", false);
		}

		this.setPageName("params");
	}

 
	

}
