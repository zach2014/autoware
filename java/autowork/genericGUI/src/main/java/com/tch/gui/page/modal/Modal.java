/**
 * 
 */
package com.tch.gui.page.modal;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.tch.common.CPE;
import com.tch.gui.page.main.HomePage;

/**
 * @author zengjp
 * 
 */
public class Modal extends HomePage {

	public static final String ID_CLOSE_CONFIG = "close-config";
	public static final String ID_SAVE_CONFIG = "save-config";
	public static final String ID_CANCEL_CONFIG = "cancel-config";
	public static final String CLS_ICON_REFRESH = "icon-refresh";
	public static final String CLS_ICON_PLUS = "icon-plus-sign";
	public static final String CLS_ICON_MINUS = "icon-minus-sign";
	public static final String CLS_ICON_REMOVE = "icon-remove";

	static final Logger loger = LogManager.getLogger(Modal.class.getName());

	public Modal(CPE cpe, Integer id) {
		super(cpe);
		List<WebElement> cards = waiter.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className(HomePage.CLS_SMALLCARD)));
		//page.findElements(By.className(HomePage.CLS_SMALLCARD)).get(id).click();
		cards.get(id).click();
		By card_modal = By.className("modal");
		try {
			//WebDriverWait wait = new WebDriverWait(page, Long.parseLong(cpe.readProp("GUI.timer.explicitlyWait")));
			waiter.until(ExpectedConditions.presenceOfElementLocated(card_modal));
		} catch (TimeoutException toe) {
			loger.error(toe.getMessage());
			throw new IllegalStateException("Enter modal config card with exception");
		}
	}

	/**
	 * To enter another Modal page from one Modal page, back home page firstly.
	 */
	public Modal enterModal(int id_card){
		return fadeoutModal().enterModal(id_card);
	}
	
	public void showAdvanced() throws NoSuchElementException,
			ElementNotVisibleException {
		By by_plus_icon = By.className(CLS_ICON_PLUS);
		WebElement icon_plus = page.findElement(by_plus_icon);
		icon_plus.click();
	}

	public void hideAdvanced() throws NoSuchElementException,
			ElementNotVisibleException {
		By by_icon_minus = By.className(CLS_ICON_MINUS);
		WebElement icon_minus = page.findElement(by_icon_minus);
		icon_minus.click();
	}

	public void refreshData() throws NoSuchElementException {
		By by_refresh_icon = By.className(CLS_ICON_REFRESH);
		WebElement icon_refresh = page.findElement(by_refresh_icon);
		icon_refresh.click();
	}

	public HomePage closeCfgPage() throws NoSuchElementException,
			ElementNotVisibleException {
		By by_btn_close = By.id(ID_CLOSE_CONFIG);
		WebElement btn_close = page.findElement(by_btn_close);
		btn_close.click();
		return new HomePage(this);
	}

	public boolean saveChanges() throws NoSuchElementException,
			ElementNotVisibleException {
		By by_btn_save = By.id(ID_SAVE_CONFIG);
		WebElement btn_save = page.findElement(by_btn_save);
		btn_save.click();
		waiter.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className(CLS_SMALLCARD)));
		return true;
	}

	public HomePage cancelChanges() throws NoSuchElementException,
			ElementNotVisibleException {
		By by_btn_cancel = By.id(ID_CANCEL_CONFIG);
		WebElement btn_cancel = page.findElement(by_btn_cancel);
		btn_cancel.click();
		return new HomePage(this);
	}

	public HomePage fadeoutModal() throws NoSuchElementException {
		By by_icon_remove = By.className(CLS_ICON_REMOVE);
		WebElement icon_remove = page.findElement(by_icon_remove);
		icon_remove.click();
		return new HomePage(this);
	}
}
  