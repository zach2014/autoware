package com.tch.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

import com.jcraft.jsch.JSchException;

public class CPETest {

	private CPE gw;

	@Before
	public void setUp() throws Exception {
		CPE.build("/Users/zach15/repos/github/autoware/java/autowork/genericGUI/src/test/resources/demo/cpe.properties");
		gw = CPE.instance;
	}

	@After
	public void tearDown() throws Exception {
		gw = null;
		CPE.reset();
	}

	@Test
	public void testSingleton() throws IOException{
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
	public void testOpenWEB() {
		WebDriver browser = gw.getWebPage();
		String hTitle = browser.getTitle();
		assertEquals("Gateway", hTitle);
		browser.close();
	}
	
	@Test
	public void testOpenSSH() throws JSchException {
		assertTrue(gw.openSSH());
	}

	@Test
	public void testCMD() throws JSchException, IOException {
		String uci_show = "uci show ";
		String wan_ifname = "network.wan.ifname";
		String re = gw.remoteExec(uci_show + wan_ifname);
		assertEquals(wan_ifname+"=\'eth4\'", re);
	}	
}
