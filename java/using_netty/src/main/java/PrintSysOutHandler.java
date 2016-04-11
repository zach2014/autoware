import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * Created by zengjp on 16-4-7.
 */
public class PrintSysOutHandler extends ChannelInboundHandlerAdapter{

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("New Channel: " + ctx.name());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buffered_msg = ((ByteBuf) msg);
        while(buffered_msg.isReadable()) {
            System.out.print((char) buffered_msg.readByte());
        }
        ReferenceCountUtil.release(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
