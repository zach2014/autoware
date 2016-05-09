package cn.zjp.nio.demo.printout;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * Created by zengjp on 16-4-8.
 */
public class TalktiveSpeaker {

    private int port;
    public TalktiveSpeaker(int port) {
        this.port = port;
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
        b.bind(this.port);
        ChannelFuture f = b.connect(new InetSocketAddress(this.port));
        System.out.println("Client will connect");
        try {
            f.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            cGroup.shutdownGracefully();
        }
    }

    public static void main(String args[]){
        TalktiveSpeaker speaker = new TalktiveSpeaker(8080);
        speaker.run();
    }
}
