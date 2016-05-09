package cn.zjp.nio.demo.blocking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by zengjp on 16-4-13.
 */
public class SingleTaskClient {

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
            if (msg.equals("Bye")) {
                break;
            }
            //resp = msgHandler(msg);
            resp = client.toString();
            writer.println(resp);
            writer.flush();
        }
        reader.close();
        writer.close();
        client.close();

    }

    private String msgHandler(String msg) throws IOException {
        BufferedReader consoleRead = new BufferedReader(new InputStreamReader(System.in));
        return consoleRead.readLine();
    }

    public static void main(String[] args) {
        SingleTaskClient clients = new SingleTaskClient();
        try {
            clients.start(new InetSocketAddress(InetAddress.getLocalHost(), 9090));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
