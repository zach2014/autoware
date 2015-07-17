/**
 * 
 */
package cn.zjp.nio.demos;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author zach15
 *
 */
public class MyEchoServer {
	private Integer port = null;
	private EventLoopGroup bossG = new NioEventLoopGroup(1);
	private EventLoopGroup workG = new NioEventLoopGroup();
	private ServerBootstrap bootstrap = new ServerBootstrap();
	
	public MyEchoServer(){
		this(8090);
	}

	public MyEchoServer(int port) {
		this.port = port;
		// configure server firstly 
		bootstrap.group(bossG, workG);
		bootstrap.channel(NioServerSocketChannel.class);
		bootstrap.option(ChannelOption.SO_BACKLOG, 100);
		bootstrap.childHandler(new ServerChannelInitializer());
	}
	
	public void work() throws InterruptedException{
		ChannelFuture f = bootstrap.bind(port).sync();
		f.channel().closeFuture().sync();
	}
	
	public void shutdown(){
		bossG.shutdownGracefully();
		workG.shutdownGracefully();
	}
}

final class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {
	
	public void initChannel(SocketChannel ch){
		ch.pipeline().addLast(new EchoServerHandler());
	}
}