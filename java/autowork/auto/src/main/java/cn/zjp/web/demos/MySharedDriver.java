/**
 * 
 */
package cn.zjp.web.demos;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;

/**
 * @author zach15
 *
 */
public class MySharedDriver extends EventFiringWebDriver {

	private static final WebDriver SHARED_DRIVER = new FirefoxDriver();
	private static final Thread ClOSE_THREAD = new Thread(){
		public void run(){
			SHARED_DRIVER.close();
		}
	};
	private static final Logger loger = LogManager.getLogger(MySharedDriver.class);

	public MySharedDriver() {
		super(SHARED_DRIVER);
	}
	
	@Override
	public void close(){
		if(Thread.currentThread() != ClOSE_THREAD) {
			throw new UnsupportedOperationException("You can't close the shared driver. It close when JVM exit.");
		}
		super.close();
	}
	
	@Before
	public void deleteAllCookie(){
		manage().deleteAllCookies();
	}

	@After
	public void embedScreenshot(Scenario scenario){
		try {
			byte[] screenshot = getScreenshotAs(OutputType.BYTES);
			scenario.embed(screenshot, "image/png");
		} catch (WebDriverException plathformDontSupportScreenshot) {
			loger.warn(plathformDontSupportScreenshot.getMessage());
		}
	}

}
