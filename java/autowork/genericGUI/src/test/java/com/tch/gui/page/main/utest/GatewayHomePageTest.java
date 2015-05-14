package com.tch.gui.page.main.utest;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.tch.common.CPE;
import com.tch.gui.page.main.GatewayHomePage;

public class GatewayHomePageTest {
	private static CPE gw; 
	private GatewayHomePage onTest;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		gw = new CPE();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		gw.closeWEB();
	}

	@Before
	public void setUp() throws Exception {
		onTest = new GatewayHomePage(gw.getGWHomePage());
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
		onTest.login().loginAs("admin", "TF074ZQT");
		onTest.logout();
	}

}
