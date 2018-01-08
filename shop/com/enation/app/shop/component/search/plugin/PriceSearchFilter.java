package com.enation.app.shop.component.search.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.enation.app.shop.core.model.Cat;
import com.enation.app.shop.core.plugin.search.IGoodsSearchFilter;
import com.enation.app.shop.core.plugin.search.SearchSelector;
import com.enation.app.shop.core.utils.UrlUtils;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.StringUtil;

/**
 * 价格搜索过虑器<br/>
 * 由themes下加载 price_filter.xml 过滤条件
 *  
 * @author kingapex
 *
 */
@Component
public class PriceSearchFilter extends AutoRegisterPlugin implements
		IGoodsSearchFilter {

	private static Map<String,List<Price>> priceMap;
	
	@Override
	public List<SearchSelector> createSelectorList(Cat cat, String url,
			String urlFragment) {
//		if(cat==null) return null;
		String catid =  "0";
//		if(cat!=null ) catid = cat.getCat_id().toString();
		
		String currMin = "";
		String currMax = "";
		
		if(!StringUtil.isEmpty( urlFragment ) ){
			
			String[] price = urlFragment.split("_");
			if(price!=null && price.length>=1){
				currMin  = price[0];
			}
			if(price!=null && price.length>=2){
				currMax  = price[1];
			}
			
		}
		
		List<SearchSelector>  list= new ArrayList<SearchSelector>();
		
		/**
		 * ------------------------ 生成'全部'价格的选择器 -----------------------
		 */
		SearchSelector allselector = new SearchSelector();
		allselector.setName("全部");
		allselector.setUrl(UrlUtils.getExParamUrl(url, "page")+".html");
		if (StringUtil.isEmpty(urlFragment)) {
			allselector.setSelected(true);
		} else {
			allselector.setSelected(false);
		}
		list.add(allselector);
		
		
		 Map<String,List<Price>> pMap  =PriceSearchFilter.getPriceMap();
		 List<Price> priceList  = pMap.get(catid);
		 if(priceList==null) return  null;
		 
		 for(Price price :priceList){
			 
			 String max = price.getMax();
			 String min = price.getMin();
			 
			 min = min==null ?"":min;
			 max = max==null ?"":max;
			 
			 String text  = price.getText();
			 
			 
			 SearchSelector selector = new SearchSelector();
			 selector.setName(text);
			 String priceUrl  = min+"_"+max;
			 selector.setUrl(UrlUtils.addUrl(url,"price",priceUrl));
			 
			 if(currMin.equals(min)  && currMax.equals( max ) ){
				 selector.setSelected(true);
			 }
			 
			 
			 list.add(selector);
			 
		 }
		 
		return list;
	}

	
	@Override
	public void filter(StringBuffer sql, Cat cat, String urlFragment) { 
		
		if(!StringUtil.isEmpty( urlFragment ) ){
			
			String[] price = urlFragment.split("_");
			if(price!=null && price.length>=1 && !StringUtil.isEmpty(price[0])){
				sql.append(" and  g.price>="+price[0]);				
			}
			if(price!=null && price.length>=2 && !StringUtil.isEmpty(price[1])){
				sql.append(" and g.price<"+price[1]);				
			}
			
		}
	}

	
	
	
	private  static Map<String,List<Price>> getPriceMap(){
		if(priceMap!=null) return priceMap;
		String xmlFile =EopSetting.EOP_PATH + EopContext.getContext().getContextPath() +"/themes/price_filter.xml";
		
		try {
			
		    DocumentBuilderFactory factory = 
		    DocumentBuilderFactory.newInstance();
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    Document document = builder.parse(xmlFile);
		    
		     priceMap = new HashMap<String, List<Price>>();
		    
		    NodeList catList  = document.getElementsByTagName("cat");
		    for( int i = 0;i<catList.getLength();i++){
		    	
		    	List<Price> priceList  = new ArrayList<Price>();
		    	
		    	Element catNode  = (Element) catList.item(i);
		    	NodeList priceNodeList  = catNode.getElementsByTagName("price");
		    	for (int j=0;j<priceNodeList.getLength();j++){
		    		
		    		
		    		Element priceEl  = (Element)priceNodeList.item(j);
		    		String text = priceEl.getTextContent();
		    		String minPrice  = priceEl.getAttribute("min");
		    		String maxPrice  = priceEl.getAttribute("max");
		    		Price price  = new Price();
		    		price.setText(text);
		    		
		    		if(!StringUtil.isEmpty(minPrice)){
		    			price.setMin(  minPrice  );
		    		}
		    		if(!StringUtil.isEmpty(maxPrice)){
		    			price.setMax(  maxPrice  );
		    		}

		    		priceList.add(price);
		    		
		    	}
		    	
		    	priceMap.put(catNode.getAttribute("id"), priceList);
		    	
		    }
		    
		    return priceMap;
		}
		catch (Exception e) {
			 
			e.printStackTrace();
			throw new RuntimeException("load  price_filter.xml   error" );
		} 	 
		 
	}
	
	@Override
	public String getFilterId() {
		
		return "price";
	}

	
	public String getAuthor() {
		
		return "kingapex";
	}

	
	public String getId() {
		
		return "priceSearchFilter";
	}

	
	public String getName() {
		
		return "价格搜索过虑器";
	}

	
	public String getType() {
		
		return "searchFilter";
	}

	
	public String getVersion() {
		
		return "1.0";
	}

	
	public void perform(Object... params) {
		

	}
	
	public void register() {
		

	}

}
