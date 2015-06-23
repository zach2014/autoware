package com.tch.gui.page.modal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.tch.common.CPE;
import com.tch.gui.page.main.HomePage;

public class GatewayModal extends Modal {

	public static final By BY_GW_MODAL = By.id("gateway-modal");
	public static final Integer INDEX_GW_MODAL = 0;
	public static final By BY_BTN_SYS_RESET = By.id("btn-system-reset");
	public static final By BY_RESET_MSG = By.id("resetting-msg");
	private static final By BY_BTN_RESTART = By.id("btn-system-reboot");
	private static final By BY_CTLS = By.className("control-group");
	private static final By BY_CTL_LBL = By.className("control-label");
	private static final By BY_CTL_DESC = By.className("simple-desc");
	private static final By BY_FW_UPGRADER = By.id("file-upgradefw");
	private static final By BY_BTN_FW_UPGRADE = By.id("btn-upgrade");
	private static final By BY_TRSF_MSG = By.id("upgrade-transfer-msg");
	private static final By BY_BSY_MSG = By.id("upgrade-busy-msg");
	private static final By BY_NO_FILE_ERR = By.id("upgrade-nofile-msg");
	private static final By BY_INVALID_FILE_ERR = By
			.id("upgrade-wrong-ext-msg");
	private static final By BY_TOO_BIG_ERR = By.id("upgrade-too-big-msg");
	private static final By BY_FAIL_ERR = By.id("upgrade-failed-msg");
	private static final By BY_NTP = By.name("system_network_timezone");
	private static final By BY_TZ_DROP = By.name("system_timezone");

	public GatewayModal(CPE cpe) {
		super(cpe, 0);
		try {
			waiter.until(ExpectedConditions
					.visibilityOfElementLocated(BY_GW_MODAL));
		} catch (TimeoutException toe) {
			loger.error("Gateway modal body is not visible as expected");
			throw new IllegalStateException(
					"Fail to load configurable card of Gateway modal");
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

	public HomePage restart() {
		page.findElement(BY_BTN_RESTART).click();
		HomePage hm = fadeoutModal();
		rebootup(page);
		return hm;
	}

	public String getFWVersion() {
		String verLBLName = cpe.readProp("GUI.GW.verlbl");
		if(null == verLBLName) verLBLName = "Firmware Version"; // base generic build
		List<WebElement> ctl_group = page.findElements(BY_CTLS);
		String fw_ver = "";
		for (WebElement e : ctl_group) {
			if (e.findElement(BY_CTL_LBL).getText().equals(verLBLName)) {
				fw_ver = e.findElement(BY_CTL_DESC).getText();
				break;
			}
		}
		return fw_ver;
	}

	public Long getUpTime() {
		List<WebElement> ctl_group = page.findElements(BY_CTLS);
		Long upTime = null;
		for (WebElement e : ctl_group) {
			if (e.findElement(BY_CTL_LBL).getText().equals("Uptime")) {
				upTime = textToSecs(e.findElement(BY_CTL_DESC).getText());
				break;
			}
		}
		return upTime;
	}

	public boolean isSyncNTP() {
		WebElement box = locateTimeBox();
		if (null == box)
			return false;
		return box.isSelected();
	}

	/**
	 * To get Current Timezone info
	 * 
	 * @return
	 */
	public String getTimezone() {
		List<WebElement> ctl_group = page.findElements(BY_CTLS);
		String timeZone = null;
		if (isSyncNTP()) {
			for (WebElement e : ctl_group) {
				if (e.findElement(BY_CTL_LBL).getText()
						.equals("Current Timezone")) {
					timeZone = e.findElement(BY_CTL_DESC).getText();
					break;
				}
			}
			return timeZone;
		}

		Select tzs = new Select(waiter.until(ExpectedConditions
				.presenceOfElementLocated(BY_TZ_DROP)));
		return tzs.getFirstSelectedOption().getText();
	}

	/**
	 * To set system time zone by the specific name of zone, NTP is for syncing
	 * time with NTP
	 * 
	 * @param zoneName
	 */
	public void setTimezone(String zoneName) {
		if (zoneName.equals("NTP")) {
			// set time to sycn NTP server
			WebElement sycnNTP = locateTimeBox();
			if (null != sycnNTP) {
				if (!sycnNTP.isSelected())
					sycnNTP.click();
				loger.info("System Time is syncing with " + zoneName);
			}
		} else {
			if (isSyncNTP()) {
				loger.debug("System time is sycned with NTP, trying to unSycn");
				locateTimeBox().click();
			}
			// set time zone from drop-down list
			if (!getTimezone().equals(zoneName)) {
				try {
					Select tzs = new Select(waiter.until(ExpectedConditions
							.presenceOfElementLocated(BY_TZ_DROP)));
					tzs.selectByVisibleText(zoneName);
				} catch (TimeoutException toe) {
					loger.error("Fail to find the 'Timezone' drop-down list");
				}
				if (!saveChanges()) {
					loger.error("Fail to save teh change on Timezone settings");
					cancelChanges();
				}
			}
		}
		loger.info("Current Timezone is " + zoneName);
	}

	/**
	 * To trigger firmware upgrade from web page, presume format of build is
	 * correct.
	 * 
	 * @param newBuild
	 * @return HomePage
	 */
	public HomePage upgradeFW(String newBuild) {
		page.findElement(BY_FW_UPGRADER).sendKeys(newBuild);
		page.findElement(BY_BTN_FW_UPGRADE).click();
		loger.info("Upgrade: Build path is " + newBuild);
		List<WebElement> errors = new ArrayList<WebElement>();

		// check transfer message
		try {
			loger.info("Upgrade: "
					+ waiter.until(
							ExpectedConditions
									.visibilityOfElementLocated(BY_TRSF_MSG))
							.getText());
		} catch (TimeoutException toe) {
			loger.warn("UPgrade: No see transfer message in Firmware upgrade.");
		}
		// check busy message
		try {
			loger.info("Upgrade: "
					+ waiter.until(
							ExpectedConditions
									.visibilityOfElementLocated(BY_BSY_MSG))
							.getText());
		} catch (TimeoutException toe) {
			loger.warn("Upgrade: No see busy message in Firmware upgrade.");
		}
		// check kinds of error message
		try {
			// polling upgrade-nofile-msg
			errors.add(waiter.until(ExpectedConditions
					.visibilityOfElementLocated(BY_NO_FILE_ERR)));
		} catch (TimeoutException toe) {
			// set build file as expected for no that error
			loger.info("Upgrade: No see error message for NOFILE to use");
		}
		try {
			// polling upgrade-wrong-ext-msg
			errors.add(waiter.until(ExpectedConditions
					.visibilityOfElementLocated(BY_INVALID_FILE_ERR)));
		} catch (TimeoutException toe) {
			// pass to check the build file format
			loger.info("Upgrade: No see error message for INVALID_FILE to use");
		}
		try {
			// polling upgrade-too-big-msg
			errors.add(waiter.until(ExpectedConditions
					.visibilityOfElementLocated(BY_TOO_BIG_ERR)));
		} catch (TimeoutException toe) {
			// pass to check the build file size
			loger.info("Upgrade: No see error message for TOO_BIG_FILE to use");
		}
		try {
			// polling upgrade-fail-msg
			errors.add(waiter.until(ExpectedConditions
					.visibilityOfElementLocated(BY_FAIL_ERR)));
		} catch (TimeoutException toe) {
			// pass all check to be doing upgrade
			loger.info("Upgrade: No see any failed message in upgrade");
		}
		if (errors.isEmpty()) {
			// so far, no error on upgrade, upgrade may be in progress
			// check even no error message after 1 minute
			try {
				if (null != (new WebDriverWait(page, 60))
						.until(ExpectedConditions
								.visibilityOfElementLocated(BY_FAIL_ERR))) {
					loger.error("Upgrade: See failure in upgrade when double check after minute");
					return fadeoutModal();
				}
			} catch (TimeoutException toe2) {
				loger.info("Upgrade: Pass to check error again, be sure upgrade is in progress as expected.");
			}
			/*
			 * HomePage hm = fadeoutModal(); use too much time to polling
			 * messages, so can not use fadeoutModal to back home page, just
			 * wait after reboot to re-construct home page
			 */
			// handle system rebooting
			rebootup(page);
			return new HomePage(this);
		} else {
			// had see error message, upgrade is in interruption
			for (WebElement e : errors) {
				loger.error(e.getText());
			}
			return fadeoutModal();
		}
	}

	/**
	 * To parse the uptime from text to seconds in Long
	 * 
	 * @param text
	 * @return
	 */
	private Long textToSecs(String text) {
		// the format like 1days 1hours 20min 20sec
		String[] items = (text.replaceAll("sec", "")).split("\\D+ ");

		if (3 < items.length) {
			long y = 0L;
			long d = 0L;
			long h = 0L;
			long m = 0L;
			long s = 0L;
			// like 1years 1days 1hours 1min 20sec
			if (items.length == 5) {
				y = Long.parseLong(items[0]) * 365 * 24 * 3600;
				d = Long.parseLong(items[1]) * 24 * 3600;
				h = Long.parseLong(items[2]) * 3600;
				m = Long.parseLong(items[3]) * 60;
				s = Long.parseLong(items[4]);
			} else {
				// 1days 1hours 1min 20sec
				d = Long.parseLong(items[0]) * 24 * 3600;
				h = Long.parseLong(items[1]) * 3600;
				m = Long.parseLong(items[2]) * 60;
				s = Long.parseLong(items[3]);
			}
			return y + d + h + m + s;
		}
		// like 1hours 1min 20sec
		Integer times = items.length - 1;
		Long sec = 0L;
		for (String i : items) {
			sec = (long) (sec + Long.parseLong(i) * Math.pow(60L, times));
			times--;
		}
		return sec;
	}

	/**
	 * To polling the system startup until home page is refreshed
	 * 
	 * @param page
	 */
	private void rebootup(WebDriver page) {
		String bootingWait = cpe.readProp("CPE.timer.reboot");
		loger.info("CPE is rebooting up for " + bootingWait + " seconds");
		WebDriverWait reboot = new WebDriverWait(page,
				Long.parseLong(bootingWait));
		reboot.pollingEvery(1, TimeUnit.MINUTES);
		reboot.until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				try {
					driver.navigate().refresh();
					return driver.getTitle().equals(cpe.getHPageTitle());
				} catch (Exception e) {
					loger.warn("CPE still be rebooting...");
					loger.info("Trying again to connect web page after 1 minute....");
					return false;
				}
			}
		});
	}

	/**
	 * To locate the specific check-box which is visible to check
	 * 
	 * @return webelement
	 */
	private WebElement locateTimeBox() {
		try {
			List<WebElement> boxes = waiter.until(ExpectedConditions
					.presenceOfAllElementsLocatedBy(BY_NTP));
			for (WebElement e : boxes) {
				if (e.getAttribute("class").equals("monitor-changes")
						&& e.getAttribute("value").equals("_TRUE_")) {
					return e;
				}
			}
			loger.error("Fail to locale matched box");
			return null;
		} catch (TimeoutException toe) {
			loger.error("Fail to find the 'Network Timezone' box to check");
			return null;
		}
	}

}
