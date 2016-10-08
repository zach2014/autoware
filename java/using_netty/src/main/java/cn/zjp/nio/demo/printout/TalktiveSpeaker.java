package cn.zjp.nio.demo.printout;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.SocketChannel;

import java.net.InetSocketAddress;

public class TalktiveSpeaker {

  private final int port;

  public TalktiveSpeaker(int port) {
    this.port = port;
  }

  public static void main(String[] args) {
    TalktiveSpeaker speaker = new TalktiveSpeaker(8080);
    speaker.run();
  }

  public void run() {
    Bootstrap b = new Bootstrap();
    EpollEventLoopGroup cGroup = new EpollEventLoopGroup();
    //NioEventLoopGroup cGroup = new NioEventLoopGroup();
    b.group(cGroup)
      .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 120)
      .channel(EpollSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
      @Override
      protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast("Repeative speaker", new TalktiveSpeakerHandler());
      }
    });
    b.bind(port);
    ChannelFuture f = b.connect(new InetSocketAddress(port));
    System.out.println("Client will connect");
    try {
      f.await();
    } catch (InterruptedException e) {
      cGroup.shutdownGracefully();
    }
  }
}
