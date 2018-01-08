package com.enation.app.shop.component.virtualcat.plugin;

import java.util.List;

import org.springframework.stereotype.Component;

import com.enation.app.shop.component.virtualcat.model.VirtualCat;
import com.enation.app.shop.component.virtualcat.service.IVirtualCatManager;
import com.enation.app.shop.core.model.Cat;
import com.enation.app.shop.core.plugin.search.IGoodsSearchFilter;
import com.enation.app.shop.core.plugin.search.SearchSelector;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.StringUtil;

/**
 * 自定义分类搜索过滤
 * @author lzf
 * 2012-10-27下午12:00:09
 * ver 1.0
 */
@Component
public class VirtualCatSearchFilter extends AutoRegisterPlugin implements
		IGoodsSearchFilter {

	private IVirtualCatManager virtualCatManager;
	
	@Override
	public List<SearchSelector> createSelectorList(Cat cat, String url,
			String urlFragment) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private String getCondition(VirtualCat vcat){
		String condition = "";
		List<VirtualCat> children = this.virtualCatManager.listChildren(vcat.getCid());
		for(VirtualCat ccat:children){
			condition += " or seller_cids like '%," + ccat.getCid()+",%'";
			condition += this.getCondition(ccat);
		}
		return condition;
	}

	@Override
	public void filter(StringBuffer sql, Cat cat, String urlFragment) {
		if (!StringUtil.isEmpty(urlFragment) && !"0".equals(urlFragment)) {
			VirtualCat vcat = this.virtualCatManager.get(Integer.valueOf(urlFragment));
			String condition = "(seller_cids like '%," + vcat.getCid()+",%'";
			condition += this.getCondition(vcat);
			condition += ")";
			sql.append(" and " + condition);
		}

	}

	@Override
	public String getFilterId() {
		return "virtual";
	}

	public IVirtualCatManager getVirtualCatManager() {
		return virtualCatManager;
	}

	public void setVirtualCatManager(IVirtualCatManager virtualCatManager) {
		this.virtualCatManager = virtualCatManager;
	}

}
