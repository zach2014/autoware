package com.tch.common;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

import com.jcraft.jsch.JSchException;

public class CPETest {
	static Properties ddt = new Properties();
	private CPE gw;

	@Before
	public void setUp() throws Exception {
		ddt.load(CPETest.class.getClassLoader().getResourceAsStream("ddt.properties"));
		CPE.build(ddt.getProperty("utest.spec.prop"));
		gw = CPE.instance;
	}

	@After
	public void tearDown() throws Exception {
		gw = null;
		CPE.reset();
	}

	@Test
	public void should_only_one_inst_global() throws IOException{
		CPE gw1 = CPE.instance;
		assertTrue(gw1 == gw);
		CPE.reset();
		gw1 = CPE.instance;
		assertTrue(gw1 == gw);
		CPE.build();
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
	public void should_open_ssh_session() throws JSchException, IOException {
		assertTrue(gw.openSSH());
		String pass = gw.getWebPasswd();
		assertFalse(pass.isEmpty());
	}

	@Test
	public void should_exec_cli_remote() throws JSchException, IOException {
		String uci_show = "uci show ";
		String wan_ifname = "network.wan.ifname";
		String re = gw.remoteExec(uci_show + wan_ifname);
		assertEquals(wan_ifname+ "=\'" + gw.readProp("CPE.network.wan.if") + "\'", re);
	}	
}
