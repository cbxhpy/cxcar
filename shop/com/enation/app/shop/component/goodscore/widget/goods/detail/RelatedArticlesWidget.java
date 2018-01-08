package com.enation.app.shop.component.goodscore.widget.goods.detail;

import java.util.List;
import java.util.Map;

import com.enation.app.shop.component.goodscore.widget.goods.AbstractGoodsDetailWidget;
import com.enation.framework.database.IDaoSupport;

/**
 * 暂不上线
 * 相关商品挂件
 * @author kingapex
 *
 */
public class RelatedArticlesWidget extends AbstractGoodsDetailWidget {

	private IDaoSupport basedDaoSupport;
	@Override
	protected void config(Map<String, String> params) {
		 

	}
	
	@Override
	protected void execute(Map<String, String> params, Map goods) {
		String sql  ="select * from goods_articles where goodsid=?";
		List articleList  = this.baseDaoSupport.queryForList(sql, goods.get("goods_id"));
		this.putData("articleList",articleList);
	}

	public IDaoSupport getBasedDaoSupport() {
		return basedDaoSupport;
	}

	public void setBasedDaoSupport(IDaoSupport basedDaoSupport) {
		this.basedDaoSupport = basedDaoSupport;
	}

 

}
