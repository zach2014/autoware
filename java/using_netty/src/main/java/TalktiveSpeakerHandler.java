import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * Created by zengjp on 16-4-8.
 */
public class TalktiveSpeakerHandler extends ChannelInboundHandlerAdapter {
    private static final byte[] GREEDY = {'H', 'e', 'l', 'l', 'o', ',', 't', 'h', 'e', 'r', 'e', '.', '\n'};
    private ChannelHandlerContext ctx;
    private ByteBuf greedy_msg;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        this.greedy_msg = ctx.alloc().directBuffer().writeBytes(GREEDY);
        sayHello();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ReferenceCountUtil.release(greedy_msg);
    }

    private final void sayHello() {
        ctx.writeAndFlush(greedy_msg.duplicate().retain()).addListener(repeator);
    }

    private final ChannelFutureListener repeator  = new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            sayHello();
        }
    };

}