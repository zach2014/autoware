package cn.zjp.web.auto.demos;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.Proxy.ProxyType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;


public class AdvancedSelenium {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		Proxy proxy  = new Proxy();
		proxy.setProxyType(ProxyType.AUTODETECT);
		DesiredCapabilities cap = new 	DesiredCapabilities();
		cap.setCapability(CapabilityType.PROXY, proxy);
		WebDriver ff = new FirefoxDriver(cap);
		ff.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
		ff.get("http://www.bing.com");
		
		if(ff.getTitle().startsWith("Bing")) {
			WebDriver augmentDriver = new Augmenter().augment(ff);
			File screenshot = ((TakesScreenshot)augmentDriver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(screenshot, new File("/tmp/screencap.png"), true);
		}
		
		ff.close();
		ff.quit();
	}

}
