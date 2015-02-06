package com.tch.gui.page.main.utest;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.tch.gui.page.main.GatewayHomePage;

public class GatewayHomePageTest {
	private static WebDriver driver; 
	private GatewayHomePage onTest;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		driver = new FirefoxDriver();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		driver.close();
		driver.quit();
	}

	@Before
	public void setUp() throws Exception {
		driver.get("http://192.168.1.1");
		onTest = new GatewayHomePage(driver);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testEnterModal() {
		onTest.enterModal(0);
	}

	@Test
	public void testLogin() {
		onTest.login();
	}

	@Test
	public void testLogout() {
		onTest.logout();
		onTest.login().loginAs("admin", "admin");
		onTest.logout();
	}

}
