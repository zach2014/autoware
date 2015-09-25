package cn.zjp.mock.network;

import static org.junit.Assert.*;

import java.net.Inet4Address;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pcap4j.util.MacAddress;

public class IpV4HostTest {
	
	private static IpV4Host host_v4;
	private static String devName = "eth0";
	private static MacAddress lMac = MacAddress.getByName("d0:67:e5:33:91:01");
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		EthernetHost ethLayer = new EthernetHost(devName, lMac);
		host_v4 = new IpV4Host(ethLayer, (Inet4Address)Inet4Address.getByName("10.11.58.225"));
		host_v4.start();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void hostisCapturingtest() throws InterruptedException {
		Thread.sleep(10000);
		assertTrue(host_v4.isCapturing());
	}

}
