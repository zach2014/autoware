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

/**
 * Created by zengjp on 16-4-13.
 */
public class BlockingServer {

    public void serve(int port) throws IOException {
        serve(InetAddress.getLocalHost(), port);
    }

    public void serve(InetAddress host, int port) throws IOException {
        ServerSocket server = new ServerSocket(port, 100, host);
        while(true) {
            Socket activedSock = server.accept();
            System.out.println("Actived Channel info: ");
            InputStream inbound = activedSock.getInputStream();
            OutputStream outbound = activedSock.getOutputStream();
            BufferedReader readin = new BufferedReader(new InputStreamReader(inbound));
            PrintStream writeout = new PrintStream(outbound);
            String msg, resp;
            while((msg = readin.readLine())!= null) {
                System.out.println(msg);
                resp = msgHandler(msg);
                writeout.println(resp);
                writeout.flush();
            }
        }

    }

    private String msgHandler(String msg) {
        if(msg.contains("Bye")) {
            return "Bye";
        }
        return "Got it";
    }

    public static void main(String[] args){
        BlockingServer server = new BlockingServer();
        try {
            server.serve(InetAddress.getByName("192.168.1.2"), 9090);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
