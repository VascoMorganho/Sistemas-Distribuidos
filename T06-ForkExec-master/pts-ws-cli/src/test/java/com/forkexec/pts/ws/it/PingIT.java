package com.forkexec.pts.ws.it;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.Properties;

import org.junit.Test;

import com.forkexec.pts.ws.*;

/**
 * Class that tests Ping operation
 */
public class PingIT extends BaseIT {

	// tests
	// assertEquals(expected, actual);

	// public String ping(String x)

	@Test
	public void pingEmptyTest() {
		assertNotNull(client.ctrlPing("test"));
	}

	@Test
	public void Test() throws InvalidEmailFault_Exception, InvalidPointsFault_Exception, NotEnoughBalanceFault_Exception{
		//client.activateAccount("ola@gmail.com");
		client.setPoints("ola@gmail.com", 50, 0);
		assertTrue(client.getPoints("ola@gmail.com").getPoints() ==50);
	}

}
