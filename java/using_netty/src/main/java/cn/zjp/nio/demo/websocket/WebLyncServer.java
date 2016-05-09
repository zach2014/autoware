package cn.zjp.nio.demo.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.ImmediateEventExecutor;

import java.net.InetSocketAddress;

/**
 * Created by zengjp on 16-5-8.
 */
public class WebLyncServer {
    private final ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
    private final NioEventLoopGroup group = new NioEventLoopGroup();
    private Channel channel;

    public ChannelFuture start(InetSocketAddress sock){
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(group)
                .channel(NioServerSocketChannel.class)
                .childHandler(new WebLyncServerInitializer(channelGroup));
        ChannelFuture future = serverBootstrap.bind(sock);
        future.syncUninterruptibly();
        channel = future.channel();
        return future;
    }

    public void destroy(){
        if(channel != null){
            channel.close();
        }
        channelGroup.close();
        group.shutdownGracefully();
    }

    public static void main(String[] args) {
        InetSocketAddress sock = new InetSocketAddress(9090);
        final WebLyncServer lync_backEnd = new WebLyncServer();
        ChannelFuture future = lync_backEnd.start(sock);
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                lync_backEnd.destroy();
            }
        });
        future.channel().closeFuture().syncUninterruptibly();
    }
}
