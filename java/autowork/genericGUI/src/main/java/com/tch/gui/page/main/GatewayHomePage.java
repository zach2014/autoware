/**
 * 
 */
package com.tch.gui.page.main;

import java.util.List;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.tch.gui.page.modal.Modal;


/**
 * @author zengjp
 *
 */
public class GatewayHomePage {
	protected static final String HOMEPAGE_TITLE = "Gateway";
	protected static final String CLS_SMALLCARD = "smallcard";
	private final WebDriver browser; 
	
	public GatewayHomePage(WebDriver driver){
		browser = driver;
		if(!HOMEPAGE_TITLE.equalsIgnoreCase(browser.getTitle())){
			throw new IllegalStateException("This is not Gateway home page!");
		}
	}
	
	public Modal enterModal(int id_card){
		By cardsLocator = By.className(CLS_SMALLCARD);
		List<WebElement> all_cards_of_home = browser.findElements(cardsLocator);
		if(id_card >= all_cards_of_home.size()){
			throw new NoSuchElementException("No card with index of "+id_card+" in this page!");
		}
		all_cards_of_home.get(id_card).click();
		return new Modal(browser);
	}
	
	public LoginPage goLogin(){
		By btn_sign_in = By.id("signin");
		browser.findElement(btn_sign_in).click();
		return new LoginPage(browser);
	}
	
	public GatewayHomePage logout(){
		if(isLogged()){
			By dropDownToggle = By.cssSelector("button.btn.dropdown-toggle");
			By btn_logout = By.id("signout");
			browser.findElement(dropDownToggle).click();
			browser.findElement(btn_logout).click();
		}
		return new GatewayHomePage(browser);
	}

	private boolean isLogged() {
		if(browser.getPageSource().contains("logged")){
			return true;
		}
		return false;
	}
	
	public WebDriver getBrowser(){
		return browser;
	}

}
