package com.tch.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jcraft.jsch.JSchException;

public class CPETest {

	public CPE gw;

	@Before
	public void setUp() throws Exception {
		gw = new CPE();
		System.out.println(gw.toString());
	}

	@After
	public void tearDown() throws Exception {
		gw = null;
	}

	@Test
	public void testOpenWEB() {
		fail("Not yet implemented");
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
		assertEquals(wan_ifname+"=atm_8_35", re);
	}
}
