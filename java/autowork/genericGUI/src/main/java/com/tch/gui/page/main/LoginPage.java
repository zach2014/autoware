/**
 * 
 */
package com.tch.gui.page.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.tch.common.CPE;

/**
 * @author zengjp
 * 
 */
public class LoginPage {

	public static final By BY_CANCEL_LINK = By.linkText("Cancel");
	private static final String EX_MSG_LOGIN_FAIL = "Fail to login with exception";
	protected static final String LOGIN_ERR_MSG = "Invalid Username or Password";
	public static final By BY_SIGN_ME_IN = By.id("sign-me-in");
	public static final By BY_PASSWD_INPUT = By.id("srp_password");
	public static final By BY_USRNM_INPUT = By.id("srp_username");

	static final Logger loger = LogManager.getLogger(LoginPage.class.getName());

	private final CPE cpe;
	private WebDriver page;
	/**
	 * To wait explicitly
	 */
	protected final WebDriverWait waiter;

	public LoginPage(CPE gw) {
		cpe = gw;
		page = gw.getWebPage();
		waiter = new WebDriverWait(page, Long.parseLong(cpe
				.readProp("GUI.timer.explicitlyWait")));
		try {
			waiter.until(
					ExpectedConditions
							.elementToBeClickable(HomePage.BY_BTN_SIGNIN))
					.click();
			if (waiter.until(ExpectedConditions.titleIs(cpe.getLPageTitle()))) {
				loger.debug("Navgate to login page successfully");
			}
		} catch (TimeoutException toe) {
			loger.error("The title of current page is " + page.getTitle()
					+ " not " + cpe.getLPageTitle());
			throw new IllegalStateException(
					"Fail to open LoginPage as expected");
		}
	}

	public LoginPage(CPE gw, String url) {
		cpe = gw;
		page = gw.getWebPage(url);
		waiter = new WebDriverWait(page, Long.parseLong(cpe
				.readProp("GUI.timer.explicitlyWait")));
		try {
			waiter.until(
					ExpectedConditions
							.elementToBeClickable(HomePage.BY_BTN_SIGNIN))
					.click();
			if (waiter.until(ExpectedConditions.titleIs(cpe.getLPageTitle()))) {
				loger.debug("Navgate to login page successfully");
			}
		} catch (TimeoutException toe) {
			loger.error("The title of current page is " + page.getTitle()
					+ " not " + cpe.getLPageTitle());
			throw new IllegalStateException(
					"Fail to open LoginPage as expected");
		}
	}

	public LoginPage(HomePage homePage) {
		cpe = homePage.getCPE();
		page = homePage.getPage();
		waiter = homePage.waiter;
		try {
			waiter.until(
					ExpectedConditions
							.elementToBeClickable(HomePage.BY_BTN_SIGNIN))
					.click();
			if (waiter.until(ExpectedConditions.titleIs(cpe.getLPageTitle()))) {
				loger.debug("Navgate to login page successfully");
			}
		} catch (TimeoutException toe) {
			loger.error("The title of current page is " + page.getTitle()
					+ " not " + cpe.getLPageTitle());
			throw new IllegalStateException(
					"Fail to open LoginPage as expected");
		}
	}

	public CPE getCPE() {
		return cpe;
	}

	public WebDriver getPage() {
		return page;
	}

	public void typeUserName(String usrName) {
		WebElement input_ele = page.findElement(BY_USRNM_INPUT);
		input_ele.clear();
		input_ele.sendKeys(usrName);
	}

	public void typePasswd(String passwd) {
		WebElement input_ele = page.findElement(BY_PASSWD_INPUT);
		input_ele.clear();
		input_ele.sendKeys(passwd);
	}

	protected void signIn() {
		page.findElement(BY_SIGN_ME_IN).click();
	}

	public boolean login(String usrName, String passwd) {
		typeUserName(usrName);
		typePasswd(passwd);
		signIn();
		try {
			WebDriverWait waiter = new WebDriverWait(page, Long.parseLong(cpe
					.readProp("GUI.timer.explicitlyWait")));
			boolean verified = waiter.until(ExpectedConditions
					.invisibilityOfElementWithText(BY_SIGN_ME_IN, "Verifying"));

			/*
			 * boolean verified = waiter.until(new ExpectedCondition<Boolean>()
			 * { public Boolean apply(WebDriver page) { if
			 * (page.getPageSource().contains("Verifying")) { return false; }
			 * return true; } });
			 */

			if (verified) {
				String title = page.getTitle();
				if (cpe.getLPageTitle().equalsIgnoreCase(title)) {
					return false;
				}
				if (cpe.getHPageTitle().equalsIgnoreCase(title)) {
					return true;
				} else {
					throw new IllegalStateException(EX_MSG_LOGIN_FAIL);
				}
			}
		} catch (TimeoutException toe) {
			loger.error("Fail to go verifing for authenticaion");
		}
		return false;

		/*
		 * if (page.getPageSource().contains("Verifying")) { Thread.sleep(3000);
		 * }
		 */

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
		page.findElement(BY_CANCEL_LINK).click();
		return new HomePage(this);
	}

}
