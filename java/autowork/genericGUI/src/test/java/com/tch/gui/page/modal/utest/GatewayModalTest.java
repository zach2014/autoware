package com.tch.gui.page.modal.utest;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.tch.common.CPE;
import com.tch.gui.page.modal.GatewayModal;

public class GatewayModalTest {
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
	}

	@Test
	public void testCloseCfgPage() {
		assertNotNull(onTest.closeCfgPage());
	}

	@Test
	public void testFadeoutModal() {
		assertNotNull(onTest.fadeoutModal());
	}

}
