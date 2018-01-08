package com.enation.app.shop.core.netty.handler;


import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.HashMap;
import java.util.Map;

import com.enation.app.shop.core.netty.constant.MsgBody;
import com.enation.app.shop.core.netty.server.CimMessageExecutor;

/**
 * @author 创建人：Administrator
 * @version 版本号：V1.0
 * @ClassName 类名：WebCheckTokeHandler
 * @Description 功能说明：检查是否有权限
 * @date 创建日期：2016/3/31
 */
@ChannelHandler.Sharable
public class WebForwardHandler extends SimpleChannelInboundHandler<MsgBody> {
    
    private Map<String, CIMHandler> handlers = new HashMap<String, CIMHandler>();
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	System.out.println("WebForwardHandler.channelRead");
    }
    
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, MsgBody msgBody) throws Exception {
        String key = msgBody.getCmd();
        String token = msgBody.getToken();
        CIMHandler handler = handlers.get(key);
        if (handler != null)
            CimMessageExecutor.run(ctx,handler,msgBody);
    }
    public void setHandlers(Map<String, CIMHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    	//记录在线活动
    	//记录在线时长
        // 当出现异常就关闭连接
        cause.printStackTrace();
        System.out.println("exceptionCaught");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
    	
    	 //记录在线活动
         //记录在线时长
    	System.out.println("handlerRemoved");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	//记录在线时长
    	System.out.println("channelInactive");
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	//记录在线时长
    	System.out.println("channelActive");
    }
    
    
    
}
