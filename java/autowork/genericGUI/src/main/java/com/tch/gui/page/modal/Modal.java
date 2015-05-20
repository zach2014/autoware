/**
 * 
 */
package com.tch.gui.page.modal;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * @author zengjp
 * 
 */
public class Modal {

	protected static final String ID_CLOSE_CONFIG = "close-config";
	protected static final String ID_SAVE_CONFIG = "save-config";
	protected static final String ID_CANCEL_CONFIG = "cancel-config";
	protected static final String CLS_ICON_REFRESH = "icon-refresh";
	protected static final String CLS_ICON_PLUS = "icon-plus-sign";
	protected static final String CLS_ICON_MINUS = "icon-minus-sign";
	protected static final String CLS_ICON_REMOVE = "icon-remove";

	protected final WebDriver browser;

	public Modal(WebDriver driver) {
		this.browser = driver;
		By card_modal = By.className("modal");
		try {
			browser.findElement(card_modal);
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			throw new IllegalStateException("This is not config card page!");
		}
	}

	public boolean showAdvanced() throws NoSuchElementException,
			ElementNotVisibleException {
		By by_plus_icon = By.className(CLS_ICON_PLUS);
		WebElement icon_plus = browser.findElement(by_plus_icon);
		icon_plus.click();
		return true;
	}

	public boolean hideAdvanced() throws NoSuchElementException,
			ElementNotVisibleException {
		By by_icon_minus = By.className(CLS_ICON_MINUS);
		WebElement icon_minus = browser.findElement(by_icon_minus);
		icon_minus.click();
		return true;
	}

	public boolean refreshData() throws NoSuchElementException {
		By by_refresh_icon = By.className(CLS_ICON_REFRESH);
		WebElement icon_refresh = browser.findElement(by_refresh_icon);
		icon_refresh.click();
		return true;
	}

	public boolean closeCfgPage() throws NoSuchElementException,
			ElementNotVisibleException {
		By by_btn_close = By.id(ID_CLOSE_CONFIG);
		WebElement btn_close = browser.findElement(by_btn_close);
		btn_close.click();
		return true;
	}

	public boolean saveChanges() throws NoSuchElementException,
			ElementNotVisibleException {
		By by_btn_save = By.id(ID_SAVE_CONFIG);
		WebElement btn_save = browser.findElement(by_btn_save);
		btn_save.click();
		return true;
	}

	public boolean cancelChanges() throws NoSuchElementException,
			ElementNotVisibleException {
		By by_btn_cancel = By.id(ID_CANCEL_CONFIG);
		WebElement btn_cancel = browser.findElement(by_btn_cancel);
		btn_cancel.click();
		return true;
	}

	public boolean fadeoutModal() throws NoSuchElementException {
		By by_icon_remove = By.className(CLS_ICON_REMOVE);
		WebElement icon_remove = browser.findElement(by_icon_remove);
		icon_remove.click();
		return true;
	}

	public WebDriver getBrowser() {
		return this.browser;
	}

}
