package cn.zjp.nio.demo.stringmsg;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class StringMsgServer {

  private InetSocketAddress transport;

  public StringMsgServer(int port) {
    try {
      transport = new InetSocketAddress(InetAddress.getLocalHost(), port);
    } catch (UnknownHostException e) {
      System.exit(1);
    }
  }

  public static void main(String[] arg) {
    StringMsgServer s = new StringMsgServer(9090);
    s.start();
  }

  public void start() {
    NioEventLoopGroup eventLoop1 = new NioEventLoopGroup();
    ServerBootstrap sBootstrap = new ServerBootstrap();

    sBootstrap.group(eventLoop1)
      .handler(new LoggingHandler(LogLevel.DEBUG))
      .option(ChannelOption.SO_BACKLOG, 10)
      .channel(NioServerSocketChannel.class)
      .childHandler(new ChannelInitializer<SocketChannel>() {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
          ch.pipeline().addLast("String msg handler", new StringMsgHandler());
        }
      });
    try {
      sBootstrap.bind(transport).sync();
    } catch (InterruptedException e) {
      eventLoop1.shutdownGracefully();
    }
  }
}
