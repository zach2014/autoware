package cn.zjp.mock.network;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class NodeTest {
	private static Node theNode;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		theNode = new Node("wlan0");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		Thread.sleep(300000);
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
	public void testStartNode(){
		theNode.start();
		assertTrue(theNode.isCapturing());
	}

}
