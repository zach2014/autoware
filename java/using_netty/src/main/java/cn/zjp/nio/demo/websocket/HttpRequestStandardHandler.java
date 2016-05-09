package cn.zjp.nio.demo.websocket;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by zengjp on 16-5-8.
 */
public class HttpRequestStandardHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final File INDEX;
    private final String ws_Uri;

    static {
//        URL location = HttpRequestStandardHandler.class.getProtectionDomain().getCodeSource().getLocation();
        URL location = HttpRequestStandardHandler.class.getClassLoader().getResource("index.html");
        try {
            String path_index = location.toURI().toString();
            if(path_index.contains("file:")){
                path_index = path_index.substring(5);
            }
            INDEX = new File(path_index);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Fail to locate index.html", e);
        }
    }
    public HttpRequestStandardHandler(String ws){
        this.ws_Uri = ws;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        if(ws_Uri.equals(msg.uri())) {
            ctx.fireChannelRead(msg.retain());
        }
        else{
            if(HttpUtil.is100ContinueExpected(msg)){
                FullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
                ctx.writeAndFlush(resp);
            }

            RandomAccessFile index_Content = new RandomAccessFile(INDEX, "r");
            HttpResponse response = new DefaultFullHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
            boolean isKeepAlive = HttpUtil.isKeepAlive(msg);
            if(isKeepAlive){
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, index_Content.length());
            }
            ctx.write(response);

            if(ctx.pipeline().get(SslHandler.class) == null){
                ctx.write(new DefaultFileRegion(index_Content.getChannel(), 0, index_Content.length()));
            }
            else {
                ctx.write(new ChunkedNioFile(index_Content.getChannel()));
            }

            ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

            if(! isKeepAlive){
                future.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
