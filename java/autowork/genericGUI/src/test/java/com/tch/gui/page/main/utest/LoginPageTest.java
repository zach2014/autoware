/**
 * 
 */
package com.tch.gui.page.main.utest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.tch.common.CPE;
import com.tch.gui.page.main.LoginPage;

/**
 * @author zengjp
 *
 */
public class LoginPageTest {
	private CPE gw; 
	private LoginPage onTest;
	
	@Rule
	public ExpectedException ex = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		CPE.build();
		gw = CPE.instance;
		onTest =  new LoginPage(gw);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		onTest = null;
		gw.closeWEB();
	}

	@Test
	public void testCancelLogin() {
		onTest.cancelLogin();
	}

	@Test
	public void testLogin() throws InterruptedException {
		boolean login_ok = onTest.login("admin", "TF074ZQT");
		assertTrue(login_ok);
	}
	
	@Test
	public void testLoginFail() throws InterruptedException {
		assertFalse(onTest.login("admin", "wrong_passwd"));
	}

}
