package com.enation.app.shop.core.netty.handler;

import java.nio.ByteOrder;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.enation.app.shop.core.netty.omen.demo.handler.WifiServiceHandler;
import com.enation.app.shop.core.netty.omen.demo.handler.codec.WifiLengthFieldDecoder;
import com.enation.app.shop.core.netty.omen.demo.handler.codec.WifiLengthFieldEncoder;


/**
 * Created by Administrator on 2016/3/24.
 */
public class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
    private static Logger log = LoggerFactory.getLogger(ChildChannelHandler.class);
    private WebSocketServerHandler webSocketServerHandler;
    @Autowired
    private WebForwardHandler webForwardHandler;
    private WifiLengthFieldDecoder wifiLengthFieldDecoder;
    private WifiLengthFieldEncoder wifiLengthFieldEncoder;
    private WifiServiceHandler wifiServiceHandler;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        log.debug("报告");
        log.debug("信息：有一客户端链接到本服务端");
        log.debug("IP:" + socketChannel.remoteAddress().getAddress().getHostAddress()+":"+socketChannel.remoteAddress().getPort());
        log.debug("报告完毕");
        //socketChannel.pipeline().addLast("http-codec",new HttpServerCodec());
        socketChannel.pipeline().addLast("aggregator",new HttpObjectAggregator(65536));
        //socketChannel.pipeline().addLast("http-chunked",new ChunkedWriteHandler());
        socketChannel.pipeline().addLast("timeout",new IdleStateHandler(181,181,181));
        //socketChannel.pipeline().addLast(new WifiLengthFieldDecoder());
        socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN, 4*1024*1024, 6, 1, 0, 0, true));
        //socketChannel.pipeline().addLast(wifiLengthFieldEncoder);
        socketChannel.pipeline().addLast(wifiServiceHandler);
        /*socketChannel.pipeline().addLast(new WebSocketServerProtocolHandler("/"));
        socketChannel.pipeline().addLast(webSocketServerHandler);
        socketChannel.pipeline().addLast(webForwardHandler);*/
    }

    public void setWebSocketServerHandler(WebSocketServerHandler webSocketServerHandler) {
        this.webSocketServerHandler = webSocketServerHandler;
    }

	public void setWifiLengthFieldDecoder(
			WifiLengthFieldDecoder wifiLengthFieldDecoder) {
		this.wifiLengthFieldDecoder = wifiLengthFieldDecoder;
	}

	public void setWifiLengthFieldEncoder(
			WifiLengthFieldEncoder wifiLengthFieldEncoder) {
		this.wifiLengthFieldEncoder = wifiLengthFieldEncoder;
	}

	public void setWifiServiceHandler(WifiServiceHandler wifiServiceHandler) {
		this.wifiServiceHandler = wifiServiceHandler;
	}

    
}
