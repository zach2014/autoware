/**
 * 
 */
package com.tch.gui.page.main;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
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
	private static final String ID_SIGNOUT_LNK = "signout";
	private static final String LOGGED_STR = "logged";
	public static final String ID_BTN_SIGNIN = "signin";
	public static final String HOMEPAGE_TITLE = "Gateway";
	public static final String CLS_SMALLCARD = "smallcard";
	static final Logger loger = LogManager.getLogger(HomePage.class.getName());
	
	protected final CPE cpe;
	protected WebDriver  page;
	/**
	 * To wait explicitly
	 */
	protected final WebDriverWait waiter;
	
	/**
	 * To check the page if show whether login or not
	 * @param page
	 * @return true/false
	 */
	public static boolean isLogged(WebDriver page){
		if (page.getTitle().equals(CPE.instance.getHPageTitle())) {
			if(page.getPageSource().contains(LOGGED_STR))  return true;
		}
		return false;
	}
	
	/**
	 * if the page is shown with login, do logout; otherwise, do nothing
	 * @param page
	 */
	public static void logout(WebDriver page) {
		if (isLogged(page)){
			By dropDownToggle = By.cssSelector("button.btn.dropdown-toggle");
			By btn_logout = By.id(ID_SIGNOUT_LNK);
			page.findElement(dropDownToggle).click();
			page.findElement(btn_logout).click();
		}
	}
	
	private static void requireLogin(CPE cpe, WebDriver page) {
		// home.url but redirect to login page
		if(page.getTitle().equals(cpe.getLPageTitle()) && page.getCurrentUrl().equals(cpe.getHPageURL())) {
			LoginPage login1StPage = new LoginPage(cpe);
			try {
				login1StPage.login(cpe.getWebUser(), cpe.getWebPasswd());
				page = login1StPage.getPage();
			} catch (InterruptedException e) {
				loger.error("Login process meet interruption");
				e.printStackTrace();
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
	 * @param gw
	 */
	public HomePage(CPE gw) {
		this.cpe = gw;
		this.page = cpe.getWebPage();
		this.waiter = new WebDriverWait(page, Long.parseLong(cpe.readProp("GUI.timer.explicitlyWait")));
		HomePage.requireLogin(cpe, page);
		if (! cpe.getHPageTitle().equalsIgnoreCase(page.getTitle())){
			loger.error("The title of current page is " + page.getTitle() + " not " + cpe.getHPageTitle() );
			throw new IllegalStateException("Not Gateway HomePage as expected");
		} else if (cpe.getHpageCards("guest") > page.findElements(By.className(CLS_SMALLCARD)).size()){
			loger.warn("Can't not load whole homepage");
			throw new IllegalStateException("Need more time for loading page");
		}
	}
	
	/**
	 * Constructor for initiating home page, specify the home page URL
	 * @param gw
	 * @param url
	 */
	public HomePage(CPE gw, String url){
		this.cpe = gw;
		this.page = cpe.getWebPage(url);
		this.waiter = new WebDriverWait(page, Long.parseLong(cpe.readProp("GUI.timer.explicitlyWait")));
		HomePage.requireLogin(cpe, page);
		if (! cpe.getHPageTitle().equalsIgnoreCase(page.getTitle())){
			loger.error("The title of current page is " + page.getTitle() + " not " + cpe.getHPageTitle() );
			throw new IllegalStateException("Not Gateway HomePage as expected");
		} else if (cpe.getHpageCards("guest") > page.findElements(By.className(CLS_SMALLCARD)).size()){
			loger.warn("Can't not load whole homepage");
			throw new IllegalStateException("Need more time for loading page");
		}
	}
	
	
	/**
	 * Constructor for cancel/complete login to home page
	 * @param loginPage
	 */
	public HomePage(LoginPage loginPage) {
		cpe = loginPage.getCPE();
		page = loginPage.getPage();
		this.waiter = new WebDriverWait(page, Long.parseLong(cpe.readProp("GUI.timer.explicitlyWait")));
		if (! cpe.getHPageTitle().equalsIgnoreCase(page.getTitle())){
			loger.error("The title of current page is " + page.getTitle() + " not " + cpe.getHPageTitle() );
			throw new IllegalStateException("Not Gateway HomePage as expected");
		} else if (cpe.getHpageCards("guest") > page.findElements(By.className(CLS_SMALLCARD)).size()){
			loger.warn("Can't not load whole homepage");
			throw new IllegalStateException("Need more time for loading page");
		}
	}

	/**
	 * Constructor for fading out modal configure card to home page
	 * @param modal
	 */
	public HomePage(Modal modal) {
		page = modal.getPage();
		cpe = modal.getCPE();
		this.waiter = new WebDriverWait(page, Long.parseLong(cpe.readProp("GUI.timer.explicitlyWait")));
	}

	public CPE getCPE(){
		return cpe;
	}
	
	public WebDriver getPage(){
		return page;
	}
	
	/**
	 * From HomePage to enter the modal configure page with the given id_card
	 * @param id_card
	 * @return Modal page
	 */
	public Modal enterModal(int id_card){
		Integer crds_max = 0;
		if(isLogged()){
			crds_max = cpe.getHpageCards(CPE.ADMIN_ROLE_NM);
		} else {
			crds_max = cpe.getHpageCards(CPE.GUEST_ROLE_NM);
		}
		if(id_card < crds_max){
			return new Modal(cpe, id_card);
		} else {
			loger.error("Card id is "+id_card + " beyond of maximum limitation.");
			throw new NoSuchElementException("Unexpected index of Modal");
		}
	}
	
	public void changePasswd(){
		if(this.isLogged()){
			// go to change password
		}
		else {
			// do login and then to change
		}
	}
	
	/**
	 * From HomePage to LoginPage, presume current page is not login
	 * @return LoginPage
	 */
	public LoginPage goLogin(){
		if (HomePage.isLogged(page)){
			HomePage.logout(page);
		}
		String url = cpe.getGivenLoginUrl();
		if(url.isEmpty()){
			By btn_sign_in = By.id(ID_BTN_SIGNIN);
			page.findElement(btn_sign_in).click();
			return new LoginPage(this);
		} else {
			return new LoginPage(cpe, url);
		}
	}
	
	private boolean isLogged() {
		return isLogged(this.page);
	}
}
