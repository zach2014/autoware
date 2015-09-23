package cn.zjp.mock.network;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.pcap4j.util.MacAddress;

public class EthernetHostTest {
	private static Node theNode;
	private static MacAddress mac = MacAddress.getByName("d0:67:e5:33:91:00");
	public static ExpectedException rule = ExpectedException.none();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		rule.expect(InterruptedException.class);
		theNode = new EthernetHost("eth0", mac);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		theNode.shutdown();
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testInitNode() {
		assertNotNull(theNode.getPcapNif());
	}
	
	@Test
	public void testStartNode() throws InterruptedException{
		theNode.start();
		Thread.sleep(10000); // wait 10 seconds for doing capture
		assertTrue(theNode.isCapturing());
	}

}
