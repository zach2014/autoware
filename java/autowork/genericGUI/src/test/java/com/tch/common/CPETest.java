package com.tch.common;

import static org.junit.Assert.*;

import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class CPETest {

	public CPE gw;
	@Before
	public void setUp() throws Exception {
		gw = new CPE("vant-f");
		gw.setHost("10.0.0.138");
		System.out.println(gw.toString());
	}

	@After
	public void tearDown() throws Exception {
		gw = null;
	}

	@Test
	public void testAddAccount() {
		Account demo = new Account("demo", "demo123");
		gw.addAccount(demo);
		assertTrue(gw.delAccount("demo"));
	}

	@Test
	public void testDelAccount() {
		assertTrue(gw.delAccount(Account.STR_ROOT));
		assertFalse(gw.delAccount(Account.STR_ROOT));
		gw.addAccount(new Account("demo", "demo123"));
		gw.addAccount(new Account("demo1", "demo1123"));
		assertTrue(gw.delAccount("demo"));
	}

	@Test
	public void testOpenWEB() {
		fail("Not yet implemented");
	}

	@Test
	public void testOpenSSH() throws JSchException {
		Account ssh_Account = new Account("root", "root");
		Session ssh = gw.openSSH(ssh_Account, null);
		assertNotNull(ssh);
		ssh.setPassword("root");
		java.util.Hashtable<String, String> config = new java.util.Hashtable<String, String>();
		config.put("StrictHostKeyChecking", "no");
		ssh.setConfig(config);
		ssh.connect();
		Channel executor = ssh.openChannel("exec");
		executor.connect();
		executor.disconnect();
		ssh.disconnect();
	}

}
