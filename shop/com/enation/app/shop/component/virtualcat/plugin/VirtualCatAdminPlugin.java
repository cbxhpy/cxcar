package com.enation.app.shop.component.virtualcat.plugin;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.shop.component.virtualcat.model.VirtualCat;
import com.enation.app.shop.component.virtualcat.service.IVirtualCatManager;
import com.enation.app.shop.core.plugin.goods.AbstractGoodsPlugin;
import com.enation.app.shop.core.plugin.goods.IGoodsTabShowEvent;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.util.StringUtil;

@Component
public class VirtualCatAdminPlugin extends AbstractGoodsPlugin implements
		IGoodsTabShowEvent {
	
	private IVirtualCatManager virtualCatManager;

	@Override
	public String getAddHtml(HttpServletRequest request) {
		FreeMarkerPaser freeMarkerPaser =FreeMarkerPaser.getInstance();
		freeMarkerPaser.setPageName("virtualcat");	
		List<VirtualCat> vcatList = this.virtualCatManager.getTree();
		freeMarkerPaser.putData("vcatList", vcatList );
		freeMarkerPaser.putData("seller_cids", null );	
		String html = freeMarkerPaser.proessPageContent();
		return html;
	}

	@Override
	public void onBeforeGoodsAdd(Map goods, HttpServletRequest request) {
		String[] arr_seller_cids = request.getParameterValues("seller_cids");
		String seller_cids = StringUtil.arrayToString(arr_seller_cids, ",");
		goods.put("seller_cids", "," + seller_cids + ",");

	}

	@Override
	public String getEditHtml(Map goods, HttpServletRequest request) {
		FreeMarkerPaser freeMarkerPaser =FreeMarkerPaser.getInstance();
		freeMarkerPaser.setPageName("virtualcat");	
		List<VirtualCat> vcatList = this.virtualCatManager.getTree();
		freeMarkerPaser.putData("vcatList", vcatList );
			
		String seller_cids = (String)goods.get("seller_cids");
		if(!StringUtil.isEmpty(seller_cids)){
			seller_cids = seller_cids.substring(1, seller_cids.length()-1);
			String[] arr_seller_cids = seller_cids.split(",");
			freeMarkerPaser.putData("seller_cids", arr_seller_cids );
		}
		String html = freeMarkerPaser.proessPageContent();
		return html;
	}
	
	public static void main(String[] args){
		String a = ",1,2,3,";
		a = a.substring(1,a.length()-1);
		//System.out.println(a);
	}

	@Override
	public void onAfterGoodsAdd(Map goods, HttpServletRequest request)
			throws RuntimeException {

	}

	@Override
	public void onAfterGoodsEdit(Map goods, HttpServletRequest request) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBeforeGoodsEdit(Map goods, HttpServletRequest request) {
		String[] arr_seller_cids = request.getParameterValues("seller_cids");
		String seller_cids = StringUtil.arrayToString(arr_seller_cids, ",");
		goods.put("seller_cids", "," + seller_cids + ",");

	}

	@Override
	public int getOrder() {
		// TODO Auto-generated method stub
		return 20;
	}

	@Override
	public String getTabName() {
		return "自定义分类";
	}

	public IVirtualCatManager getVirtualCatManager() {
		return virtualCatManager;
	}

	public void setVirtualCatManager(IVirtualCatManager virtualCatManager) {
		this.virtualCatManager = virtualCatManager;
	}

}
