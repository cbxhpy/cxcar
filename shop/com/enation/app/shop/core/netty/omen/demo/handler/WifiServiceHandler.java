package com.enation.app.shop.core.netty.omen.demo.handler;

import java.util.HashMap;
import java.util.Map;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.netty.Global;
import com.enation.app.shop.core.netty.handler.CIMHandler;
import com.enation.app.shop.core.netty.omen.demo.utils.MyLogger;

@Service()@Scope("prototype")
@Sharable
public class WifiServiceHandler extends ChannelHandlerAdapter{
	private static Logger log = Logger.getLogger(WifiServiceHandler.class);
	
	private Map<String, CIMHandler> handlers = new HashMap<String, CIMHandler>();
	
	@Override
	public void channelRead(ChannelHandlerContext ctx,Object msg)throws Exception{
		
		//WifiRequest req = (WifiRequest)msg;
		
		CIMHandler handler = handlers.get("201");
		
		if (handler != null)
			handler.process(ctx, msg);
		
		
		/*MyLogger.debug("读取数组数据完成,交付业务处理...");
		WifiRequest req = (WifiRequest)msg;
		MyLogger.debug("收到数据,开始组织返回数据...");
		//处理注册请求
		if(req.getReqType().equals(XmlData.REQ_TYPE_REGISTE)){
			
			//TODO
			//处理注册请求
			
			WifiResponse res = new WifiResponse();
			res.setResType((byte)0xf0);
			res.setSeq(req.getSeq());
			ctx.writeAndFlush(res);
			
			
		}
		//处理最新数据请求
		else{
			//do another thing
			WifiResponse res = new WifiResponse();
			res.setResType((byte)0xf0);
			res.setSeq(req.getSeq());
			ctx.writeAndFlush(res);
			
			log.info(res.toString());
		}*/
		
		MyLogger.debug("结果返回完成...");
			
	}
	
	@Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        // 添加
        Global.group.add(ctx.channel());
        System.out.println("Client:" + incoming.remoteAddress() + "加入");
        log.debug("Client:" + incoming.remoteAddress() + "加入");
    }
 
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("Client:" + incoming.remoteAddress() + "离开");
        // 移除
        Global.group.remove(ctx.channel());
        log.debug("Client:" + incoming.remoteAddress() + "离开");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("Client:" + incoming.remoteAddress() + "在线");
        log.debug("Client:" + incoming.remoteAddress() + "在线");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("Client:" + incoming.remoteAddress() + "掉线");
        log.debug("Client:" + incoming.remoteAddress() + "掉线");
    }

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		if (ctx != null) {
			MyLogger.error("Tcp 请求中发生异常,channelId["
					+ ctx.channel().id().asLongText() + "]");
			cause.printStackTrace();
			try {
				MyLogger.info("开始关闭业务流程...");
			} catch (Exception e) {
				MyLogger.error("无法正常关闭业务流程", e);
			}
			/*
			 * MyLogger.info("开始检查channel是否存活..."); if
			 * (ctx.channel().isActive()) { ctx.close();
			 * MyLogger.info("channel存活,开始关闭..."); }else
			 * MyLogger.info("channel已关闭,处理结束...");
			 */
		}

	}

	@Override  
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {  
		Channel channel=ctx.channel();
        if (evt instanceof IdleStateEvent){
            IdleStateEvent e = (IdleStateEvent) evt;
            Channel incoming = ctx.channel();
            if (e.state()== IdleState.READER_IDLE){//---No data was received for a while ,read time out... ...
                System.out.println("Client:" + incoming.remoteAddress() + "心跳读超时");
                log.debug("Client:" + incoming.remoteAddress() + "心跳读超时");
                //channel.close();  //call back channelInactive(ChannelHandlerContext ctx)
            } //      because we are attaching  more importance to the read_idle time(timeout to rec)
            else  if (e.state()== IdleState.WRITER_IDLE){//---No data was sent for a while.write time out... ...
                //channel.close();
                System.out.println("Client:" + incoming.remoteAddress() + "心跳写超时");
                log.debug("Client:" + incoming.remoteAddress() + "心跳写超时");
            }
            else if (e.state() == IdleState.ALL_IDLE){
            	System.out.println("Client:" + incoming.remoteAddress() + "心跳读写超时");
            	channel.close();
            	log.debug("Client:" + incoming.remoteAddress() + "心跳读写超时");
            }
        }else {
            super.userEventTriggered(ctx,evt);
        }
	}
	
	public void setHandlers(Map<String, CIMHandler> handlers) {
        this.handlers = handlers;
    }


}
