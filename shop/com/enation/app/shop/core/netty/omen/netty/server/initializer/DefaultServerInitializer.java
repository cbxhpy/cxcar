package com.enation.app.shop.core.netty.omen.netty.server.initializer;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslHandler;

import java.util.List;

import javax.net.ssl.SSLEngine;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.netty.omen.netty.securechat.SecureChatSslContextFactory;
import com.enation.app.shop.core.netty.omen.netty.server.sysPojo.ServerList;


@Service()@Scope("prototype")
public class DefaultServerInitializer extends ChannelInitializer<SocketChannel>  {
  
//private List<ChannelHandler> channelHandlerList;
	
	private String handlerListName;
	
	//默认为false
	private Boolean isSsl=false;
	
    private static Logger log = Logger.getLogger(DefaultServerInitializer.class);
   
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		List<ChannelHandler> channelHandlerList =(List<ChannelHandler>)ServerList.getCtx().getBean(handlerListName);
		
		ChannelPipeline pipeline = ch.pipeline();
		if (isSsl==null||isSsl) {
			SSLEngine engine = SecureChatSslContextFactory.getServerContext().createSSLEngine();
			engine.setNeedClientAuth(true); //ssl双向认证
			engine.setUseClientMode(false);
			engine.setWantClientAuth(true);
			engine.setEnabledProtocols(new String[]{"SSLv3"});
			pipeline.addLast("ssl", new SslHandler(engine));
		}
		
		for(Object channelHandler: channelHandlerList){
			System.out.println("添加的handler类型：" + channelHandler.getClass());
			System.out.println("handler 内存地址：" + channelHandler.hashCode());
  			pipeline.addLast((ChannelHandler)channelHandler);
  		}
		
	}
  	
  	
  	public Boolean getIsSsl() {
  		return isSsl;
  	}
  	
  	public void setIsSsl(Boolean isSsl) {
  		this.isSsl = isSsl;
  	}
  	
  	public String getHandlerListName() {
		return handlerListName;
	}
  	
  	public void setHandlerListName(String handlerListName) {
		this.handlerListName = handlerListName;
	}
  	
}

