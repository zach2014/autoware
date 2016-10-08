package cn.zjp.nio.demo.blocking;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by zengjp on 16-5-8.
 */
@SuppressWarnings("ALL")
public class SelectiveSocketServer {

  private final InetSocketAddress transport;

  public SelectiveSocketServer(InetSocketAddress sock) {
    transport = sock;
  }

  public static void main(String[] args) {
    InetSocketAddress sock = null;
    try {
      sock = new InetSocketAddress(InetAddress.getLocalHost(), 9090);
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
    SelectiveSocketServer server = new SelectiveSocketServer(sock);
    try {
      server.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void start() throws IOException {
    ServerSocketChannel server = ServerSocketChannel.open();
    server.configureBlocking(false);
    ServerSocket serverSocket = server.socket();
    serverSocket.bind(transport);
    Selector selector = Selector.open();
    server.register(selector, SelectionKey.OP_ACCEPT);

    for (; ; ) {
      // accept new connect from client
      selector.select();
      Set<SelectionKey> readyKeys = selector.selectedKeys();
      Iterator<SelectionKey> iterator = readyKeys.iterator();
      while (iterator.hasNext()) {
        SelectionKey actived = iterator.next();
        iterator.remove();
        if (actived.isAcceptable()) {
          ServerSocketChannel s = (ServerSocketChannel) actived.channel();
          SocketChannel client = s.accept();
          client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE,
            ByteBuffer.wrap("Got it\n".getBytes()));
          System.out.println("See client from: " + client);

        }
        if (actived.isReadable()) {
          SocketChannel client = (SocketChannel) actived.channel();
          ByteBuffer buff = ByteBuffer.allocate(1024);
          client.read(buff);
          if (buff.hasArray()) {
            String msgText = new String(buff.array());
            System.out.println(msgText);
          }
          client.write(ByteBuffer.wrap("Got it\n".getBytes()));
        }
      }
    }
  }
}
