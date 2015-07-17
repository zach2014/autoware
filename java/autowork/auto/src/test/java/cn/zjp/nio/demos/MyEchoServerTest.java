/**
 * 
 */
package cn.zjp.nio.demos;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author zach15
 *
 */
public class MyEchoServerTest {
	private MyEchoServer echoServer = null;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		echoServer = new MyEchoServer();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		echoServer = null;
	}

	/**
	 * Test method for {@link cn.zjp.nio.demos.MyEchoServer#work()}.
	 */
	@Test
	public void testWork() {
		try {
			echoServer.work();
		} catch (InterruptedException e) {
			fail(e.getMessage());
		} finally {
			echoServer.shutdown();
		}
	}

}
