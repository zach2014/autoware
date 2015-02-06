package cn.zjp.web.auto.demos;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.Augmenter;


public class ScreenCapturer {

	private static final String HOME_URL = "http://192.168.1.1";

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		WebDriver driver = new FirefoxDriver();
		driver.manage().timeouts().pageLoadTimeout(3, TimeUnit.SECONDS);
		driver.get(HOME_URL);
		
		WebDriver augmentedDriver  = new Augmenter().augment(driver);
		File screenshot = ((TakesScreenshot)augmentedDriver).getScreenshotAs(OutputType.FILE);
		org.apache.commons.io.FileUtils.copyFile(screenshot, new File("/tmp/screenshot.png"), true);
		driver.close();
		augmentedDriver.quit();
		driver.quit();
	}

}
