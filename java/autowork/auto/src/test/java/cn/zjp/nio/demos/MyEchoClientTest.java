/**
 * 
 */
package cn.zjp.nio.demos;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

/**
 * @author zach15
 *
 */
public class MyEchoClientTest {
	private MyEchoClient echoClient = null;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		echoClient = new MyEchoClient();
	}

	@Test
	public void testEcho() {
		try {
			echoClient.echo("localhost", 8090);
		} catch (InterruptedException e) {
			fail(e.getMessage());
		} finally {
			echoClient.shutdown();
		}
	}
}
