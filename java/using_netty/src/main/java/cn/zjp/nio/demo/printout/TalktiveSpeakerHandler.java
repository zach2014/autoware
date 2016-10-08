package cn.zjp.nio.demo.printout;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class TalktiveSpeakerHandler extends ChannelInboundHandlerAdapter {
  private static final byte[] GREEDY = {'H', 'e', 'l', 'l', 'o', ',', 't', 'h', 'e', 'r', 'e', '.', '\n'};
  private ChannelHandlerContext ctx;
  private ByteBuf greedy_msg;
  private final ChannelFutureListener repeator = new ChannelFutureListener() {
    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
      if (future.isSuccess()) {
        sayHello();
      } else {
        future.channel().close();
      }
    }
  };

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    this.ctx = ctx;
    greedy_msg = ctx.alloc().directBuffer().writeBytes(GREEDY);
    sayHello();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    ctx.close();
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    ReferenceCountUtil.release(greedy_msg);
  }

  private void sayHello() {
    ctx.writeAndFlush(greedy_msg.duplicate().retain()).addListener(repeator);
  }

}