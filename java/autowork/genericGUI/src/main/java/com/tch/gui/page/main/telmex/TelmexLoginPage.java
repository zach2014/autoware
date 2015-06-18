/**
 * 
 */
package com.tch.gui.page.main.telmex;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.tch.common.CPE;
import com.tch.gui.page.main.HomePage;
import com.tch.gui.page.main.LoginPage;

/**
 * @author zengjp
 * 
 */
public class TelmexLoginPage extends LoginPage {
	static final Logger loger = LogManager.getLogger(TelmexLoginPage.class
			.getName());

	public TelmexLoginPage(CPE gw) {
		cpe = gw;
		page = gw.getWebPage();
		waiter = new WebDriverWait(page, Long.parseLong(cpe
				.readProp("GUI.timer.explicitlyWait")));
		// make sure not logded in session
		HomePage.logout(page);
		try {
			if (waiter.until(ExpectedConditions.titleIs(cpe.getLPageTitle()))) {
				loger.info("Open login page directly");
			}
		} catch (TimeoutException toe) {
			loger.error("The title of current page is " + page.getTitle()
					+ " not " + cpe.getLPageTitle());
			throw new IllegalStateException("Fail to open login page dirrectly");
		}
	}

	public TelmexLoginPage(CPE gw, String url) {
		super(gw, url);
	}

}
