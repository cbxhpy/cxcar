package com.enation.app.shop.component.search.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Cat;
import com.enation.app.shop.core.plugin.search.IGoodsSearchFilter;
import com.enation.app.shop.core.plugin.search.IGoodsSortFilter;
import com.enation.app.shop.core.plugin.search.IPutWidgetParamsEvent;
import com.enation.app.shop.core.plugin.search.SearchSelector;
import com.enation.app.shop.core.utils.UrlUtils;
import com.enation.framework.plugin.AutoRegisterPlugin;

/***
 * 商品排序过滤器
 * @author kingapex
 *
 */
@Component
public class SortSearchFilter extends AutoRegisterPlugin implements
		IGoodsSearchFilter,IGoodsSortFilter,IPutWidgetParamsEvent {

	/**
	 * //价格从低到高    
            
//价格从高到低    
            
//评价从高到低    
            
//评价从低到高    
            
//销量从高到低    
            
//销量从低到高    
            
/上架时间从高到低   
            
//上架时间从低到高  
	 */
	private static final String[] sortItemList  =
		new String[]{"默认排序","价格从高到低","价格从低到高","评价从高到低","评价从低到高","销量从高到低","销量从低到高","上架时间从新到旧","上架时间从旧到新"};
	
	@Override
	public List<SearchSelector> createSelectorList(Cat cat, String url,
			String urlFragment) {
		
		String sort  = urlFragment==null?"":urlFragment;
		
		List<SearchSelector>  selectorList  = new ArrayList<SearchSelector>();
		int i=0;
		for(String item:sortItemList){
			
			SearchSelector selector = new SearchSelector();
			selector.setName(item);
			selector.setUrl(UrlUtils.addUrl(url,"sort",""+(i+1)));
			selector.setValue(String.valueOf((i+1)));
			
			if(sort.equals(String.valueOf( (i+1) ))){
				selector.setSelected(true);
				
			}
			selectorList.add(selector);
			i++;
		}
		
		return selectorList;
	}

	
	@Override
	public void filter(StringBuffer sql, Cat cat, String urlFragment) {
		String order = urlFragment;
		
		//默认按上架时间从低到高排序
		if(order == null || order.equals("")){
			order = "6";
		}
		if ("1".equals(order)) {  
			order = "sord desc";
		}  else if ("2".equals(order)) { //价格从高到低
			order = "price desc";
		}else if ("3".equals(order)) { //价格从低到高
			order = "price asc";
		} else if ("4".equals(order)) { //评价从高到低
			order = "grade desc";
		} else if ("5".equals(order)) { //评价从低到高
			order = "grade asc";
		} else if ("6".equals(order)) { //销量从高到低
			order = "buy_count desc";
		} else if ("7".equals(order)) { //销量从低到高
			order = "buy_count asc";
		}else if ("8".equals(order)) { //上架时间从高到低
			order = "last_modify desc";
		}else if ("9".equals(order)) {  //上架时间从低到高
			order = "last_modify asc";
		} else if (order == null || order.equals("") || order.equals("0")) {
			order = "sord desc";
		}

		sql.append(" order by "+ order);

	}

	
	/**
	 * 向挂件中压入当前排序值 
	 */
	@Override
	public void putParams(Map<String, Object> params, String urlFragment,String url) {
		
		String currSort  = urlFragment;
		Map sorter = new HashMap(4);
		
		Map price = new  HashMap(3);		
		price.put("url", UrlUtils.addUrl(url,"sort", "2".equals(currSort)?"3":"2"));		
		price.put("selected",  "2".equals(currSort)|| "3".equals(currSort));
		price.put("sorttype",  "2".equals(currSort)?"desc":"asc");
		
		
		Map grade = new  HashMap(3);		
		grade.put("url", UrlUtils.addUrl(url,"sort", "4".equals(currSort)?"5":"4"));		
		grade.put("selected",  "4".equals(currSort)|| "5".equals(currSort));
		grade.put("sorttype",  "4".equals(currSort)?"desc":"asc");
		
		
		Map sales = new  HashMap(3);		
		sales.put("url", UrlUtils.addUrl(url,"sort", "6".equals(currSort)?"7":"6"));		
		sales.put("selected",  "6".equals(currSort)|| "7".equals(currSort));
		sales.put("sorttype",  "6".equals(currSort)?"desc":"asc");
		
		Map time = new  HashMap(3);		
		time.put("url", UrlUtils.addUrl(url,"sort", "8".equals(currSort)?"9":"8"));		
		time.put("selected",  "8".equals(currSort)|| "9".equals(currSort));
		time.put("sorttype",  "8".equals(currSort)?"desc":"asc");
		
		sorter.put("price", price);
		sorter.put("grade", grade);
		sorter.put("sales", sales);
		sorter.put("time", time);
		
		params.put("sort", currSort);
		params.put("sorter", sorter);
	}
	
	@Override
	public String getFilterId() {
		
		return "sort";
	}

	
	public String getAuthor() {
		
		return "kingapex";
	}

	
	public String getId() {
		
		return "sortSearchFilter";
	}

	
	public String getName() {
		
		return "商品排序过滤器";
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
