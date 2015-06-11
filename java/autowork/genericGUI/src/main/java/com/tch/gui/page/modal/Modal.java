/**
 * 
 */
package com.tch.gui.page.modal;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
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

	public static final By BY_BTN_CLOSE = By.id("close-config");
	public static final By BY_BTN_SAVE = By.id("save-config");
	public static final By BY_BTN_CANCEL = By.id("cancel-config");
	public static final By BY_ICON_REFRESH = By.className("icon-refresh");
	public static final By BY_ICON_PLUS = By.className("icon-plus-sign");
	public static final By BY_ICON_MINUS = By.className("icon-minus-sign");
	public static final By BY_ICON_REMOVE = By.className("icon-remove");
	public static final By BY_CLS_MDAL_BDY = By.className("modal-body");

	static final Logger loger = LogManager.getLogger(Modal.class.getName());

	public Modal(CPE cpe, Integer id) {
		super(cpe);
		List<WebElement> cards = waiter.until(ExpectedConditions
				.presenceOfAllElementsLocatedBy(HomePage.BY_CLS_SCARD));
		cards.get(id).click();
		By card_modal = By.className("modal");
		try {
			waiter.until(ExpectedConditions
					.presenceOfElementLocated(card_modal));
		} catch (TimeoutException toe) {
			loger.error("The modal configurable card does not fade in as expected");
			throw new IllegalStateException("Fail to enter modal of indexed "
					+ id);
		}
	}

	/**
	 * To enter another Modal page from one Modal page, back home page firstly.
	 */
	@Override
	public Modal enterModal(int id_card) {
		return fadeoutModal().enterModal(id_card);
	}

	public void showAdvanced() {
		waiter.until(ExpectedConditions.visibilityOfElementLocated(BY_ICON_PLUS))
				.click();
	}

	public void hideAdvanced() {
		waiter.until(ExpectedConditions.visibilityOfElementLocated(BY_ICON_MINUS))
				.click();
	}

	public void refreshData() {
		waiter.until(ExpectedConditions.visibilityOfElementLocated(BY_ICON_REFRESH))
				.click();
	}

	public HomePage closeCfgPage() {
		waiter.until(ExpectedConditions.visibilityOfElementLocated(BY_BTN_CLOSE))
				.click();
		return new HomePage(this);
	}

	public boolean saveChanges() {
		waiter.until(ExpectedConditions.visibilityOfElementLocated(BY_BTN_SAVE))
				.click();
		return waiter.until(ExpectedConditions
				.invisibilityOfElementLocated(BY_BTN_SAVE));
	}

	public HomePage cancelChanges() {
		waiter.until(ExpectedConditions.visibilityOfElementLocated(BY_BTN_CANCEL))
				.click();
		return new HomePage(this);
	}

	public HomePage fadeoutModal() {
		waiter.until(ExpectedConditions.visibilityOfElementLocated(BY_ICON_REMOVE))
				.click();
		return new HomePage(this);
	}
}
