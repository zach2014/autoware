package cn.zjp.nio.demo.blocking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TaskDrivenServer {
  private static final Executor executor = Executors.newFixedThreadPool(5);

  public static void main(String[] args) {
    TaskDrivenServer server = new TaskDrivenServer();
    try {
      server.serve(InetAddress.getLocalHost(), 9090);
    } catch (IOException e) {
    }
  }

  public void serve(InetAddress host, int port) throws IOException {
    ServerSocket server = new ServerSocket(port, 1, host);
    System.out.println("Server listen at: " + server.getLocalSocketAddress());

    while (true) {
      final Socket activedSock = server.accept();
      Runnable task = new Runnable() {
        @Override
        public void run() {
          try {
            interactOn(activedSock);
          } catch (IOException e) {
            System.exit(1);
          } catch (InterruptedException e) {
          }
        }
      };
      executor.execute(task);
    }
  }

  private static void interactOn(Socket activedSock) throws IOException, InterruptedException {
    System.out.println("See client from: " + activedSock.getRemoteSocketAddress());
    InputStream inbound;

    inbound = activedSock.getInputStream();
    OutputStream outbound = activedSock.getOutputStream();
    BufferedReader readin = new BufferedReader(new InputStreamReader(inbound));
    PrintStream writeout = new PrintStream(outbound);

    String msg, resp;
    while ((msg = readin.readLine()) != null) {
      System.out.println(msg);
      if ("Bye".equals(msg)) {
        break;
      }
      resp = msgHandler(msg);
      writeout.println(resp);
      writeout.flush();
      Thread.sleep(1000); //fake to engage more
    }
    readin.close();
    writeout.close();
    activedSock.close();
  }

  private static String msgHandler(String msg) {
    if (msg.contains("Bye")) {
      return "Bye";
    }
    return "Got it";
  }
}
