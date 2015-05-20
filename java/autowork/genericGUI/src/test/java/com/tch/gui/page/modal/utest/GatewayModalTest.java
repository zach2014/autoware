package com.tch.gui.page.modal.utest;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tch.common.CPE;
import com.tch.gui.page.main.GatewayHomePage;
import com.tch.gui.page.modal.GatewayModal;

public class GatewayModalTest {
	CPE gw;
	public GatewayModal onTest = null;

	@Before
	public void setUp() throws Exception {
		gw = new CPE();
		onTest = new GatewayModal((new GatewayHomePage(gw.getWebPage())).enterModal(0).getBrowser());
	}

	@After
	public void tearDown() throws Exception {
		onTest = null;
		gw.closeWEB();
		
	}

	@Test
	public void testShowAdvanced() {
		assertTrue(onTest.showAdvanced());
		assertTrue(onTest.hideAdvanced());
	}

	@Test
	public void testCloseCfgPage() {
		assertTrue(onTest.closeCfgPage());
	}

	@Test
	public void testFadeoutModal() {
		assertTrue(onTest.fadeoutModal());
	}

}
