/**
 * 
 */
package com.tch.gui.page.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.tch.common.CPE;

/**
 * @author zengjp
 * 
 */
public class LoginPage {

	private static final String EX_MSG_LOGIN_FAIL = "Fail to login with exception";
	public static final String LOGINPAGE_TITLE = "Login";
	protected static final String LOGIN_ERR_MSG = "Invalid Username or Password";
	protected static final String ID_SIGN_ME_IN = "sign-me-in";
	protected static final String ID_PASSWORD_INPUT = "srp_password";
	protected static final String ID_USERNAME_INPUT = "srp_username";
	protected static final String ID_SRP_ERROR = "erroruserpass";

	static final Logger loger = LogManager.getLogger(LoginPage.class.getName());

	private final CPE cpe;
	private WebDriver page;

	public LoginPage(CPE gw) {
		cpe = gw;
		page = gw.getWebPage();
		page.findElement(By.id(HomePage.ID_BTN_SIGNIN)).click();
		if (!cpe.getLPageTitle().equalsIgnoreCase(page.getTitle())) {
			loger.error("The title of current page is " + page.getTitle()
					+ " not " + cpe.getLPageTitle());
			throw new IllegalStateException("Not Gateway LoginPage as expected");
		}
	}

	public LoginPage(CPE gw, String url) {
		cpe = gw;
		page = gw.getWebPage(url);
		if (!cpe.getLPageTitle().equalsIgnoreCase(page.getTitle())) {
			loger.error("The title of current page is " + "\'"
					+ page.getTitle() + "\' not \'" + cpe.getLPageTitle()
					+ "\'");
			throw new IllegalStateException("Not Gateway LoginPage as expected");
		}
	}

	public LoginPage(HomePage homePage) {
		cpe = homePage.getCPE();
		page = homePage.getPage();
		if (!cpe.getLPageTitle().equalsIgnoreCase(page.getTitle())) {
			loger.error("The title of current page is " + "\'"
					+ page.getTitle() + "\' not \'" + cpe.getLPageTitle()
					+ "\'");
			throw new IllegalStateException("Not Gateway LoginPage as expected");
		}
	}

	public CPE getCPE() {
		return cpe;
	}

	public WebDriver getPage() {
		return page;
	}

	public void typeUserName(String usrName) {
		By input_usrName = By.id(ID_USERNAME_INPUT);
		WebElement input_ele = page.findElement(input_usrName);
		input_ele.clear();
		input_ele.sendKeys(usrName);
	}

	public void typePasswd(String passwd) {
		By input_passwd = By.id(ID_PASSWORD_INPUT);
		WebElement input_ele = page.findElement(input_passwd);
		input_ele.clear();
		input_ele.sendKeys(passwd);
	}

	protected void signIn() {
		By btn_sign_in = By.id(ID_SIGN_ME_IN);
		page.findElement(btn_sign_in).click();
	}

	public boolean login(String usrName, String passwd)
			throws InterruptedException {
		typeUserName(usrName);
		typePasswd(passwd);
		signIn();
		if (page.getPageSource().contains("Verifying")) {
			Thread.sleep(3000);
		}

		String title = page.getTitle();
		if (LOGINPAGE_TITLE.equalsIgnoreCase(title)) {
			return false;
		}
		if (HomePage.HOMEPAGE_TITLE.equalsIgnoreCase(title)) {
			return true;
		} else {
			throw new IllegalStateException(EX_MSG_LOGIN_FAIL);
		}
		// use explicit web driver waiter for login checking
		/*
		 * WebDriverWait sleep = new WebDriverWait(page, 30); try { boolean
		 * isTrue =
		 * sleep.until(ExpectedConditions.titleIs(cpe.getHPageTitle())); if
		 * (isTrue) return true; return false; } catch (TimeoutException toe) {
		 * cancelLogin(); throw new IllegalStateException(EX_MSG_LOGIN_FAIL); }
		 */
	}

	public HomePage cancelLogin() {
		By btn_cancel = By.linkText("Cancel");
		page.findElement(btn_cancel).click();
		return new HomePage(this);
	}

}
