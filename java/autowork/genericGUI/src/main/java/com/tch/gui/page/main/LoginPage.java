/**
 * 
 */
package com.tch.gui.page.main;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


/**
 * @author zengjp
 *
 */
public class LoginPage {

	private WebDriver loginP;
	public LoginPage(WebDriver driver) {
		loginP = driver;
		loginP.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
		loginP.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		String pageTitle = loginP.getTitle();
		if(!"Login".equalsIgnoreCase(pageTitle)){
			throw new IllegalStateException("This is not the Login page!");
		}
	}
	
	public void typeUserName(String usrName){
		By input_usrName = By.id("srp_username");
		WebElement input_ele = loginP.findElement(input_usrName);
		input_ele.clear();
		input_ele.sendKeys(usrName);
		//return this;
	}
	
	public void typePasswd(String passwd){
		By input_passwd = By.id("srp_password");
		WebElement input_ele = loginP.findElement(input_passwd);
		input_ele.clear();
		input_ele.sendKeys(passwd);
		//return this;
	}
	
	public GatewayHomePage submitLogin(){
		By btn_submit = By.id("sign-me-in");
		loginP.findElement(btn_submit).click();
		boolean isVerifying = true;
		while(isVerifying){
			if(!loginP.getPageSource().contains("Verifying")) isVerifying = false;
		}
		return new GatewayHomePage(loginP);
	}
	
	public LoginPage submitLoginException(){
		By btn_submit = By.id("sign-me-in");
		loginP.findElement(btn_submit).click();
		return new LoginPage(loginP);
	}
	
	public GatewayHomePage cancelLogin(){
		By btn_cancel = By.linkText("Cancel");
		loginP.findElement(btn_cancel).click();
		return new GatewayHomePage(loginP);
	}
	public GatewayHomePage loginAs(String usrName, String passwd){
		typeUserName(usrName);
		typePasswd(passwd);
		return submitLogin();		
	}
	
	public LoginPage loginAsExcetion(String usrName, String passwd){
		typeUserName(usrName);
		typePasswd(passwd);
		return submitLoginException();
	}

}
