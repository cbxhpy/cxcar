package com.enation.app.shop.component.member.widget;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import net.sf.json.JSONObject;

import com.enation.app.shop.core.service.IOrderManager;
import com.enation.eop.processor.core.RemoteRequest;
import com.enation.eop.processor.core.Request;
import com.enation.eop.processor.core.Response;
import com.enation.eop.sdk.widget.AbstractWidget;
import com.enation.framework.context.webcontext.ThreadContextHolder;

/**
 * 快递001查询挂件
 * @author kingapex
 *2012-4-1上午8:09:45
 */
@Component("kuaidi")
@Scope("prototype")
public class Kuaidi100Widget extends AbstractWidget {
	private IOrderManager orderManager;

	@Override
	protected void display(Map<String, String> params) {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String logino = request.getParameter("logino");//物流号
		String code = request.getParameter("code");//物流公司代码
		if(logino==null || logino.length()<5){
 
			Map result = new HashMap();
			result.put("status", "-1");
			this.putData("result",result);
			return;
		}
		if(code == null || code.equals("")){
			code = "yuantong";
		}
		Request remoteRequest  = new RemoteRequest();
		//"http://api.kuaidi100.com/api?id=c24581986993585e&com=yuantong&nu=7000711003&show=2&muti=11")
		Response remoteResponse = remoteRequest.execute("http://api.kuaidi100.com/api?id=c24581986993585e&com="+code+"&nu="+logino+"&show=0&muti=1&order=asc");
		String content  = remoteResponse.getContent();
		Map result = (Map)JSONObject.toBean( JSONObject.fromObject(content) ,Map.class );
		this.putData("result",result);

	}

	@Override
	protected void config(Map<String, String> params) {
		// TODO Auto-generated method stub

	}

	public IOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}


}
