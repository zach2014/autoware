/**
 * 
 */
package cn.zjp.web.auto.demos;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * @author zengjp
 * 
 */
public class Firefox {

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		WebDriver ff = new FirefoxDriver();
		ff.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
		ff.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

		ff.get("http://192.168.1.1/");
		ff.findElement(By.id("signin")).click();
		WebElement name = ff.findElement(By.id("srp_username"));
		WebElement pass = ff.findElement(By.id("srp_password"));
		WebElement btn_sign = ff.findElement(By.id("sign-me-in"));

		name.clear();
		name.sendKeys("admin");
		pass.clear();
		pass.sendKeys("admin");

		btn_sign.click();
		String home_title = ff.getTitle();
		System.out.println("Title:" + home_title);

		// ff.close();
		// ff.quit();
	}

}
