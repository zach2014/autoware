package com.tch.gui.page.modal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.javascript.TimeoutError;
import com.tch.common.CPE;
import com.tch.gui.page.main.HomePage;

public class GatewayModal extends Modal {

	public static final String ID_GW_MODAL = "gateway-modal";
	public static final Integer INDEX_GW_MODAL = 0;
	public static final By BY_BTN_SYS_RESET = By.id("btn-system-reset");
	public static final By BY_RESET_MSG = By.id("resetting-msg");
	private static final By BY_BTN_RESTART = By.id("btn-system-reboot");
	private static final By BY_CTLS = By.className("control-group");
	private static final By BY_CTL_LBL = By.className("control-label");
	private static final By BY_CTL_DESC = By.className("simple-desc");
	private static final By BY_FW_UPGRADER = By.id("file-upgradefw");
	private static final By BY_BTN_FW_UPGRADE = By.id("btn-upgrade");
	private static final By BY_CLS_ERROR = By.className("alert-error");
	private static final By BY_TRSF_MSG = By.id("upgrade-transfer-msg");
	private static final By BY_BSY_MSG = By.id("upgrade-busy-msg");

	public GatewayModal(CPE cpe) {
		super(cpe, 0);
		By by_gw_modal = By.id(ID_GW_MODAL);
		try {
			this.page.findElement(by_gw_modal);
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			throw new IllegalStateException(
					"This is not config card page of Gateway!");
		}
	}

	/**
	 * To do factory reset from web GUI, until CPE reboot up
	 * 
	 */
	public HomePage FactoryReset() {
		page.findElement(BY_BTN_SYS_RESET).click();
		HomePage hm = fadeoutModal();
		WebDriverWait reboot = new WebDriverWait(page, Long.parseLong(cpe
				.readProp("CPE.timer.reboot")));
		reboot.pollingEvery(1, TimeUnit.MINUTES);
		reboot.until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				try {
					driver.navigate().refresh();
					return driver.getTitle().equals(cpe.getHPageTitle());
				} catch (Exception e) {
					loger.warn(e.getMessage());
					loger.info("Trying again to connect web page after 1 minute....");
					return false;
				}
			}
		});
		return hm;
	}
	
	public HomePage restart(){
		page.findElement(BY_BTN_RESTART).click();
		HomePage hm = fadeoutModal();
		rebootup(page);
		return hm;
	}
	
	public String getFWVersion() {
		List<WebElement> ctl_group = page.findElements(BY_CTLS);
		String fw_ver = "";
		for(WebElement e: ctl_group){
			if (e.findElement(BY_CTL_LBL).getText().equals("Firmware Version")) {
				fw_ver = e.findElement(BY_CTL_DESC).getText();
				break;
			}
		}
		return fw_ver;
	}
	
	public HomePage upgradeFW(String newBuild) {
		page.findElement(BY_FW_UPGRADER).sendKeys(newBuild);
		page.findElement(BY_BTN_FW_UPGRADE).click();
		// check the progress of upgrade
		
		return fadeoutModal();
	}
	
	private void rebootup(WebDriver page){
		WebDriverWait reboot = new WebDriverWait(page, Long.parseLong(cpe
				.readProp("CPE.timer.reboot")));
		reboot.pollingEvery(1, TimeUnit.MINUTES);
		reboot.until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				try {
					driver.navigate().refresh();
					return driver.getTitle().equals(cpe.getHPageTitle());
				} catch (Exception e) {
					loger.warn(e.getMessage());
					loger.info("Trying again to connect web page after 1 minute....");
					return false;
				}
			}
		});	
	}

}
