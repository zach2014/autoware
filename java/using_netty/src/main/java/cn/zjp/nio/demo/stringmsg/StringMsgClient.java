package cn.zjp.nio.demo.stringmsg;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * Created by zengjp on 16-4-12.
 */
public class StringMsgClient {

    private InetSocketAddress remoteAddr;

    public StringMsgClient(InetSocketAddress sock) {
        this.remoteAddr = sock;
    }

    public void init(){
        NioEventLoopGroup eventLoop1 = new NioEventLoopGroup();
        Bootstrap cBootstrap = new Bootstrap();
        cBootstrap.group(eventLoop1)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringMsgPushHandler());
                    }
                });
        try {
            cBootstrap.connect(this.remoteAddr).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            eventLoop1.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        try {
            StringMsgClient c = new StringMsgClient(new InetSocketAddress(InetAddress.getLocalHost(), 9090));
            c.init();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }
}
