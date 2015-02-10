package com.tch.common;


import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class TestCPE extends TestCase {

	private CPE dut=null;
	
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		dut = new CPE();
		dut.enableService("ssh");
		dut.enableService("web");
		dut.setHostAddr("1.1.1.1");
	}

	@After
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testGetVariant() {
		assertEquals(null, dut.getVariant());
	}

	@Test
	public void testSetHostAddr() {
		assertEquals("1.1.1.1", dut.getHostAddr());
		dut.setHostAddr("192.168.1.1");
		assertEquals("192.168.1.1", dut.getHostAddr());
	}

	@Test
	public void testSetServices() {
		assertEquals(true, dut.isEnabled("ssh"));
		assertEquals(false, dut.isEnabled("ftp"));
		Set<String> svr_set = new HashSet<String>();
		svr_set.add("ftp");
		svr_set.add("tftp");
		dut.setServices(svr_set);
		assertEquals(true, dut.isEnabled("ftp"));
	}

	@Test
	public void testEnableService() {
		assertEquals(false, dut.isEnabled("ftp"));
		dut.enableService("ftp");
		assertEquals(true, dut.isEnabled("ftp"));
	}

	@Test
	public void testDisableService() {
		dut.disableService("ssh");
		assertEquals(false, dut.isEnabled("ssh"));
	}

	@Test
	public void testToString() {
		String str_DUT = dut.toString();
		System.out.println(str_DUT);
		assertEquals(true, str_DUT.contains("1.1.1.1"));
		assertEquals(true, str_DUT.contains("ssh"));
	}

}
