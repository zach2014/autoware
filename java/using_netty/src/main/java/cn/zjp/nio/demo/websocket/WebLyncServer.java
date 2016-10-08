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

public class WebLyncServer {
  private final ChannelGroup chatRoomOne = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
  private final NioEventLoopGroup bEvtGroup = new NioEventLoopGroup(1);
  private final NioEventLoopGroup wEvtGroup = new NioEventLoopGroup(16);
  private Channel channel;

  public static void main(String[] args) {
    InetSocketAddress sock = new InetSocketAddress(9090);
    final WebLyncServer lync_backEnd = new WebLyncServer();
    ChannelFuture future = lync_backEnd.start(sock);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        lync_backEnd.destroy();
      }
    });
    future.channel().closeFuture().syncUninterruptibly();
  }

  public ChannelFuture start(InetSocketAddress sock) {
    ServerBootstrap serverBootstrap = new ServerBootstrap();
    serverBootstrap
      .group(bEvtGroup, wEvtGroup)
      .channel(NioServerSocketChannel.class)
      .childHandler(new WebLyncServerInitializer(chatRoomOne));
    ChannelFuture future = serverBootstrap.bind(sock);
    future.syncUninterruptibly();
    channel = future.channel();
    return future;
  }

  public void destroy() {
    if (channel != null) {
      channel.close();
    }
    chatRoomOne.close();
    bEvtGroup.shutdownGracefully();
  }
}
