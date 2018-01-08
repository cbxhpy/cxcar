package com.enation.app.shop.core.netty.handler;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.slf4j.LoggerFactory;

import com.enation.app.shop.core.netty.constant.MsgBody;
import com.enation.app.shop.core.utils.CommonUtil;

/**
 * Created by Administrator on 2016/3/25.
 */
@ChannelHandler.Sharable
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger logger = Logger
            .getLogger(WebSocketServerHandshaker.class.getName());
    private static org.slf4j.Logger log = LoggerFactory.getLogger(WebSocketServerHandler.class);


    private WebSocketServerHandshaker handshaker;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("WebSocketServerHandler.channelRead");
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
    	if (msg instanceof FullHttpRequest) {

            handleHttpRequest(ctx, ((FullHttpRequest) msg));

        } else if (msg instanceof WebSocketFrame) {

            handlerWebSocketFrame(ctx, (WebSocketFrame) msg);

        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    private void handleHttpRequest(ChannelHandlerContext ctx,
                                   FullHttpRequest req) {

        if (!req.getDecoderResult().isSuccess()
                || (!"websocket".equals(req.headers().get("Upgrade")))) {

            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));

            return;
        }

        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                "ws://localhost:8090/websocket", null, false);

        handshaker = wsFactory.newHandshaker(req);

        if (handshaker == null) {
            WebSocketServerHandshakerFactory
                    .sendUnsupportedWebSocketVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }

    }

    private static void sendHttpResponse(ChannelHandlerContext ctx,
                                         FullHttpRequest req, DefaultFullHttpResponse res) {

        // 返回应答给客户端
        if (res.getStatus().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(),
                    CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }

        // 如果是非Keep-Alive，关闭连接
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!isKeepAlive(req) || res.getStatus().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private static boolean isKeepAlive(FullHttpRequest req) {

        return false;
    }

    private void handlerWebSocketFrame(ChannelHandlerContext ctx,
                                       WebSocketFrame frame) {

        // 判断是否关闭链路的指令
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame
                    .retain());
        }

        // 判断是否ping消息
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(
                    new PongWebSocketFrame(frame.content().retain()));
            return;
        }

        //仅支持文本消息，不支持二进制消息
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(String.format(
                    "%s frame types not supported", frame.getClass().getName()));
        }

        // 返回应答消息
        String request = ((TextWebSocketFrame) frame).text();
        log.error("服务端收到：" + request);
        try {
            MsgBody msgBody = CommonUtil.jackson.readValue(request,MsgBody.class);
            ctx.fireChannelRead(msgBody);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(),e);
        }
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(String.format("%s received %s", ctx.channel(),
                            request));
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        // 添加
        //Global.group.add(ctx.channel());
        System.out.println("Client:" + incoming.remoteAddress() + "加入");
        log.debug("Client:" + incoming.remoteAddress() + "加入");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("Client:" + incoming.remoteAddress() + "离开");
        // 移除
        //Global.group.remove(ctx.channel());
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
        Channel incoming = ctx.channel();
        System.out.println("Client:" + incoming.remoteAddress() + "异常");

        // 当出现异常就关闭连接
        cause.printStackTrace();
        log.error(cause.getMessage(),cause);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception{
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
}
