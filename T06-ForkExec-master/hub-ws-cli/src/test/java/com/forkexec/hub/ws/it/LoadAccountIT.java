package com.forkexec.hub.ws.it;

import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.forkexec.hub.ws.cli.HubClient;
import com.forkexec.hub.ws.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class LoadAccountIT extends BaseIT{
	private final static String userId = "joao@gmail.com";
	private final static int startPoints = 100;
	private final static int moneyToAdd = 100;
	private final static String creditCard = "0024007102923926";


	@Before
	public void setUp() throws InvalidInitFault_Exception, InvalidUserIdFault_Exception {
		//client.ctrlInitUserPoints(startPoints);
		//client.activateAccount(userId);
	}

	@After
	public void tearDown() {
		//client.ctrlClear();
	}

	@Test
	public void loadAccountTest() throws InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception, InvalidUserIdFault_Exception, EmptyCartFault_Exception, InvalidFoodQuantityFault_Exception, NotEnoughPointsFault_Exception {

		client.loadAccount(userId, moneyToAdd, creditCard);
		client.orderCart(userId);

		assertNotNull(userId);
		assertNotNull(creditCard);

	}
}
