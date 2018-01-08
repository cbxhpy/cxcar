package com.enation.app.shop.component.goodscore.widget.goods.sns;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.eop.sdk.widget.AbstractWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.RequestUtil;
import com.enation.framework.util.StringUtil;

/**
 * 商品sns分享挂件
 * @author kingapex
 *2012-3-31下午9:17:43
 */
@Component("goods_sns_share")
@Scope("prototype")
public class GoodsSnsShareWidget extends AbstractWidget {

	@Override
	protected void config(Map<String, String> params) {

	}

	@Override
	protected void display(Map<String, String> params) {
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		Map goods  = (Map)request.getAttribute("goods");
		if(goods==null) throw new RuntimeException("参数显示挂件必须和商品详细显示挂件同时存在");
		String default_img  = (String)goods.get("small");
		if(StringUtil.isEmpty(default_img)){
			default_img =EopSetting.IMG_SERVER_DOMAIN +"/images/no_picture.jpg";
		}else{
//			default_img= UploadUtil.getThumbPath(default_img, "_small");
			default_img=UploadUtil.replacePath(default_img);
		}
		;
		String title  = (String)goods.get("name");
		String url = RequestUtil.getWholeUrl(request);
		
		title = title.replaceAll("\r", "").replaceAll("\n", "");
		
		this.putData("title",title);
		this.putData("url",url);
		this.putData("img",default_img);
	}

}
