package cn.zjp.nio.demo.stringmsg;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;

import java.net.InetSocketAddress;

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
                .channel(SocketChannel.class)
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
}
