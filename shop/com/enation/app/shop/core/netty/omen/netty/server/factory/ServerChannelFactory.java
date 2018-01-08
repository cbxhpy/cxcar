package com.enation.app.shop.core.netty.omen.netty.server.factory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import javax.ejb.DependsOn;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.netty.omen.netty.exception.SysErrException;
import com.enation.app.shop.core.netty.omen.netty.server.initializer.DefaultServerInitializer;
import com.enation.app.shop.core.netty.omen.netty.server.sysPojo.ServerInfo;
import com.enation.app.shop.core.netty.omen.netty.server.sysPojo.ServerList;


@Service()@DependsOn({"httpServerHandler"})@Scope("prototype")
public class ServerChannelFactory {

	
	@Autowired
	private DefaultServerInitializer defaultServerInitializer;
	
	private static Logger log = Logger.getLogger(ServerChannelFactory.class);
	
	public  Channel createAcceptorChannel(int serverNo) throws SysErrException{
		
		ServerInfo serverInfo = ServerList.getServerInfo(serverNo);
		
		final ServerBootstrap serverBootstrap = ServerBootstrapFactory.createServerBootstrap(serverNo);
//		defaultServerInitializer.setChannelHandlerList(serverInfo.getChannelHandlerList());
		defaultServerInitializer.setHandlerListName(serverInfo.getHandlerListName());
		serverBootstrap.childHandler(defaultServerInitializer);
		log.info("创建Server...");
		 try {
			 ChannelFuture channelFuture = serverBootstrap.bind(serverInfo.getPort()).sync();
			 channelFuture.awaitUninterruptibly();
	            if (channelFuture.isSuccess()) {
	            	serverInfo.printSysInfo();
	                return channelFuture.channel();
	            } else {
	            	String errMsg="Failed to open socket! Cannot bind to port: "+serverInfo.getPort()+"!";
	            	log.error(errMsg);
	            	throw new SysErrException(errMsg);
	            }
				
		} catch (Exception e){
			throw new SysErrException(e);
		}
	}
}
