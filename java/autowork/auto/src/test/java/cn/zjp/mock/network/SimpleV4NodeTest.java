package cn.zjp.mock.network;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SimpleV4NodeTest {
	private SimpleV4Node nodeV4;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		nodeV4 = new SimpleV4Node("192.168.1.254", "192.168.1.222", "22:22:22:22:22:22");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		nodeV4.attach("eth1");
		nodeV4.resolveNextHop();
		nodeV4.echoNextHop();
	}

}
