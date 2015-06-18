/**
 * 
 */
package com.tch.gui.page.main.telmex.utest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.tch.common.CPE;
import com.tch.gui.page.main.telmex.TelmexHomePage;

/**
 * @author zengjp
 * 
 */
public class TelmexHomePageTest {
	public static CPE gw;
	public static Properties ddt = new Properties();
	static String defPWD = null;
	static String newPWD = "Password";
	private TelmexHomePage onTest;
	

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ddt.load(TelmexHomePageTest.class.getClassLoader().getResourceAsStream(
				"ddt.properties"));
		CPE.build(ddt.getProperty("telmex.utest.prop"));
		gw = CPE.instance;
		defPWD = gw.getWebPasswd();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		CPE.reset();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		onTest = new TelmexHomePage(gw);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link com.tch.gui.page.main.HomePage#isLogged(org.openqa.selenium.WebDriver)}
	 * .
	 */
	@Test
	public void testIsLogged() {
		assertTrue(TelmexHomePage.isLogged(onTest.getPage()));
	}

	/**
	 * Test method for
	 * {@link com.tch.gui.page.main.HomePage#logout(org.openqa.selenium.WebDriver)}
	 * .
	 */
	@Test
	public void testLogout() {
		TelmexHomePage.logout(onTest.getPage());
		assertFalse(TelmexHomePage.isLogged(onTest.getPage()));
	}

	/**
	 * Test method for {@link com.tch.gui.page.main.HomePage#enterModal(int)}.
	 */
	@Test
	public void testEnterModal() {
		assertNotNull(onTest.enterModal(2));
	}

	/**
	 * Test method for {@link com.tch.gui.page.main.HomePage#changePasswd()}.
	 */
	@Test
	public void testChangePasswd() {
			try {
				onTest.changePasswd(defPWD, newPWD);
				onTest = new TelmexHomePage(gw);
				onTest.changePasswd(newPWD, defPWD);
			} catch (Exception e) {
				fail(e.getMessage());
			}
	}

	/**
	 * Test method for {@link com.tch.gui.page.main.HomePage#goLogin()}.
	 */
	@Test
	public void testGoLogin() {
		try {
			assertNotNull(onTest.goLogin());
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
	}

}
