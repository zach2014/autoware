/**
 * 
 */
package cn.zjp.nio.demos;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author zach15
 *
 */
public class MyEchoClient {
	
	public static final Integer BUF_SIZE = 256;
	private EventLoopGroup group = new NioEventLoopGroup();
	private Bootstrap bootstrap = new Bootstrap();

	public MyEchoClient() {
		bootstrap.group(group);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.handler( new ClientChannelInitializer());
	}
	
	public void echo(String host, Integer port) throws InterruptedException{
		ChannelFuture f = bootstrap.connect(host, port).sync();
		f.channel().closeFuture().sync();
	}
	
	public void shutdown(){
		group.shutdownGracefully();
	}
}

final class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline().addLast(new MyEchoClientHandler());
	}
}