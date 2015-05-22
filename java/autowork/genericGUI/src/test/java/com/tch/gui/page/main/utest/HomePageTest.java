package com.tch.gui.page.main.utest;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.tch.common.CPE;
import com.tch.gui.page.main.HomePage;

public class HomePageTest {
	private static CPE gw; 
	private HomePage onTest;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CPE.build();
		gw = CPE.instance;
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		gw.closeWEB();
	}

	@Before
	public void setUp() throws Exception {
		onTest = new HomePage(gw);
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
		onTest.goLogin();
	}

	@Test
	public void testLogout() throws InterruptedException {
		onTest.logout();
		onTest.goLogin().login("admin", "TF074ZQT");
		onTest.logout();
	}

}
