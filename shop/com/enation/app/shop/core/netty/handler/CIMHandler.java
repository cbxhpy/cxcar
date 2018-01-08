package com.enation.app.shop.core.netty.handler;

import io.netty.channel.ChannelHandlerContext;

/**
 * Created by Administrator on 2016/3/29.
 * 请求处理接口,所有的请求实现必须实现此接口
 */
public interface CIMHandler  {
    public abstract void process(ChannelHandlerContext ctx, Object message)throws  Exception;
}
