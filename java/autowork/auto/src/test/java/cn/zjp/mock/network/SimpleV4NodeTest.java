package cn.zjp.mock.network;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.javascript.host.dom.Node;

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
		nodeV4 = new SimpleV4Node("en1", true);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		nodeV4.onLink();
	}

}
