package com.enation.app.shop.component.goodscore.widget.goods.detail;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.eop.sdk.widget.AbstractWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.StringUtil;

/**
 * 商品描述信息挂件
 * @author kingapex
 *
 */
@Component("goods_intro")
@Scope("prototype")
public class GoodsIntroWidget extends AbstractWidget {
 
	
	@Override
	protected void config(Map<String, String> params) {

	}

	
	@Override
	protected void display(Map<String, String> params) {
		
		Map goods  = (Map)ThreadContextHolder.getHttpRequest().getAttribute("goods");
		
		if(goods==null) throw new RuntimeException("参数显示挂件必须和商品详细显示挂件同时存在");
		String intro =(String)goods.get("intro");
		intro =intro==null?"":intro;
		intro = UploadUtil.replacePath(intro);//替换地址
		
		String lazyload = params.get("lazyload");
		if("yes".equals(lazyload)){
			
			String placeholder= params.get("placeholder");
			if(StringUtil.isEmpty(  placeholder )){
				placeholder="http://javashop3.javamall.com.cn/themes/default/images/ajax-loader.gif";
			}
				
			intro= replace(intro,placeholder);
		}
		
		this.putData("intro", intro);
		this.setPageName("intro");
	}

	public String replace(String content,String placeholder){
	 
		//String img="<img class=\"test\" src='http://www.aaa.com/aa.jpg' >";
		
		String pattern ="<img([^<|^>]*?)src=[\"|\'](.*?)[\"|\']([^<|^>]*?)>";
		 
		Pattern p = Pattern.compile(pattern, 2 | Pattern.DOTALL);
		Matcher m = p.matcher(content);
		
		if(m.find()){
			content  =m.replaceAll("<img$1src=\""+placeholder+"\"$3 original=\"$2\">");
		}
		
		return content;
	 
	}
	
	

}
