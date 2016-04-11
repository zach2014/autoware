/*
 * JustPrintReceivedServer.java
 * Copyright (C) 2016  <>
 *
 * Distributed under terms of the MIT license.
 */

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ListenAndPrinter
{
    private int port;
    public ListenAndPrinter(int port) {
        this.port = port;
    }

    public void start(){
        EventLoopGroup eGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        b.group(eGroup)
         .channel(NioServerSocketChannel.class)
         .option(ChannelOption.SO_BACKLOG, 128)
         .childHandler(new ChannelInitializer<SocketChannel>() {

             @Override
             protected void initChannel(SocketChannel ch) throws Exception {
                 ch.pipeline()
                   .addLast("SysOutPrinter", new PrintSysOutHandler());
             }
         }).option(ChannelOption.SO_KEEPALIVE, true);
        ChannelFuture future = b.bind(this.port);
        System.out.println("Server is bound at " + this.port);
        try{
            future.sync();
        } catch (InterruptedException i){

        } finally {
            //eGroup.shutdownGracefully();
        }

    }

    public static void main(String args[]) {
        ListenAndPrinter server = new ListenAndPrinter(8080);
        server.start();
    }
}

