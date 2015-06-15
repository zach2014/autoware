package com.tch.gui.page.modal.utest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.tch.common.CPE;
import com.tch.gui.page.main.HomePage;
import com.tch.gui.page.modal.GatewayModal;

public class GatewayModalTest {
	// private static final int ID_TETST_CARD = 0;
	static CPE gw;
	static Properties ddt = new Properties();
	public GatewayModal onTest = null;

	@BeforeClass
	public static void setUpBeforeClass() throws IOException {
		CPE.build();
		gw = CPE.instance;
		ddt.load(GatewayModalTest.class.getClassLoader().getResourceAsStream(
				"ddt.properties"));
	}

	@Before
	public void setUp() throws Exception {
		onTest = new GatewayModal(gw);
	}

	@After
	public void tearDown() throws Exception {
		onTest = null;
	}

	@AfterClass
	public static void tearDownAfterClass() {
		CPE.reset();
	}

	/*
	 * @Test public void testShowAdvanced() { onTest.showAdvanced();
	 * onTest.hideAdvanced(); onTest.refreshData(); }
	 * 
	 * @Test public void testCloseCfgPage() { HomePage hm =
	 * onTest.closeCfgPage(); List<WebElement> cards =
	 * hm.getPage().findElements(HomePage.BY_CLS_SCARD);
	 * assertFalse(cards.isEmpty()); }
	 * 
	 * @Test public void testFadeoutModal() { HomePage hm =
	 * onTest.fadeoutModal(); List<WebElement> cards =
	 * hm.getPage().findElements(HomePage.BY_CLS_SCARD);
	 * assertFalse(cards.isEmpty()); }
	 * 
	 * @Test public void testEnterModal(){ Modal newModal =
	 * onTest.enterModal(ID_TETST_CARD); newModal.showAdvanced();
	 * newModal.hideAdvanced(); HomePage hm = newModal.fadeoutModal();
	 * assertEquals("Back home page as expected", gw.getHPageTitle(),
	 * hm.getPage().getTitle()); }
	 */

	@Test
	public void should_do_factory_reset_by_resetBtn() {
		CPE tmp = onTest.getCPE();
		try {
			HomePage hm = onTest.FactoryReset();
			String expectedTitle = tmp.getHPageTitle();
			String actualTitle = hm.getPage().getTitle();
			assertEquals("Back home page successfully after factory reset",
					expectedTitle, actualTitle);
		} catch (IllegalStateException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void should_do_restart_by_restartBtn() {
		CPE tmp = onTest.getCPE();
		HomePage hm;
		try {
			hm = onTest.restart();
			String expectedTitle = tmp.getHPageTitle();
			String actualTitle = hm.getPage().getTitle();
			assertEquals("Back home page successfully after restart",
					expectedTitle, actualTitle);
		} catch (IllegalStateException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void should_be_upgrade_new_ver() {
		String newBuild = ddt.getProperty("build.valid.new");
		String newVer = ddt.getProperty("upgrader.ver");
		try {
			onTest = new GatewayModal(onTest.upgradeFW(newBuild).getCPE());
			String fwVer = onTest.getFWVersion();
			assertEquals(newVer, fwVer);
		} catch (IllegalStateException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void should_not_be_upgrade_with_InvalidFW() {
		String invalidBuild = ddt.getProperty("build.invalid");
		String fwVer = onTest.getFWVersion();
		onTest = new GatewayModal(onTest.upgradeFW(invalidBuild).getCPE());
		String newVer = onTest.getFWVersion();
		assertEquals(fwVer, newVer);
	}

	@Test
	public void should_present_correct_system_uptime() {
		Long cur = onTest.getUpTime();
		onTest.fadeoutModal();
		onTest = new GatewayModal(onTest.getCPE());
		Long later = onTest.getUpTime();
		assertTrue(cur.compareTo(later) < 0);
	}

	@Test
	public void should_be_able_to_sync_time_2_NTP() {
		onTest.setTimezone("NTP");
		assertTrue(onTest.isSyncNTP());
	}

	@Test
	public void should_be_able_2_custo_time_zone() {
		String tz = ddt.getProperty("CPE.timezone", "UTC");
		onTest.setTimezone(tz);
		assertFalse(onTest.isSyncNTP());
		assertEquals(tz, onTest.getTimezone());
	}

}