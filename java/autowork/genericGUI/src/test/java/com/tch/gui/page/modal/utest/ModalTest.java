package com.tch.gui.page.modal.utest;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openqa.selenium.ElementNotVisibleException;

import com.tch.common.CPE;
import com.tch.gui.page.main.HomePage;
import com.tch.gui.page.modal.Modal;

public class ModalTest {
	private static CPE gw; 
	Modal onTest;
	
	@Rule
	public ExpectedException ex = ExpectedException.none();
	
	@Before
	public void setUp() throws Exception {
		CPE.build();
		gw = CPE.instance;
		onTest = new Modal(gw, 0);
	}

	@After
	public void tearDown() throws Exception {
		gw.closeWEB();
	}

	@Test
	public void testShowAdvanced() {
		assertTrue(onTest.showAdvanced());
		onTest.hideAdvanced();
		assertTrue(onTest.showAdvanced());
		ex.expect(ElementNotVisibleException.class);
		onTest.showAdvanced();
	}

	@Test
	public void testRefreshData() {
		Modal modalP = onTest.enterModal(0);
		assertTrue(modalP.refreshData());
	}

	@Test
	public void testCloseCfgPage() {
		HomePage home = onTest.closeCfgPage();
		Modal modalP = home.enterModal(0);
		assertTrue(modalP.showAdvanced());		
	}

	@Test
	public void testSaveChanges() {
		Modal modalP = onTest.enterModal(0);
		ex.expect(ElementNotVisibleException.class);
		assertTrue(modalP.saveChanges());
	}

	@Test
	public void testCancelChanges() {
		ex.expect(ElementNotVisibleException.class);
		assertNotNull(onTest.cancelChanges());
	}

}
