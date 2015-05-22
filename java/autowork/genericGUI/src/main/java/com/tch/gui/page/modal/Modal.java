/**
 * 
 */
package com.tch.gui.page.modal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.tch.common.CPE;
import com.tch.gui.page.main.HomePage;

/**
 * @author zengjp
 * 
 */
public class Modal extends HomePage {

	protected static final String ID_CLOSE_CONFIG = "close-config";
	protected static final String ID_SAVE_CONFIG = "save-config";
	protected static final String ID_CANCEL_CONFIG = "cancel-config";
	protected static final String CLS_ICON_REFRESH = "icon-refresh";
	protected static final String CLS_ICON_PLUS = "icon-plus-sign";
	protected static final String CLS_ICON_MINUS = "icon-minus-sign";
	protected static final String CLS_ICON_REMOVE = "icon-remove";

	static final Logger loger = LogManager.getLogger(Modal.class.getName());

	public Modal(CPE cpe, Integer id){
		super(cpe);
		try{
			page.findElements(By.className(HomePage.CLS_SMALLCARD)).get(id).click();
		} catch (NoSuchElementException noe){
			loger.error(noe.getMessage());
			throw new IllegalArgumentException("id of modal is out of page");
		}
		By card_modal = By.className("modal");
		try {
			page.findElement(card_modal);
		} catch (NoSuchElementException e) {
			loger.error(e.getMessage());
			throw new IllegalStateException("This is not config card page!");
		}
	}

	public boolean showAdvanced() throws NoSuchElementException,
			ElementNotVisibleException {
		By by_plus_icon = By.className(CLS_ICON_PLUS);
		WebElement icon_plus = page.findElement(by_plus_icon);
		icon_plus.click();
		return true;
	}

	public boolean hideAdvanced() throws NoSuchElementException,
			ElementNotVisibleException {
		By by_icon_minus = By.className(CLS_ICON_MINUS);
		WebElement icon_minus = page.findElement(by_icon_minus);
		icon_minus.click();
		return true;
	}

	public boolean refreshData() throws NoSuchElementException {
		By by_refresh_icon = By.className(CLS_ICON_REFRESH);
		WebElement icon_refresh = page.findElement(by_refresh_icon);
		icon_refresh.click();
		return true;
	}

	public HomePage closeCfgPage() throws NoSuchElementException,
			ElementNotVisibleException {
		By by_btn_close = By.id(ID_CLOSE_CONFIG);
		WebElement btn_close = page.findElement(by_btn_close);
		btn_close.click();
		return this;
	}

	public boolean saveChanges() throws NoSuchElementException,
			ElementNotVisibleException {
		By by_btn_save = By.id(ID_SAVE_CONFIG);
		WebElement btn_save = page.findElement(by_btn_save);
		btn_save.click();
		return true;
	}

	public HomePage cancelChanges() throws NoSuchElementException,
			ElementNotVisibleException {
		By by_btn_cancel = By.id(ID_CANCEL_CONFIG);
		WebElement btn_cancel = page.findElement(by_btn_cancel);
		btn_cancel.click();
		return this;
	}

	public HomePage fadeoutModal() throws NoSuchElementException {
		By by_icon_remove = By.className(CLS_ICON_REMOVE);
		WebElement icon_remove = page.findElement(by_icon_remove);
		icon_remove.click();
		return this;
	}
}
