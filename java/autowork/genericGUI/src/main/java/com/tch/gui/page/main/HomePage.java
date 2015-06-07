/**
 * 
 */
package com.tch.gui.page.main;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.jcraft.jsch.JSchException;
import com.tch.common.CPE;
import com.tch.gui.page.modal.Modal;

/**
 * @author zengjp
 *
 */
/**
 * @author zach15
 *
 */
public class HomePage {
	public static final By BY_DRPDWN_TGGL = By
			.cssSelector("button.btn.dropdown-toggle");
	public static final By BY_SIGNOUT_LINK = By.id("signout");
	private static final String LOGGED_STR = "logged";
	public static final By BY_BTN_SIGNIN = By.id("signin");
	public static final By BY_CLS_SCARD = By.className("smallcard");
	static final Logger loger = LogManager.getLogger(HomePage.class.getName());

	protected final CPE cpe;
	protected WebDriver page;
	/**
	 * To wait explicitly
	 */
	protected final WebDriverWait waiter;

	/**
	 * To check the page if show whether login or not
	 * 
	 * @param page
	 * @return true/false
	 */
	public static boolean isLogged(WebDriver page) {
		if (page.getTitle().equals(CPE.instance.getHPageTitle())) {
			if (page.getPageSource().contains(LOGGED_STR))
				return true;
		}
		return false;
	}

	/**
	 * if the page is shown with login, do logout; otherwise, do nothing
	 * 
	 * @param page
	 */
	public static void logout(WebDriver page) {
		if (isLogged(page)) {
			page.findElement(BY_DRPDWN_TGGL).click();
			page.findElement(BY_SIGNOUT_LINK).click();
		}
	}

	/**
	 * To do login firstly for redirect to login page when navigating to home
	 * page
	 * 
	 * @param cpe
	 * @param page
	 */
	private static void requireLogin(CPE cpe, WebDriver page) {
		// home.url but redirect to login page
		if (page.getTitle().equals(cpe.getLPageTitle())
				&& page.getCurrentUrl().equals(cpe.getHPageURL())) {
			LoginPage login1StPage = new LoginPage(cpe);
			try {
				login1StPage.login(cpe.getWebUser(), cpe.getWebPasswd());
				page = login1StPage.getPage();
			} catch (IOException e) {
				loger.error("Read CPE properties file with error");
				e.printStackTrace();
			} catch (JSchException e) {
				loger.error("Remote SSH connection with error");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Constructor for initiating home page
	 * 
	 * @param gw
	 */
	public HomePage(CPE gw) {
		this.cpe = gw;
		this.page = cpe.getWebPage();
		this.waiter = new WebDriverWait(page, Long.parseLong(cpe
				.readProp("GUI.timer.explicitlyWait")));
		HomePage.requireLogin(cpe, page);
		try {
			if (waiter.until(ExpectedConditions.titleIs(cpe.getHPageTitle()))) {
				if (cpe.getHpageCards("guest") > page
						.findElements(BY_CLS_SCARD).size()) {
					loger.warn("Home page loading do NOT complete");
				}
				loger.trace("Home page has been loaded completely");
			}
		} catch (TimeoutException toe) {
			loger.error("The current page is titled \'" + page.getTitle()
					+ "\' not expected \'" + cpe.getHPageTitle() + "\'");
			throw new IllegalStateException("Fail to open HomePage as expected");
		}
	}

	/**
	 * Constructor for initiating home page, specify the home page URL
	 * 
	 * @param gw
	 * @param url
	 */
	public HomePage(CPE gw, String url) {
		this.cpe = gw;
		this.page = cpe.getWebPage(url);
		this.waiter = new WebDriverWait(page, Long.parseLong(cpe
				.readProp("GUI.timer.explicitlyWait")));
		HomePage.requireLogin(cpe, page);
		try {
			if (waiter.until(ExpectedConditions.titleIs(cpe.getHPageTitle()))) {
				if (cpe.getHpageCards("guest") > page
						.findElements(BY_CLS_SCARD).size()) {
					loger.warn("Home page loading do NOT complete");
				}
				loger.trace("Home page has been loaded completely");
			}
		} catch (TimeoutException toe) {
			loger.error("Fail to open home page for titled " + page.getTitle()
					+ " not expected " + cpe.getHPageTitle());
			throw new IllegalStateException("Not Gateway HomePage as expected");
		}
	}

	/**
	 * Constructor for cancel/complete login to home page
	 * 
	 * @param loginPage
	 */
	public HomePage(LoginPage loginPage) {
		cpe = loginPage.getCPE();
		page = loginPage.getPage();
		this.waiter = new WebDriverWait(page, Long.parseLong(cpe
				.readProp("GUI.timer.explicitlyWait")));
		try {
			if (waiter.until(ExpectedConditions.titleIs(cpe.getHPageTitle()))) {
				if (cpe.getHpageCards("guest") > page
						.findElements(BY_CLS_SCARD).size()) {
					loger.warn("Home page loading do NOT complete");
				}
				loger.trace("Home page has been loaded completely");
			}
		} catch (TimeoutException toe) {
			loger.error("Fail to open home page for titled " + page.getTitle()
					+ " not expected " + cpe.getHPageTitle());
			throw new IllegalStateException("Not Gateway HomePage as expected");
		}
	}

	/**
	 * Constructor for fading out modal configure card to home page
	 * 
	 * @param modal
	 */
	public HomePage(Modal modal) {
		page = modal.getPage();
		cpe = modal.getCPE();
		this.waiter = new WebDriverWait(page, Long.parseLong(cpe
				.readProp("GUI.timer.explicitlyWait")));
	}

	public CPE getCPE() {
		return cpe;
	}

	public WebDriver getPage() {
		return page;
	}

	/**
	 * From HomePage to enter the modal configure page with the given id_card
	 * 
	 * @param id_card
	 * @return Modal page
	 */
	public Modal enterModal(int id_card) {
		Integer crds_max = 0;
		if (isLogged()) {
			crds_max = cpe.getHpageCards(CPE.ADMIN_ROLE_NM);
		} else {
			crds_max = cpe.getHpageCards(CPE.GUEST_ROLE_NM);
		}
		if (id_card < crds_max) {
			return new Modal(cpe, id_card);
		} else {
			loger.error("Card id is " + id_card
					+ " beyond of maximum limitation.");
			return null;
		}
	}

	public void changePasswd() {
		if (this.isLogged()) {
			// go to change password
		} else {
			// do login and then to change
		}
	}

	/**
	 * From HomePage to LoginPage, presume current page is not login
	 * 
	 * @return LoginPage
	 */
	public LoginPage goLogin() {
		if (HomePage.isLogged(page)) {
			HomePage.logout(page);
		}
		String url = cpe.getGivenLoginUrl();
		if (url.isEmpty()) {
			page.findElement(BY_BTN_SIGNIN).click();
			return new LoginPage(this);
		} else {
			return new LoginPage(cpe, url);
		}
	}

	private boolean isLogged() {
		return isLogged(this.page);
	}
}
