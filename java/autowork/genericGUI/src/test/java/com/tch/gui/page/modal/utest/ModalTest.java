package com.tch.gui.page.modal.utest;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.WebDriver;

import com.tch.common.CPE;
import com.tch.gui.page.main.GatewayHomePage;
import com.tch.gui.page.modal.Modal;

public class ModalTest {
	private static CPE gw; 
	GatewayHomePage onTest;
	
	@Rule
	public ExpectedException ex = ExpectedException.none();
	
	@Before
	public void setUp() throws Exception {
		gw = new CPE();
		WebDriver browser = gw.getGWHomePage();
		onTest = new GatewayHomePage(browser);
	}

	@After
	public void tearDown() throws Exception {
		gw.closeWEB();
	}

	@Test
	public void testShowAdvanced() {
		Modal modalP = onTest.enterModal(0);
		assertTrue(modalP.showAdvanced());
		modalP.hideAdvanced();
		assertTrue(modalP.showAdvanced());
		ex.expect(ElementNotVisibleException.class);
		modalP.showAdvanced();
	}

	@Test
	public void testRefreshData() {
		Modal modalP = onTest.enterModal(0);
		assertTrue(modalP.refreshData());
	}

	@Test
	public void testCloseCfgPage() {
		Modal modalP = onTest.enterModal(0);
		assertTrue(modalP.closeCfgPage());
		modalP = onTest.enterModal(1);
		modalP.showAdvanced();
		assertTrue(modalP.closeCfgPage());		
	}

	@Test
	public void testSaveChanges() {
		Modal modalP = onTest.enterModal(0);
		ex.expect(ElementNotVisibleException.class);
		assertTrue(modalP.saveChanges());
	}

	@Test
	public void testCancelChanges() {
		Modal modalP = onTest.enterModal(0);
		ex.expect(ElementNotVisibleException.class);
		assertTrue(modalP.cancelChanges());
	}

}
