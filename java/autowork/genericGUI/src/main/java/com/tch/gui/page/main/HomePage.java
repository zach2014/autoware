/**
 * 
 */
package com.tch.gui.page.main;

import java.util.NoSuchElementException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.tch.common.CPE;
import com.tch.gui.page.modal.Modal;


/**
 * @author zengjp
 *
 */
public class HomePage {
	public static final String ID_BTN_SIGNIN = "signin";
	public static final String HOMEPAGE_TITLE = "Gateway";
	public static final String CLS_SMALLCARD = "smallcard";
	static final Logger loger = LogManager.getLogger(HomePage.class.getName());
	
	protected final CPE cpe;
	protected WebDriver  page;
	
	public HomePage(CPE gw){
		this.cpe = gw;
		this.page = cpe.getWebPage();		
		if (! cpe.getHPageTitle().equalsIgnoreCase(page.getTitle())){
			loger.error("The title of current page is " + page.getTitle() + " not " + cpe.getHPageTitle() );
			throw new IllegalStateException("Not Gateway HomePage as expected");
		} else if (cpe.getHpageCards("guest") > page.findElements(By.className(CLS_SMALLCARD)).size()){
			loger.warn("Can't not load whole homepage");
			throw new IllegalStateException("Need more time for loading page");
		}
	}
	
	public HomePage(CPE gw, String url){
		this.cpe = gw;
		this.page = cpe.getWebPage(url);
		if (! cpe.getHPageTitle().equalsIgnoreCase(page.getTitle())){
			loger.error("The title of current page is " + page.getTitle() + " not " + cpe.getHPageTitle() );
			throw new IllegalStateException("Not Gateway HomePage as expected");
		} else if (cpe.getHpageCards("guest") > page.findElements(By.className(CLS_SMALLCARD)).size()){
			loger.warn("Can't not load whole homepage");
			throw new IllegalStateException("Need more time for loading page");
		}
	}
	
	public HomePage(LoginPage loginPage) {
		cpe = loginPage.getCPE();
		page = loginPage.getPage();
		if (! cpe.getHPageTitle().equalsIgnoreCase(page.getTitle())){
			loger.error("The title of current page is " + page.getTitle() + " not " + cpe.getHPageTitle() );
			throw new IllegalStateException("Not Gateway HomePage as expected");
		} else if (cpe.getHpageCards("guest") > page.findElements(By.className(CLS_SMALLCARD)).size()){
			loger.warn("Can't not load whole homepage");
			throw new IllegalStateException("Need more time for loading page");
		}
	}

	public CPE getCPE(){
		return cpe;
	}
	
	public WebDriver getPage(){
		return page;
	}
	
	public Modal enterModal(int id_card){
		Integer crds_max = 0;
		if(isLogged()){
			crds_max = cpe.getHpageCards(CPE.ADMIN_ROLE_NM);
		} else {
			crds_max = cpe.getHpageCards(CPE.GUEST_ROLE_NM);
		}
		if(id_card < crds_max){
			//page.findElements(cardsLocator).get(id_card).click();
			return new Modal(cpe, id_card);
		} else {
			loger.error("Card id is "+id_card + " beyond of maximum limitation.");
			throw new NoSuchElementException("Unexpected index of card");
		}
		
	}
	
	public LoginPage goLogin(){
		String url = cpe.getGivenLoginUrl();
		if(url.isEmpty()){
			By btn_sign_in = By.id(ID_BTN_SIGNIN);
			page.findElement(btn_sign_in).click();
			return new LoginPage(this);
		} else {
			return new LoginPage(cpe, url);
		}
	}
	
	public HomePage logout(){
		if(isLogged()){
			By dropDownToggle = By.cssSelector("button.btn.dropdown-toggle");
			By btn_logout = By.id("signout");
			page.findElement(dropDownToggle).click();
			page.findElement(btn_logout).click();
		}
		return new HomePage(cpe);
	}

	private boolean isLogged() {
		if(page.getPageSource().contains("logged")){
			return true;
		}
		return false;
	}
}
