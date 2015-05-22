package com.tch.gui.page.modal;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

import com.tch.common.CPE;

public class GatewayModal extends Modal {

	private static final String ID_GW_MODAL = "gateway-modal";
	public static final Integer INDEX_GW_MODAL = 0;

	public GatewayModal(CPE cpe) {
		super(cpe, 0);
		By by_gw_modal = By.id(ID_GW_MODAL);
		try {
			this.page.findElement(by_gw_modal);
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			throw new IllegalStateException("This is not config card page of Gateway!");
		}
	}

}
