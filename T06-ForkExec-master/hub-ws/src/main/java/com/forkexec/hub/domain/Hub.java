package com.forkexec.hub.domain;

import java.util.*;
/**
 * Hub
 *
 * A restaurants hub server.
 *
 */
public class Hub {

	protected List<Cart> carts;
	// Singleton -------------------------------------------------------------

	/** Private constructor prevents instantiation from other classes. */
	private Hub() {
		// Initialization of default values
		this.carts = new ArrayList<Cart>();
	}

	/**
	 * SingletonHolder is loaded on the first execution of Singleton.getInstance()
	 * or the first access to SingletonHolder.INSTANCE, not before.
	 */
	private static class SingletonHolder {
		private static final Hub INSTANCE = new Hub();
	}

	public static synchronized Hub getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public Cart getCart(String userId){
		for(Cart c: carts){
			if(c.getUserId().equals(userId)){
				return c;
			}
		}
		return null;
	}

	public void addFoodToCart(String userId, FoodId foodId, int quant){
		for(Cart c: carts){
			if(c.getUserId().equals(userId)){
				c.addToCart(foodId, quant);
			}
		}
	}

	public void addCart(String userId){
		Cart c = new Cart(userId);
		this.carts.add(c);
	}

	public void removeCart(String userId){
		this.carts.remove(getCart(userId));
	}

	public void clearCart(String userId){
		for(Cart c: carts){
			if(c.getUserId().equals(userId)){
				c.clear();
			}
		}
	}

	public void clearAllCarts(){
		this.carts.clear();
	}

	public List<FoodCart> getFoodCarts(String userId){
		for(Cart c: carts){
			if(c.getUserId().equals(userId)){
				return c.getFoods();
			}
		}
		return null;
	}
	// TODO 
	
}
