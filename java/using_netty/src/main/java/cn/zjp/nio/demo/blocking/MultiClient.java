package cn.zjp.nio.demo.blocking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by zengjp on 16-4-13.
 */
public class MultiClient {

    public void start(InetSocketAddress servePoint, int multiple) throws IOException {
        while (multiple > 0) {
            Socket client = new Socket();
            client.connect(servePoint);
            InputStreamReader inbound = new InputStreamReader(client.getInputStream());
            BufferedReader reader = new BufferedReader(inbound);
            OutputStream outbound = client.getOutputStream();
            PrintWriter writer = new PrintWriter(outbound);

            writer.println("Hello, There! I am #" + multiple);
            writer.flush();
            String msg, resp;
            while ((msg = reader.readLine()) != null) {
                resp = msgHandler(msg);
                writer.println(resp);
                writer.flush();

            }
            multiple--;
        }
    }

    private String msgHandler(String msg) throws IOException {
        if(msg.equals("Got it")) {
            BufferedReader consoleRead = new BufferedReader(new InputStreamReader(System.in));
            String typedMsg;
            if((typedMsg = consoleRead.readLine()) != null){
                return typedMsg;
            }
            else {
                return "please wait...";
            }
        }
        else {
            return "Bye";
        }
    }

    public static void main(String[] args) {
        MultiClient clients = new MultiClient();
        try {
            clients.start(new InetSocketAddress("192.168.1.2", 9090), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
