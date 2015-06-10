package com.tch.gui.page.modal.utest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
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
	
	@BeforeClass
	public static void setUpBeforeClass() throws IOException {
		CPE.build();
		gw = CPE.instance;
	}
	
	@Before
	public void setUp() throws Exception {
		onTest = new Modal(gw, 0);
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
	public void should_expand_infos_by_showAdvanced_btn() {
		onTest.showAdvanced();
		onTest.hideAdvanced();
		onTest.showAdvanced();
		ex.expect(ElementNotVisibleException.class);
		onTest.showAdvanced();
	}

	@Test
	public void should_refresh_modal_config_card() {
		onTest.refreshData();
		HomePage hp = onTest.fadeoutModal();
		onTest = hp.enterModal(0);
		onTest.refreshData();
	}

	@Test
	public void should_be_back_homePage_aftr_close_modal_card() {
		HomePage home = onTest.closeCfgPage();
		Modal modalP = home.enterModal(0);
		modalP.showAdvanced();		
	}

	@Test
	public void should_save_modification_by_save_btn() {
		Modal modalP = onTest.enterModal(0);
		ex.expect(ElementNotVisibleException.class);
		assertTrue(modalP.saveChanges());
	}

	@Test
	public void should_cancel_modification_by_cancel_btn() {
		ex.expect(ElementNotVisibleException.class);
		HomePage hm = onTest.cancelChanges();
		hm.enterModal(1);
		assertNotNull(onTest.cancelChanges());
	}
	
	@Test
	public void should_be_back_homePage_aftr_fadeOut_modal_card(){
		HomePage hm = onTest.fadeoutModal();
		hm.enterModal(1);
		assertNotNull(onTest.fadeoutModal());
	}

}
