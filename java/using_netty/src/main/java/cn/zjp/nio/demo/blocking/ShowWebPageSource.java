package cn.zjp.nio.demo.blocking;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ShowWebPageSource {

  private final ExecutorService exe = Executors.newSingleThreadScheduledExecutor();

  public static void main(String[] args) {
    try {
      URL targetUrl = new URL("http://www.bing.com");
      ShowWebPageSource scaner = new ShowWebPageSource();
      scaner.doShow(targetUrl);
    } catch (MalformedURLException e) {
    }
  }

  public void doShow(final URL url) {
    Callable<List<String>> cc = new Callable<List<String>>() {
      @Override
      public List<String> call() throws Exception {
        ArrayList<String> content = new ArrayList<String>();

//        Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress("edgmnproxy01.eu.thmulti.com", 80));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
          content.add(line);
        }
        return content;
      }
    };
    System.out.println("Start...");
    Future<List<String>> future = exe.submit(cc);
    System.out.println("Wait for show");
    try {
      List<String> lines = future.get(10, TimeUnit.SECONDS);
      for (String line : lines) {
        System.out.println(line);
      }
    } catch (InterruptedException | TimeoutException | ExecutionException e) {
    } finally {
      exe.shutdownNow();
    }
  }
}
