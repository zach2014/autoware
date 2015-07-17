/**
 * 
 */
package cn.zjp.nio.demos;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.jboss.netty.util.CharsetUtil;

/**
 * @author zach15
 *
 */
public class EchoServerHandler extends SimpleChannelInboundHandler<Object> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		if(msg.toString().isEmpty()) {
			ctx.close();
		} else {
			System.out.println("Received: " + msg.toString());
			ctx.write(Unpooled.copiedBuffer(msg.toString(), CharsetUtil.UTF_8));
		}
	}
	
	public void channelReadComplete(ChannelHandlerContext ctx){
		ctx.flush();
	}
	
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
		cause.printStackTrace();
		ctx.close();
	}
}
