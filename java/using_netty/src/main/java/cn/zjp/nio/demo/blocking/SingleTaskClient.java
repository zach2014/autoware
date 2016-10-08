package cn.zjp.nio.demo.blocking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SingleTaskClient {

  public static void main(String[] args) {
    SingleTaskClient clients = new SingleTaskClient();
    try {
      clients.start(new InetSocketAddress(InetAddress.getLocalHost(), 9090));
    } catch (IOException e) {
    }
  }

  public void start(InetSocketAddress servePoint) throws IOException {

    Socket client = new Socket();
    client.connect(servePoint);
    String server = client.getRemoteSocketAddress().toString();
    System.out.println("Connected with: " + server);
    InputStreamReader inbound = new InputStreamReader(client.getInputStream());
    BufferedReader reader = new BufferedReader(inbound);
    OutputStream outbound = client.getOutputStream();
    PrintWriter writer = new PrintWriter(outbound);
    writer.println("Hello, There!");
    writer.flush();

    String msg, resp;
    while ((msg = reader.readLine()) != null) {
      if ("Bye".equals(msg)) {
        break;
      }
      resp = client.toString();
      writer.println(resp);
      writer.flush();
    }
    reader.close();
    writer.close();
    client.close();

  }
}
