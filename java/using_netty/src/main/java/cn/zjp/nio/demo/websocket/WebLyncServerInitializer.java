package cn.zjp.nio.demo.websocket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * Created by zengjp on 16-5-9.
 */
public class WebLyncServerInitializer extends ChannelInitializer<Channel> {
    private final ChannelGroup group;

    public WebLyncServerInitializer(ChannelGroup group){
        this.group = group;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
                .addLast(new HttpServerCodec())
                .addLast(new ChunkedWriteHandler())
                .addLast(new HttpObjectAggregator(64*1024))
                .addLast(new HttpRequestStandardHandler("/ws"))
                .addLast(new WebSocketServerProtocolHandler("/ws"))
                .addLast(new WebSocketTextMsgFrameHandler(group));
    }
}
