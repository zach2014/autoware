/**
 * 
 */
package com.tch.gui.page.main.utest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.tch.common.CPE;
import com.tch.gui.page.main.HomePage;
import com.tch.gui.page.main.LoginPage;

/**
 * @author zengjp
 *
 */
public class LoginPageTest {
	private static CPE gw;
	static Properties ddt = new Properties();
	private LoginPage onTest;

	@BeforeClass
	public static void setupBeforeClass() throws Exception {
		ddt.load(HomePageTest.class.getClassLoader().getResourceAsStream("ddt.properties"));
		CPE.build(ddt.getProperty("dut.def.prop"));
		gw = CPE.instance;
	}

	@Before
	public void setUp() throws Exception {
		onTest = new LoginPage(gw);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		HomePage.logout(onTest.getPage());
		onTest = null;
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		gw = null;
		CPE.reset();
	}

	@Test
	public void should_be_back_homePage_4_cancel_login() {
		onTest.cancelLogin();
		String cur_Title = onTest.getPage().getTitle();
		assertFalse(cur_Title.equals(gw.getLPageTitle()));
		assertTrue(cur_Title.equals(gw.getHPageTitle()));
	}

	@Test
	public void should_go_logged_homPage_aftr_login() {
		try{
			assertTrue(onTest.login(gw.getWebUser(), gw.getWebPasswd()));
		} catch(Exception e){
			fail(e.getMessage());
		}
	}

	@Test
	public void should_not_logged_with_invalid_auth(){
		assertFalse(onTest.login("admin", "invalid_passwd"));
	}

}
