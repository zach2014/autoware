package cn.zjp.nio.demo.printout;/*
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
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
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
        EventLoopGroup sGroup = new EpollEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        b.group(sGroup)
         .channel(EpollServerSocketChannel.class)
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
        try {
            future.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            sGroup.shutdownGracefully();
        }

    }

    public static void main(String args[]) {
        ListenAndPrinter server = new ListenAndPrinter(8080);
        server.start();
    }
}

