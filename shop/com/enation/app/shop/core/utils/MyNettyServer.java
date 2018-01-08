package com.enation.app.shop.core.utils;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
 
public class MyNettyServer {
     
    ChannelFuture f = null;
     
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();
     
    private void run(int port) {
        try {
            // Configure the server.
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                        // 有连接到达时会创建一个Channel
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            System.out.println("has a request....");
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            pipeline.addLast(new LengthFieldPrepender(4));
                            pipeline.addLast(new ByteArrayDecoder());
                            pipeline.addLast(new ByteArrayEncoder());
                            // 在Channel队列中，添加自己的Handler处理业务
                            //pipeline.addLast(new WifiServiceHandler());
                        }
                });
             
            // 配置完成，开始绑定server，通过调用sync同步方法阻塞直到绑定成功
            f = b.bind(port).sync();
            System.out.println("Server statrted ...");
            // 应用程序会一直等待，直到channel关闭
            f.channel().closeFuture().sync();
        } catch (Exception e) {
             
        }
    }
     
    private void shutdown() {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }
 
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        MyNettyServer server = new MyNettyServer();
        server.run(8000);
    }   
}
