package com.enation.app.shop.core.netty.server;


import com.enation.app.shop.core.netty.constant.CIMConstant;
import com.enation.app.shop.core.netty.constant.MsgBody;
import com.enation.app.shop.core.netty.handler.CIMHandler;
import com.enation.app.shop.core.utils.CommonUtil;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2016/3/29.
 */
public class CimMessageExecutor implements Runnable{
    private static Logger log = LoggerFactory.getLogger(CimMessageExecutor.class);
    private ChannelHandlerContext channelHandlerContext;
    private MsgBody message;
    private CIMHandler handler;
    @Override
    public void run() {
        CimMessageExecutor.run(this.channelHandlerContext,this.handler,this.message);
    }
    public static void run(ChannelHandlerContext ctx,
                            CIMHandler handler, MsgBody message){
        if (ctx == null || !ctx.channel().isActive()) {
            throw new IllegalArgumentException("Broken channel");
        }

        if (handler == null) {
            throw new IllegalArgumentException("Null handler");
        }

        if (message == null) {
            throw new IllegalArgumentException("Null message");
        }
        try {
            boolean isAuthorized = true;
            if (isAuthorized){
                handler.process(ctx,message);
            }else {
                message.setCode(CIMConstant.ReturnCode.CODE_403);
                CommonUtil.pushMessage(ctx,message);
            }
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(),e);
            message.setCode(CIMConstant.ReturnCode.CODE_500);
            CommonUtil.pushMessage(ctx,message);
        }
    }
    public CimMessageExecutor(ChannelHandlerContext channelHandlerContext,
                              CIMHandler handler, MsgBody message) {
        if (channelHandlerContext == null || !channelHandlerContext.channel().isActive()) {
            throw new IllegalArgumentException("Broken channel");
        }

        if (handler == null) {
            throw new IllegalArgumentException("Null handler");
        }

        if (message == null) {
            throw new IllegalArgumentException("Null message");
        }

        this.channelHandlerContext = channelHandlerContext;
        this.handler = handler;
        this.message = message.clone();
    }
}
