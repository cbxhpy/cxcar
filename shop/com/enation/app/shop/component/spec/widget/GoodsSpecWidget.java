package com.enation.app.shop.component.spec.widget;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.component.goodscore.widget.goods.AbstractGoodsDetailWidget;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.model.Specification;
import com.enation.app.shop.core.service.IProductManager;

/**
 * 商品规格挂件
 * @author kingapex
 * 2010-2-4上午11:19:03
 */
@Component("goods_spec")
@Scope("prototype")
public class GoodsSpecWidget extends AbstractGoodsDetailWidget {
 
	private IProductManager productManager;
	
	@Override
	protected void config(Map<String, String> params) {
		
	}

 
	public IProductManager getProductManager() {
		return productManager;
	}

	public void setProductManager(IProductManager productManager) {
		this.productManager = productManager;
	}

	
	@Override
	protected void execute(Map<String, String> params, Map goods) {

		Integer goods_id = Integer.valueOf( goods.get("goods_id").toString() );
		List<Product> productList  = this.productManager.list(goods_id);
		
		if( (""+goods.get("have_spec")).equals("0")){
			this.putData("productid", productList.get(0).getProduct_id());
			this.putData("productList", productList);
		}else{
			List<Specification> specList = this.productManager.listSpecs(goods_id);
		
			this.putData("productList", productList);
			this.putData("specList", specList);
		}
		this.putData("have_spec", goods.get("have_spec"));
	
		
	}

}
