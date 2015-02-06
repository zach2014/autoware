/**
 * 
 */
package com.tch.gui.page.modal;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

/**
 * @author zengjp
 *
 */
public class Modal {

	private final WebDriver modalP;
	
	public Modal(WebDriver driver) {
		this.modalP = driver;
		//By card_modal = By.cssSelector("div[class='modal fade in']"); 
		By card_modal = By.className("modal");
		try {
			modalP.findElement(card_modal);
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			throw new IllegalStateException("This is not config card page!");
		}
	}

}
