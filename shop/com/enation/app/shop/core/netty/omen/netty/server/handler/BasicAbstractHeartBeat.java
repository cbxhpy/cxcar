package com.enation.app.shop.core.netty.omen.netty.server.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public abstract class BasicAbstractHeartBeat  extends ChannelHandlerAdapter{
	
	@Override
	public void channelRead(ChannelHandlerContext ctx,Object msg)throws Exception{
		//判断是否是心跳报文
		if(isHeartBeat(ctx,msg))
			dealHeartBeat(ctx,msg);
		else
			ctx.fireChannelRead(msg);
	}
	
	protected abstract boolean isHeartBeat(ChannelHandlerContext ctx,Object msg);
		
	
	protected abstract void dealHeartBeat(ChannelHandlerContext ctx,Object msg);
	
}
