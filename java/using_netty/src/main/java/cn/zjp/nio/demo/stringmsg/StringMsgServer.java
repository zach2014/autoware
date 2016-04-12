package cn.zjp.nio.demo.stringmsg;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * Created by zengjp on 16-4-12.
 */
public class StringMsgServer {
    public static final String DEF_RESP = "Got it.";

    private InetSocketAddress transport;

    public StringMsgServer(int port) {
        try {
            this.transport = new InetSocketAddress(InetAddress.getLocalHost(), port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public StringMsgServer(InetSocketAddress sock) {
        this.transport  = sock;
    }

    public void start(){
        NioEventLoopGroup eventLoop1 = new NioEventLoopGroup();
        ServerBootstrap sBootstrap = new ServerBootstrap();

        sBootstrap.group(eventLoop1)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .option(ChannelOption.SO_BACKLOG, 10)
                .channel(ServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast("String msg handler", new StringMsgHandler());
                    }
                });
        try {
            sBootstrap.bind(this.transport).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            eventLoop1.shutdownGracefully();
        }
    }
}
