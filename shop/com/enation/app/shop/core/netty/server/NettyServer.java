package com.enation.app.shop.core.netty.server;

import com.enation.app.shop.core.netty.handler.ChildChannelHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Created by Administrator on 2016/3/24.
 */
public class NettyServer implements Server,Runnable {
    private static int port;
    private static EventLoopGroup bossGroup;
    private static EventLoopGroup workerGroup;
    private static ServerBootstrap serverBootstrap;
    private static ChildChannelHandler childChannelHandler;
    @Autowired
    private ThreadPoolTaskExecutor executor;
    
    @Override
    public void startServer() throws Exception {
        try {
            NettyServer nettyServer = new NettyServer();
            //executor.execute(nettyServer); // 禁用了
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void stopServer() throws Exception {
        // TODO: 2016/7/7 关闭netty
        System.out.println("关闭netty");
        // 优雅的退出
        Future bossFuture = bossGroup.shutdownGracefully();
        Future workerFuture = workerGroup.shutdownGracefully();
        try {
            bossFuture.await();
            workerFuture.await();
            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 3500)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childHandler(childChannelHandler)
                    //保持长连接状态
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            		//.childOption(ChannelOption.TCP_NODELAY,true);TCP默认关闭着的，如果存在有很多小包连续write,write再read的情况，开启nodelay比较合
            ChannelFuture future =serverBootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        NettyServer.port = port;
    }

    public void setBossGroup(EventLoopGroup bossGroup) {
        NettyServer.bossGroup = bossGroup;
    }

    public void setWorkerGroup(EventLoopGroup workerGroup) {
        NettyServer.workerGroup = workerGroup;
    }

    public void setServerBootstrap(ServerBootstrap serverBootstrap) {
        NettyServer.serverBootstrap = serverBootstrap;
    }

    public void setChildChannelHandler(ChildChannelHandler childChannelHandler) {
        NettyServer.childChannelHandler = childChannelHandler;
    }
}