package com.forkexec.pts.domain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import com.forkexec.pts.domain.exception.EmailAlreadyExistsFaultException;
import com.forkexec.pts.domain.exception.InvalidEmailFaultException;
import com.forkexec.pts.domain.exception.InvalidPointsFaultException;
import com.forkexec.pts.domain.exception.NotEnoughBalanceFaultException;

/**
 * Points
 * <p>
 * A points server.
 */
public class Points {

	/**
	 * Constant representing the default initial balance for every new client
	 */
	private static final int DEFAULT_INITIAL_BALANCE = 100;

	/**
	 * Global with the current value for the initial balance of every new client
	 */
	private final AtomicInteger initialBalance = new AtomicInteger(DEFAULT_INITIAL_BALANCE);

	/**
	 * Accounts. Associates the user's email with a points balance. The collection
	 * uses a hash table supporting full concurrency of retrievals and updates. Each
	 * item is an AtomicInteger, a lock-free thread-safe single variable. This means
	 * that multiple threads can update this variable concurrently with correct
	 * synchronization.
	 */
	private Map<String, AtomicInteger> accounts = new ConcurrentHashMap<>();

	private AtomicInteger tag = new AtomicInteger(0);

	// Singleton -------------------------------------------------------------

	/**
	 * SingletonHolder is loaded on the first execution of Singleton.getInstance()
	 * or the first access to SingletonHolder.INSTANCE, not before.
	 */
	private static class SingletonHolder {
		private static final Points INSTANCE = new Points();
	}

	/**
	 * Retrieve single instance of class. Only method where 'synchronized' is used.
	 */
	public static synchronized Points getInstance() {
		return SingletonHolder.INSTANCE;
	}

	/**
	 * Private constructor prevents instantiation from other classes.
	 */
	private Points() {
		// initialization with default values
		reset();
	}

	/**
	 * Reset accounts. Synchronized is not required because we are using concurrent
	 * map and atomic integer.
	 */
	public void reset() {
		// clear current hash map
		accounts.clear();
		// set initial balance to default
		initialBalance.set(DEFAULT_INITIAL_BALANCE);
	}

	/**
	 * Set initial Reset accounts. Synchronized is not required because we are using
	 * atomic integer.
	 */
	public void setInitialBalance(int newInitialBalance) {
		initialBalance.set(newInitialBalance);
	}

	/** Access points for account. Throws exception if it does not exist. */
	public PV getPoints(final String accountId) throws InvalidEmailFaultException {
		if (!accounts.containsKey(accountId)){
			accounts.put(accountId,new AtomicInteger(DEFAULT_INITIAL_BALANCE));
		}
		final PV points = new PV(accounts.get(accountId).get(), tag.get());
		System.out.println("USER:  " + accountId+ "   POINTS:   " + points.getPoints()+ "    TAG:   "+ points.getTag());
		return points;
	}

	/**
	 * Access points for account. Throws exception if email is invalid or account
	 * does not exist.
	 */
	public PV getAccountPoints(final String accountId) throws InvalidEmailFaultException {
		checkValidEmail(accountId);
		return getPoints(accountId);
	}

	/** Email address validation. */
	private void checkValidEmail(final String emailAddress) throws InvalidEmailFaultException {
		final String message;
		if (emailAddress == null) {
			message = "Null email is not valid";
		} else if (!Pattern.matches("(\\w\\.?)*\\w+@\\w+(\\.?\\w)*", emailAddress)) {
			message = String.format("Email: %s is not valid", emailAddress);
		} else {
			return;
		}
		throw new InvalidEmailFaultException(message);
	}

	/** Initialize account. */
	public void initAccount(final String accountId)
			throws EmailAlreadyExistsFaultException, InvalidEmailFaultException {
		checkValidEmail(accountId);
		if (accounts.containsKey(accountId)) {
			final String message = String.format("Account with email: %s already exists", accountId);
			throw new EmailAlreadyExistsFaultException(message);
		}
		AtomicInteger points = accounts.get(accountId);
		if (points == null) {
			points = new AtomicInteger(initialBalance.get());
			accounts.put(accountId, points);
		}
	}

	/** Add points to account. */
	public void setPoints(final String accountId, final int pointsToAdd, final int tag)
			throws InvalidPointsFaultException, InvalidEmailFaultException {
		checkValidEmail(accountId);
		if (accounts.get(accountId)==null){
			accounts.put(accountId,new AtomicInteger(DEFAULT_INITIAL_BALANCE));
		}
		final PV points = new PV(accounts.get(accountId).get(), tag);
		//final AtomicInteger points = new AtomicInteger(getPoints(accountId).getPoints());
		accounts.get(accountId).set(pointsToAdd);
		this.tag = new AtomicInteger(tag);
	}


}
