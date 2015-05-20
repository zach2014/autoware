/**
 * 
 */
package com.tch.gui.page.main;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * @author zengjp
 * 
 */
public class LoginPage {

	protected static final String LOGIN_ERR_MSG = "Invalid Username or Password";
	protected static final String LOGINPAGE_TITLE = "Login";
	protected static final String ID_SIGN_ME_IN = "sign-me-in";
	protected static final String ID_PASSWORD_INPUT = "srp_password";
	protected static final String ID_USERNAME_INPUT = "srp_username";
	protected static final String ID_SRP_ERROR = "erroruserpass";
	private WebDriver browser;

	public LoginPage(WebDriver driver) {
		browser = driver;
		String pageTitle = browser.getTitle();
		if (!LOGINPAGE_TITLE.equalsIgnoreCase(pageTitle)) {
			throw new IllegalStateException("This is not the Login page!");
		}
	}

	public void typeUserName(String usrName) {
		By input_usrName = By.id(ID_USERNAME_INPUT);
		WebElement input_ele = browser.findElement(input_usrName);
		input_ele.clear();
		input_ele.sendKeys(usrName);
	}

	public void typePasswd(String passwd) {
		By input_passwd = By.id(ID_PASSWORD_INPUT);
		WebElement input_ele = browser.findElement(input_passwd);
		input_ele.clear();
		input_ele.sendKeys(passwd);
		// return this;
	}

	protected void signIn() {
		By btn_sign_in = By.id(ID_SIGN_ME_IN);
		browser.findElement(btn_sign_in).click();		
	}

	public boolean login(String usrName, String passwd) throws InterruptedException {
		typeUserName(usrName);
		typePasswd(passwd);
		signIn();
		if(browser.getPageSource().contains("Verifying")){
			Thread.sleep(3000);
		}
		String title = browser.getTitle();
		if(LOGINPAGE_TITLE.equalsIgnoreCase(title)){
			return false;
		}
		if(GatewayHomePage.HOMEPAGE_TITLE.equalsIgnoreCase(title)){
			return true;
		} else {
			throw new IllegalStateException("Login with exception");
		}
	}

	/*
	 * public GatewayHomePage submitLogin() { By btn_submit =
	 * By.id("sign-me-in"); browser.findElement(btn_submit).click(); boolean
	 * isVerifying = true; while (isVerifying) { if
	 * (!browser.getPageSource().contains("Verifying")) isVerifying = false; }
	 * return new GatewayHomePage(browser); }
	 * 
	 * public LoginPage submitLoginFail() { By btn_submit = By.id("sign-me-in");
	 * browser.findElement(btn_submit).click(); return new LoginPage(browser); }
	 */

	public GatewayHomePage cancelLogin() {
		By btn_cancel = By.linkText("Cancel");
		browser.findElement(btn_cancel).click();
		return new GatewayHomePage(browser);
	}

	public WebDriver getBrower() {
		return browser;
	}
	/*
	 * public GatewayHomePage loginAs(String usrName, String passwd) {
	 * typeUserName(usrName); typePasswd(passwd); return submitLogin(); }
	 * 
	 * public LoginPage loginAsFail(String usrName, String passwd) {
	 * typeUserName(usrName); typePasswd(passwd); return submitLoginFail(); }
	 */

}
