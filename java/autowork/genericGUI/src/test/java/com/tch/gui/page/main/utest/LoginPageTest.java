/**
 * 
 */
package com.tch.gui.page.main.utest;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.tch.gui.page.main.LoginPage;

/**
 * @author zengjp
 *
 */
public class LoginPageTest {
	private static WebDriver driver; 
	private static LoginPage onTest;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		driver  = new FirefoxDriver();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		driver.close();
		driver.quit();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	
	public void setUp() throws Exception {
		driver.get("http://192.168.1.1/login.lp");
		onTest = new LoginPage(driver);
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.tch.gui.page.main.LoginPage#cancelLogin()}.
	 */
	@Test
	public void testCancelLogin() {
		onTest.cancelLogin();
	}

	/**
	 * Test method for {@link com.tch.gui.page.main.LoginPage#loginAs(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testLoginAs() {
		onTest.loginAs("admin", "admin");
	}

	/**
	 * Test method for {@link com.tch.gui.page.main.LoginPage#loginAsExcetion(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testLoginAsExcetion() {
		onTest.loginAsExcetion("admin", "wrong");
	}

}
