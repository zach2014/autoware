/**
 * 
 */
package cn.zjp.nio.demos;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.jboss.netty.util.CharsetUtil;

/**
 * @author zach15
 *
 */
public class MyEchoClientHandler extends SimpleChannelInboundHandler<Object> {

	private final ByteBuf firstMsg;
	
	public MyEchoClientHandler() {
		this(MyEchoClient.BUF_SIZE);
	}

	public MyEchoClientHandler(Integer bufSize) {
		firstMsg = Unpooled.buffer(bufSize);
		for(int i = 0; i < firstMsg.capacity(); i++) {
			firstMsg.writeByte(i);
		}
	}
	
	public void channelActive(ChannelHandlerContext cxt) {
		System.out.println("Echo: " + firstMsg.toString(CharsetUtil.UTF_8));
		cxt.writeAndFlush(firstMsg);
	}
	
	public void channelRead0(ChannelHandlerContext cxt, Object msg) {
		System.out.println("Received: " + msg.toString());
		if(msg.toString().contains("exit")) {
			cxt.close();
		}
		cxt.write(Unpooled.copiedBuffer(msg.toString(), CharsetUtil.UTF_8));
	}
	
	public void channelReadComplete(ChannelHandlerContext cxt){
		cxt.flush();
	}
	
	public void exceptionCaught(ChannelHandlerContext cxt, Throwable cause) {
		cause.printStackTrace();
		cxt.close();
	}
}
