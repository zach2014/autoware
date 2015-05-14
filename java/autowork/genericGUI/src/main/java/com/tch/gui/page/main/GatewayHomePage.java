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
	private final WebDriver homeP; 
	
	public GatewayHomePage(WebDriver browser){
		homeP = browser;
		if(!"Gateway".equalsIgnoreCase(homeP.getTitle())){
			throw new IllegalStateException("This is not Gateway home page!");
		}
	}
	
	public Modal enterModal(int id_card){
		By cardsLocator = By.className("smallcard");
		List<WebElement> all_cards_of_home = homeP.findElements(cardsLocator);
		if(id_card >= all_cards_of_home.size()){
			throw new NoSuchElementException("No card with index of "+id_card+" in this page!");
		}
		all_cards_of_home.get(id_card).click();
		return new Modal(homeP);
	}
	
	public LoginPage login(){
		By btn_sign_in = By.id("signin");
		homeP.findElement(btn_sign_in).click();
		return new LoginPage(homeP);
	}
	
	public GatewayHomePage logout(){
		if(isLogged()){
			By dropDownToggle = By.cssSelector("button.btn.dropdown-toggle");
			By btn_logout = By.id("signout");
			homeP.findElement(dropDownToggle).click();
			homeP.findElement(btn_logout).click();
		}
		return new GatewayHomePage(homeP);
	}

	private boolean isLogged() {
		if(homeP.getPageSource().contains("logged")){
			return true;
		}
		return false;
	}

}
