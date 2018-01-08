package com.enation.app.shop.core.netty.omen.netty.server.service;

import io.netty.channel.ChannelHandlerContext;

import com.enation.app.shop.core.netty.omen.netty.server.http.pojo.HttpParameter;

public interface  HttpServerService{

	/**
	 * 
	* @Title: doService
	* @Description: Http业务请求应答消息处理类，返回String
	* @param @param paramMap 第一个key存放Set<Cookie> cookies
	* @param @return    
	* @return String    
	* @throws
	* @author omen  www.liyidong.com 
	* @date 2014年12月25日 下午2:59:22
	 */
	public  String doService(HttpParameter httpParameter)throws Exception ;
	
	/**
	 * 
	* @Title: close
	* @Description: 链路关闭时释放在doService方法中创建的，不会随channel关闭而回收但是需要回收的资源回收操作。譬如异常退出时，删除hashtable里的数据等。注：无需在此方法关闭channel
	* @param @param ctx
	* @param @throws Exception    
	* @return void    
	* @throws
	* @author omen  www.liyidong.com 
	* @date 2015年6月17日 下午4:11:49
	 */
	public void close(ChannelHandlerContext ctx, Throwable cause)throws Exception;
	
	
//	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)throws Exception;
	
}
