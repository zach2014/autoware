package com.tch.gui.page.main.utest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

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
	static Properties ddt = new Properties();
	private HomePage onTest;

	@Rule
	public ExpectedException ex_rule = ExpectedException.none();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CPE.build();
		ddt.load(HomePageTest.class.getClassLoader().getResourceAsStream("ddt.properties"));
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
	public void should_fadeIn_modal_config_card() {
		Modal modalP = onTest.enterModal(0);
		assertTrue(onTest.getPage().getTitle().equalsIgnoreCase(gw.getHPageTitle()));
		onTest = modalP.fadeoutModal();
		assertTrue(onTest.getPage().getTitle().equalsIgnoreCase(gw.getHPageTitle()));
	}
	
	@Test
	public void should_do_nothing_4_invalid_card_index(){
		assertNull(onTest.enterModal(100));
	}

	@Test
	public void should_navigate_2_login_page() {
		LoginPage loginP = onTest.goLogin();
		assertTrue(loginP.getPage().getTitle().equalsIgnoreCase("Login"));
	}

	@Test
	public void should_open_login_page_with_url() throws Exception {
		CPE.build(ddt.getProperty("utest.spec.prop"));
		LoginPage loginP = onTest.goLogin();
		assertTrue(loginP.getPage().getTitle().equalsIgnoreCase("Login"));
	}

	@Test
	public void should_not_open_login_page_4_invalid_url() throws Exception {
		CPE.build(ddt.getProperty("utest.ex.prop"));
		ex_rule.expect(IllegalStateException.class);
		LoginPage loginP = onTest.goLogin();
		assertTrue(loginP.getPage().getTitle()
				.equalsIgnoreCase("404 Not Found"));
	}

	@Test
	public void should_be_back_2_home_page_aftr_logpout() throws InterruptedException {
		HomePage.logout(onTest.getPage());
		onTest.goLogin().login("admin", "test");
		HomePage.logout(onTest.getPage());
		assertFalse(HomePage.isLogged(onTest.getPage()));

	}

	@Test
	public void should_no_issue_when_dup_logout(){
		HomePage.logout(onTest.getPage());
		HomePage.logout(onTest.getPage());
		assertFalse(HomePage.isLogged(onTest.getPage()));
		
	}
}
