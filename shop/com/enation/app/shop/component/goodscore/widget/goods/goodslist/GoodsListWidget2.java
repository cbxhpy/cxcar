package com.enation.app.shop.component.goodscore.widget.goods.goodslist;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Cat;
import com.enation.app.shop.core.model.mapper.GoodsListMapper;
import com.enation.app.shop.core.service.IGoodsCatManager;
import com.enation.app.shop.core.utils.UrlUtils;
import com.enation.eop.sdk.widget.AbstractWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.StringUtil;
/**
 * 商品列表挂件2
 * @author kingapex
 *2012-3-31下午9:08:50
 */
@Component("goodslist2")
@Scope("prototype")
public class GoodsListWidget2 extends AbstractWidget {
	private IGoodsCatManager goodsCatManager;
	@Override
	protected void config(Map<String, String> params) {
		 
	}

	@Override
	protected void display(Map<String, String> params) {
		
		String catid = params.get("catid");
		String tagid = params.get("tagid");
		String goodsnum = params.get("goodsnum");
		
		if(catid == null || catid.equals("")){
			String uri  = ThreadContextHolder.getHttpRequest().getServletPath();
			catid = UrlUtils.getParamStringValue(uri,"cat");
		}
		
 
		
		
		List goodsList  = this.listGoods(catid, tagid, goodsnum);
		this.putData("goodsList",goodsList);
		
	}
	
	
	private List listGoods(String catid,String tagid,String goodsnum){
		
	
		int num = 10;
		if(!StringUtil.isEmpty(goodsnum)){
			num = Integer.valueOf(goodsnum);
		}
		
		StringBuffer sql = new StringBuffer();
		sql.append("select g.* from " + this.getTableName("tag_rel") + " r LEFT JOIN " + this.getTableName("goods") + " g ON g.goods_id=r.rel_id where g.disabled=0 and g.market_enable=1");
		
		if(! StringUtil.isEmpty(catid) ){
			Cat cat  = this.goodsCatManager.getById(Integer.valueOf(catid));
			String cat_path  = cat.getCat_path();
			if (cat_path != null) {
				sql.append( " and  g.cat_id in(" ) ;
				sql.append("select c.cat_id from " + this.getTableName("goods_cat") + " ");
				sql.append(" c where c.cat_path like '" + cat_path + "%')");
			}
		}
		
		if(!StringUtil.isEmpty(tagid)){
			sql.append(" AND r.tag_id="+tagid+"");
		}
		
		sql.append(" order by r.ordernum desc");
	//	//System.out.println(sql);
		List list = this.daoSupport.queryForList(sql.toString(), 1,num, new GoodsListMapper());
	
		return list;
	}

	public IGoodsCatManager getGoodsCatManager() {
		return goodsCatManager;
	}

	public void setGoodsCatManager(IGoodsCatManager goodsCatManager) {
		this.goodsCatManager = goodsCatManager;
	}

}
