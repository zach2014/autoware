package com.tch.gui.page.modal;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

public class GatewayModal extends Modal {

	private static final String ID_GW_MODAL = "gateway-modal";

	public GatewayModal(WebDriver driver) {
		super(driver);
		By by_gw_modal = By.id(ID_GW_MODAL);
		try {
			this.browser.findElement(by_gw_modal);
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			throw new IllegalStateException("This is not config card page of Gateway!");
		}
	}

}
