package com.tch.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

public class CPETest {
	static Properties ddt = new Properties();
	private CPE gw;

	@Before
	public void setUp() throws Exception {
		ddt.load(CPETest.class.getClassLoader().getResourceAsStream("ddt.properties"));
		CPE.build(ddt.getProperty("dut.utest.prop"));
		gw = CPE.instance;
	}

	@After
	public void tearDown() throws Exception {
		gw = null;
		CPE.reset();
	}

	@Test
	public void should_only_one_inst_global() {
		CPE gw1 = CPE.instance;
		assertTrue(gw1 == gw);
		CPE.reset();
		gw1 = CPE.instance;
		assertTrue(gw1 == gw);
		try {
			CPE.build(ddt.getProperty("dut.def.prop"));
		} catch (IOException e) {
			fail(e.getMessage());
		}
		gw1 = CPE.instance;
		assertTrue(gw1 == gw);		
	}
	
	@Test
	public void should_open_web_page() {
		WebDriver browser = gw.getWebPage();
		String hTitle = browser.getTitle();
		assertEquals("Gateway", hTitle);
		browser.close();
	}
	
	@Test
	public void should_open_ssh_session() {
		try {
			assertTrue(gw.openSSH());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void should_exec_cli_remote() {
		String uci_show = "uci show ";
		String wan_ifname = "network.wan.ifname";
		try {
			String re = gw.remoteExec(uci_show + wan_ifname);
			assertTrue(re.contains(gw.readProp("CPE.network.wan.if")));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}	
}
