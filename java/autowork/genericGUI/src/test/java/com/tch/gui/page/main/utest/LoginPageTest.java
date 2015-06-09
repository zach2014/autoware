/**
 * 
 */
package com.tch.gui.page.main.utest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.jcraft.jsch.JSchException;
import com.tch.common.CPE;
import com.tch.gui.page.main.HomePage;
import com.tch.gui.page.main.LoginPage;

/**
 * @author zengjp
 *
 */
public class LoginPageTest {
	private static CPE gw;
	private LoginPage onTest;

	@Rule
	public ExpectedException ex = ExpectedException.none();

	@BeforeClass
	public static void setupBeforeClass() throws Exception {
		CPE.build();
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
		gw.closeWEB();
		gw.closeSSH();
	}

	@Test
	public void testCancelLogin() {
		onTest.cancelLogin();
		String cur_Title = onTest.getPage().getTitle();
		assertFalse(cur_Title.equals(gw.getLPageTitle()));
		assertTrue(cur_Title.equals(gw.getHPageTitle()));
	}

	@Test
	public void testLogin() throws InterruptedException, IOException,
			JSchException {
		assertTrue(onTest.login(gw.getWebUser(), gw.getWebPasswd()));
	}

	@Test
	public void testLoginFail() throws InterruptedException {
		assertFalse(onTest.login("admin", "invalid_passwd"));
	}

}
