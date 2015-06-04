package com.tch.gui.page.modal.utest;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.tch.common.CPE;
import com.tch.gui.page.main.HomePage;
import com.tch.gui.page.modal.GatewayModal;
import com.tch.gui.page.modal.Modal;

public class GatewayModalTest {
	private static final int ID_TETST_CARD = 0;
	static CPE gw;
	public GatewayModal onTest = null;

	@BeforeClass
	public static void setUpBeforeClass() throws IOException {
		CPE.build();
		gw = CPE.instance;
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
	public static void tearDownAfterClass(){
		gw.closeWEB();
	}
	@Test
	public void testShowAdvanced() {
		onTest.showAdvanced();
		onTest.hideAdvanced();
		onTest.refreshData();
	}

	@Test
	public void testCloseCfgPage() {
		HomePage hm = onTest.closeCfgPage();
		List<WebElement> cards = hm.getPage().findElements(By.className(HomePage.CLS_SMALLCARD));
		assertFalse(cards.isEmpty());
	}

	@Test
	public void testFadeoutModal() {
		HomePage hm = onTest.fadeoutModal();
		List<WebElement> cards = hm.getPage().findElements(By.className(HomePage.CLS_SMALLCARD));
		assertFalse(cards.isEmpty());
	}
	
	@Test
	public void testEnterModal(){
		Modal newModal = onTest.enterModal(ID_TETST_CARD);
		newModal.showAdvanced();
		newModal.hideAdvanced();
		HomePage hm = newModal.fadeoutModal();
		assertEquals("Back home page as expected", gw.getHPageTitle(), hm.getPage().getTitle());
	}
	
	@Test
	public void testFactoryReset() {
		CPE tmp = onTest.getCPE();
		HomePage hm = onTest.FactoryReset();
		String expectedTitle = tmp.getHPageTitle();
		String actualTitle = hm.getPage().getTitle();
		assertEquals("Back home page successfully after factory reset", expectedTitle, actualTitle);
	}
	
	@Test
	public void testRestart() {
		CPE tmp = onTest.getCPE();
		HomePage hm = onTest.restart();
		String expectedTitle = tmp.getHPageTitle();
		String actualTitle = hm.getPage().getTitle();
		assertEquals("Back home page successfully after restart", expectedTitle, actualTitle);
	}
	
	@Test
	public void testUpgradeFW(){
		String newBuild = "/Users/zach15/Downloads/New-open-VANT-4.rbi";
		String fwVer = onTest.getFWVersion();
		onTest.upgradeFW(newBuild);
		String newVer = onTest.getFWVersion();
		assertNotEquals(fwVer, newVer);
	}
	

}