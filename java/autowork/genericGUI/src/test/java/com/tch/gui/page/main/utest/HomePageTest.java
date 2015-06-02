package com.tch.gui.page.main.utest;

import static org.junit.Assert.*;

import java.util.NoSuchElementException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.tch.common.CPE;
import com.tch.gui.page.main.HomePage;
import com.tch.gui.page.main.LoginPage;
import com.tch.gui.page.modal.Modal;

public class HomePageTest {
	private static CPE gw;
	private HomePage onTest;

	@Rule
	public ExpectedException ex_rule = ExpectedException.none();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CPE.build();
	}

	@Before
	public void setUp() throws Exception {
		gw = CPE.instance;
		onTest = new HomePage(gw);
	}

	@After
	public void tearDown() throws Exception {
		CPE.reset();
		CPE.build(); // reset CPE to be with default properties
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		gw.closeWEB();
	}

	@Test
	public void testEnterModal() {
		Modal modalP = onTest.enterModal(0);
		assertTrue(onTest.getPage().getTitle().equalsIgnoreCase(HomePage.HOMEPAGE_TITLE));
		onTest = modalP.fadeoutModal();
		assertTrue(onTest.getPage().getTitle().equalsIgnoreCase(HomePage.HOMEPAGE_TITLE));
	}
	
	@Test
	public void testEnterModalException(){
		ex_rule.expect(NoSuchElementException.class);
		onTest.enterModal(100);
	}

	@Test
	public void testLogin() {
		LoginPage loginP = onTest.goLogin();
		assertTrue(loginP.getPage().getTitle().equalsIgnoreCase("Login"));
	}

	@Test
	public void testGoLoginURL() throws Exception {
		CPE.build("/Users/zach15/repos/github/autoware/java/autowork/genericGUI/src/test/resources/demo/cpe.properties");
		LoginPage loginP = onTest.goLogin();
		assertTrue(loginP.getPage().getTitle().equalsIgnoreCase("Login"));
	}

	@Test
	public void testGoInvalidLoginURL() throws Exception {
		CPE.build("/Users/zach15/repos/github/autoware/java/autowork/genericGUI/src/test/resources/demo/cpe_ex.properties");
		ex_rule.expect(IllegalStateException.class);
		LoginPage loginP = onTest.goLogin();
		assertTrue(loginP.getPage().getTitle()
				.equalsIgnoreCase("404 Not Found"));
	}

	@Test
	public void testLogout() throws InterruptedException {
		HomePage.logout(onTest.getPage());
		onTest.goLogin().login("admin", "test");
		HomePage.logout(onTest.getPage());
		assertFalse(HomePage.isLogged(onTest.getPage()));

	}

	@Test
	public void testNoLogout(){
		HomePage.logout(onTest.getPage());
		HomePage.logout(onTest.getPage());
		assertFalse(HomePage.isLogged(onTest.getPage()));
		
	}
}
